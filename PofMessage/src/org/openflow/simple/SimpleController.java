/**
 *
 */
package org.openflow.simple;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openflow.example.cli.Options;
import org.openflow.example.cli.ParseException;
import org.openflow.example.cli.SimpleCLI;
import org.openflow.io.OFMessageAsyncStream;
import org.openflow.protocol.OFEchoReply;
import org.openflow.protocol.OFFeaturesReply;
import org.openflow.protocol.OFFeaturesRequest;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFGetConfigReply;
import org.openflow.protocol.OFGetConfigRequest;
import org.openflow.protocol.OFHello;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFSetConfig;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.factory.BasicFactory;
import org.openflow.util.LRULinkedHashMap;
import org.openflow.util.U16;

/**
 * @author littlejelly
 * start a simple controller to make some small test
 */
public class SimpleController implements SelectListener {
    protected ExecutorService es;
    protected BasicFactory factory;
    protected SelectLoop listenSelectLoop;
    protected ServerSocketChannel listenSock;
    protected List<SelectLoop> switchSelectLoops;
    protected Map<SocketChannel,OFSwitch> switchSockets;
    protected Integer threadCount;
    protected int port;

    protected class OFSwitch {
        protected SocketChannel sock;
        protected OFMessageAsyncStream stream;
        protected Map<Integer, Short> macTable =
            new LRULinkedHashMap<Integer, Short>(64001, 64000);
        protected int portNum;
        protected int receNum;
        public OFSwitch(SocketChannel sock, OFMessageAsyncStream stream) {
            this.sock = sock;
            this.stream = stream;
        }

        public void handlePacketIn(OFPacketIn pi) {
           //show the packetIn message
        	System.out.println("receive an OFPacketIN:");
        	System.out.println(pi);
        	System.out.println("OFPacketIN over");
        	
        }

        public String toString() {
            InetAddress remote = sock.socket().getInetAddress();
            return remote.getHostAddress() + ":" + sock.socket().getPort();
        }

        public OFMessageAsyncStream getStream() {
            return stream;
        }
    }

    public SimpleController(int port) throws IOException{
        listenSock = ServerSocketChannel.open();
        listenSock.configureBlocking(false);
        listenSock.socket().bind(new java.net.InetSocketAddress(port));
        listenSock.socket().setReuseAddress(true);
        this.port = port;
        switchSelectLoops = new ArrayList<SelectLoop>();
        switchSockets = new ConcurrentHashMap<SocketChannel,OFSwitch>();
        threadCount = 1;
        listenSelectLoop = new SelectLoop(this);
        // register this connection for accepting
        listenSelectLoop.register(listenSock, SelectionKey.OP_ACCEPT, listenSock);

        this.factory = new BasicFactory();
    }

    @Override
    public void handleEvent(SelectionKey key, Object arg) throws IOException {
        if (arg instanceof ServerSocketChannel)
            handleListenEvent(key, (ServerSocketChannel)arg);
        else
            handleSwitchEvent(key, (SocketChannel) arg);
    }

    protected void handleListenEvent(SelectionKey key, ServerSocketChannel ssc)
            throws IOException {
        SocketChannel sock = listenSock.accept();
        OFMessageAsyncStream stream = new OFMessageAsyncStream(sock, factory);
        switchSockets.put(sock, new OFSwitch(sock, stream));
        System.err.println("Got new connection from " + switchSockets.get(sock));
        int ops = SelectionKey.OP_READ;
        if (stream.needsFlush())
            ops |= SelectionKey.OP_WRITE;

        // hash this switch into a thread
        SelectLoop sl = switchSelectLoops.get(sock.hashCode()
                % switchSelectLoops.size());
        sl.register(sock, ops, sock);
        // force select to return and re-enter using the new set of keys
        sl.wakeup();
    }

    protected void handleSwitchEvent(SelectionKey key, SocketChannel sock) {
        OFSwitch sw = switchSockets.get(sock);
        OFMessageAsyncStream stream = sw.getStream();
        try {
            if (key.isReadable()) {
                List<OFMessage> msgs = stream.read();
              
                if (msgs == null) {
                	System.out.println("yes i am null!");
                    key.cancel();
                    switchSockets.remove(sock);
                    return;
                }
                //System.out.println("get msgs and list----------:"+msgs.size());
                for (OFMessage m : msgs) {
                    switch (m.getType()) {
                        //first recieve a hello
                        //then send a hello and a feature request
                        case HELLO:
                           System.out.println("get:"+m);
                           OFHello ofhello=(OFHello) stream.getMessageFactory().getOFMessage(OFType.HELLO);
                           stream.write(ofhello);
                           System.out.println("send:"+ofhello);
                           OFFeaturesRequest ofrequest=(OFFeaturesRequest) stream.getMessageFactory().getOFMessage(OFType.FEATURES_REQUEST);
                           ofrequest.setXid(m.getXid()+1);
                           stream.write(ofrequest);
                           System.out.println("send:"+ofrequest);
                           break;
                        //receive features_replay 
                        //then send pof_set_config
                        //then send get_config_request
                        case FEATURES_REPLY:
                           OFFeaturesReply ofr=(OFFeaturesReply)m;
                           sw.portNum=ofr.getPortNum();
                           System.out.println("get:"+m);
                           OFSetConfig setconfig=(OFSetConfig) stream.getMessageFactory().getOFMessage(OFType.SET_CONFIG);
                           setconfig.setXid(m.getXid());
                           stream.write(setconfig);
                           System.out.println("send:"+setconfig);
                           OFGetConfigRequest getconfigrequest=(OFGetConfigRequest)stream.getMessageFactory().getOFMessage(OFType.GET_CONFIG_REQUEST);
                           stream.write(getconfigrequest);
                           System.out.println("send:"+getconfigrequest);
                           break;
                        //get resource report
                        case GET_CONFIG_REPLY:
                           System.out.println("get:"+m);
                           break;
                        case RESOURCE_REPORT:
                           System.out.println("get:"+m);
                           break;
                        case PORT_STATUS:
                           sw.receNum++;
                           System.out.println("get:"+m);
                           if(sw.receNum==sw.portNum)
                           {
                        	   //start a thread for test
                        	   new MessageTestThread(stream).start();
                           }
                           break;
                        case PACKET_IN:
                           System.out.println("get:"+m);
                           break;
                        case ECHO_REQUEST:
                           OFEchoReply reply = (OFEchoReply) stream.getMessageFactory().getOFMessage(OFType.ECHO_REPLY);
                           reply.setXid(m.getXid());
                           stream.write(reply);
                           break;
                        default:
                            System.err.println("Unhandled OF message: "
                                    + m.getType() + " from "
                                    + sock.socket().getInetAddress());
                    }
                }
            }
            if (key.isWritable()) {
                stream.flush();
            }
            
            
            
            /**
             * Only register for interest in R OR W, not both, causes stream
             * deadlock after some period of time
             */
            if (stream.needsFlush())
                key.interestOps(SelectionKey.OP_WRITE);
            else
                key.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            // if we have an exception, disconnect the switch
        	e.printStackTrace();
            key.cancel();
            switchSockets.remove(sock);
        }
    }

    public void run() throws IOException{
        System.err.println("Starting " + this.getClass().getCanonicalName() + 
                " on port " + this.port + " with " + this.threadCount + " threads");
        // Static number of threads equal to processor cores
        es = Executors.newFixedThreadPool(threadCount);

        // Launch one select loop per threadCount and start running
        for (int i = 0; i < threadCount; ++i) {
            final SelectLoop sl = new SelectLoop(this);
            switchSelectLoops.add(sl);
            es.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sl.doLoop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }}
            );
        }

        // Start the listen loop
        listenSelectLoop.doLoop();
    }

    public static void main(String [] args) throws IOException {
        SimpleCLI cmd = parseArgs(args);
        int port = Integer.valueOf(cmd.getOptionValue("p"));
        SimpleController sc = new SimpleController(port);
        sc.threadCount = Integer.valueOf(cmd.getOptionValue("t"));
        sc.run();
    }

    public static SimpleCLI parseArgs(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", "print help");
        // unused?
        // options.addOption("n", true, "the number of packets to send");
        options.addOption("p", "port", 9999, "the port to listen on");
        options.addOption("t", "threads", 1, "the number of threads to run");
        try {
            SimpleCLI cmd = SimpleCLI.parse(options, args);
            if (cmd.hasOption("h")) {
                printUsage(options);
                System.exit(0);
            }
            return cmd;
        } catch (ParseException e) {
            System.err.println(e);
            printUsage(options);
        }

        System.exit(-1);
        return null;
    }

    public static void printUsage(Options options) {
        SimpleCLI.printHelp("Usage: "
                + SimpleController.class.getCanonicalName() + " [options]",
                options);
    }
}
