package org.openflow.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.factory.OFMessageFactory;

public class OFMessageAsyncStream
  implements OFMessageInStream, OFMessageOutStream
{
  public static int defaultBufferSize = 65536;
  protected ByteBuffer inBuf;
  protected ByteBuffer outBuf;
  protected OFMessageFactory messageFactory;
  protected SocketChannel sock;
  protected int partialReadCount = 0;
  
  public OFMessageAsyncStream(SocketChannel sock, OFMessageFactory messageFactory)
    throws IOException
  {
    this.inBuf = ByteBuffer.allocateDirect(defaultBufferSize);
    
    this.outBuf = ByteBuffer.allocateDirect(defaultBufferSize);
    
    this.sock = sock;
    this.messageFactory = messageFactory;
    this.sock.configureBlocking(false);
  }
  
  public List<OFMessage> read()
    throws IOException
  {
    return read(0);
  }
  
  public List<OFMessage> read(int limit)
    throws IOException
  {
    int read = this.sock.read(this.inBuf);
    if (read == -1) {
      return null;
    }
    this.inBuf.flip();
    List<OFMessage> l =null;
    try{
       l= this.messageFactory.parseOFMessage(this.inBuf, limit);
    }
    catch(Exception e)
    {
    	e.printStackTrace();
    	throw new IOException();
    }
    if (this.inBuf.hasRemaining()) {
      this.inBuf.compact();
    } else {
      this.inBuf.clear();
    }
    return l;
  }
  
  protected void appendMessageToOutBuf(OFMessage m)
    throws IOException
  {
    int msglen = m.getLengthU();
    if (this.outBuf.remaining() < msglen) {
      throw new IOException("Message length exceeds buffer capacity: " + msglen);
    }
    m.writeTo(this.outBuf);
  }
  
  public void write(OFMessage m)
    throws IOException
  {
    appendMessageToOutBuf(m);
  }
  
  public void write(List<OFMessage> l)
    throws IOException
  {
    for (OFMessage m : l) {
      appendMessageToOutBuf(m);
    }
  }
  
  public void flush()
    throws IOException
  {
    this.outBuf.flip();
    this.sock.write(this.outBuf);
    this.outBuf.compact();
  }
  
  public boolean needsFlush()
  {
    return this.outBuf.position() > 0;
  }
  
  public OFMessageFactory getMessageFactory()
  {
    return this.messageFactory;
  }
  
  public void setMessageFactory(OFMessageFactory messageFactory)
  {
    this.messageFactory = messageFactory;
  }
}
