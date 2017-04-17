/**
*    Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior
*    University
* 
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package org.openflow.protocol;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.factory.OFActionFactory;
import org.openflow.protocol.factory.OFActionFactoryAware;
import org.openflow.util.U16;
import org.openflow.util.U8;

/**
 * Represents an ofp_packet_out message
 *
 * @author David Erickson (daviderickson@cs.stanford.edu) - Mar 12, 2010
 */
public class OFPacketOut extends OFMessage implements OFActionFactoryAware {
    public static int MINIMUM_LENGTH = 24;
    //这里仿照instruction的实现
    public static int MAXMUN_LENGTH=MINIMUM_LENGTH+OFGlobal.OFP_MAX_ACTION_NUMBER_PER_INSTRUCTION * OFAction.MAXIMAL_LENGTH
    		+OFGlobal.OFP_PACKET_IN_MAX_LENGTH;
    public static int BUFFER_ID_NONE = 0xffffffff;

    protected OFActionFactory actionFactory;
    protected int bufferId;
    protected int inPort;
    protected byte actionNum;
    protected int packetLen;
    protected List<OFAction> actionList;
    protected byte[] data;

    public OFPacketOut() {
        super();
        this.type = OFType.PACKET_OUT;
        this.length = U16.t(MINIMUM_LENGTH);
    }

    /**
     * Get buffer_id
     * @return bufferId
     */
    public int getBufferId() {
        return this.bufferId;
    }

    /**
     * Set buffer_id
     * @param bufferId
     */
    public OFPacketOut setBufferId(int bufferId) {
        this.bufferId = bufferId;
        return this;
    }

    /**
     * Returns the packet data
     * @return packetData
     */
    public byte[] getPacketData() {
        return this.data;
    }

    /**
     * Sets the packet data
     * @param packetData
     */
    public OFPacketOut setPacketData(byte[] packetData) {
        this.data = packetData;
        return this;
    }

    /**
     * Get in_port
     * @return inPort
     */
    public int getInPort() {
        return this.inPort;
    }

    /**
     * Set in_port
     * @param inPort
     */
    public OFPacketOut setInPort(int inPort) {
        this.inPort = inPort;
        return this;
    }

    /**
     * Set in_port. Convenience method using OFPort enum.
     * @param inPort
     */
    public OFPacketOut setInPort(OFPort inPort) {
        this.inPort = inPort.getValue();
        return this;
    }

    /**
     * Get actionsNum
     * @return actionsLength
     */
    public short getActionsNumU() {
        return U8.f(this.actionNum);
    }

    /**
     * Set actions_len
     * @param actionsLength
     */
    public OFPacketOut setActionsNum(short actionNum) {
        this.actionNum = U8.t(actionNum);
        return this;
    }
    
    public int getPacketLen()
    {
    	return this.packetLen;
    }
    
    public OFPacketOut setPacketLen(int packetLen)
    {
    	this.packetLen=packetLen;
    	return this;
    }

    /**
     * Returns the actions contained in this message
     * @return a list of ordered OFAction objects
     */
    public List<OFAction> getActions() {
        return this.actionList;
    }

    /**
     * Sets the list of actions on this message
     * @param actions a list of ordered OFAction objects
     */
    public OFPacketOut setActions(List<OFAction> actions) {
        this.actionList = actions;
        return this;
    }

    @Override
    public void setActionFactory(OFActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Override
    public void readFrom(ByteBuffer data) {
        super.readFrom(data);
        this.bufferId = data.getInt();
        this.inPort = data.getInt();
        this.actionNum=data.get();
        //just padding
        data.get(new byte[3]);
        this.packetLen=data.getInt();
        if ( this.actionFactory == null)
            throw new RuntimeException("ActionFactory not set");
        this.actionList = this.actionFactory.parseActions(data, OFGlobal.OFP_MAX_ACTION_NUMBER_PER_INSTRUCTION * OFAction.MAXIMAL_LENGTH);
        this.data = new byte[OFGlobal.OFP_PACKET_IN_MAX_LENGTH];
        data.get(this.data);
    }

    @Override
    public void writeTo(ByteBuffer data) {
        super.writeTo(data);
        data.putInt(bufferId);
        data.putInt(inPort);
        data.put(actionNum);
        data.put(new byte[3]);
        data.putInt(packetLen);
        //开始写list
        if(actionList == null){
            data.put(new byte[OFGlobal.OFP_MAX_ACTION_NUMBER_PER_INSTRUCTION * OFAction.MAXIMAL_LENGTH]);
        }else{
            OFAction action;
            if(actionNum > actionList.size()){
                throw new RuntimeException("actionNum " + actionNum + " > actionList.size()" + actionList.size());
            }
            int i;
            for(i = 0; i < actionNum && i < OFGlobal.OFP_MAX_ACTION_NUMBER_PER_INSTRUCTION; i++){
                action = actionList.get(i);
                if(action == null){
                    data.put(new byte[OFAction.MAXIMAL_LENGTH]);
                }else{
                    action.writeTo(data);
                    if(action.getLength() < OFAction.MAXIMAL_LENGTH ){
                        data.put(new byte[OFAction.MAXIMAL_LENGTH - action.getLength()]);
                    }
                }
            }
            if(i < OFGlobal.OFP_MAX_ACTION_NUMBER_PER_INSTRUCTION){
                data.put(new byte[(OFGlobal.OFP_MAX_ACTION_NUMBER_PER_INSTRUCTION - i) * OFAction.MAXIMAL_LENGTH]);
            }
        }
        if (this.data != null)
        {
        	if(this.data.length>OFGlobal.OFP_PACKET_IN_MAX_LENGTH)
        	{
        		data.put(this.data,0,OFGlobal.OFP_PACKET_IN_MAX_LENGTH);
        	}
        	else{
        		data.put(this.data);
        		data.put(new byte[OFGlobal.OFP_PACKET_IN_MAX_LENGTH-this.data.length]);
        	}
        }
        else{
        	data.put(new byte[OFGlobal.OFP_PACKET_IN_MAX_LENGTH]);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 293;
        int result = super.hashCode();
        result = prime * result + ((actionList == null) ? 0 : actionList.hashCode());
        result = prime * result + actionNum;
        result = prime * result + packetLen;
        result = prime * result + bufferId;
        result = prime * result + inPort;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof OFPacketOut)) {
            return false;
        }
        OFPacketOut other = (OFPacketOut) obj;
        if (actionList == null) {
            if (other.actionList != null) {
                return false;
            }
        } else if (!actionList.equals(other.actionList)) {
            return false;
        }
        if (actionNum != other.actionNum) {
            return false;
        }
        if (bufferId != other.bufferId) {
            return false;
        }
        if (inPort != other.inPort) {
            return false;
        }
        if (packetLen != other.packetLen)
        {
        	return false;
        }
        if (!Arrays.equals(data, other.data)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OFPacketOut [actionFactory=" + actionFactory + ", actions="
                + actionList + ", actionNum=" + actionNum + ", bufferId=0x"
                + Integer.toHexString(bufferId) + ", inPort=" + inPort + ",packetLen="+packetLen+", packetData="
                + Arrays.toString(data) + "]";
    }
    
    public void computeLength()
    {
    	this.length=(short)MAXMUN_LENGTH;
    }
}
