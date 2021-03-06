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

import org.openflow.util.HexString;
import org.openflow.util.U16;

/**
 * Represents an ofp_port_status message
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
public class OFPortStatus extends OFMessage {
    public static int MINIMUM_LENGTH = OFMessage.MINIMUM_LENGTH + 8 + OFPhysicalPort.MINIMUM_LENGTH;

    public enum OFPortReason {
        OFPPR_ADD,
        OFPPR_DELETE,
        OFPPR_MODIFY
    }

    protected byte reason;
    protected OFPhysicalPort desc;

    /**
     * @return the reason
     */
    public byte getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(byte reason) {
        this.reason = reason;
    }

    /**
     * @return the desc
     */
    public OFPhysicalPort getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(OFPhysicalPort desc) {
        this.desc = desc;
    }

    public OFPortStatus() {
        super();
        this.type = OFType.PORT_STATUS;
        this.length = U16.t(MINIMUM_LENGTH);
    }

    @Override
    public void readFrom(ByteBuffer data) {
        super.readFrom(data);
        this.reason = data.get();
        data.get(new byte[7]);
        
        if (this.desc == null)
            this.desc = new OFPhysicalPort();
        this.desc.readFrom(data);
    }

    @Override
    public void writeTo(ByteBuffer data) {
        super.writeTo(data);
        data.put(this.reason);
        data.put(new byte[7]);
        this.desc.writeTo(data);
    }
    

    @Override
    public String toBytesString(){
        return super.toBytesString()
                + HexString.toHex(reason)
                + HexString.ByteZeroEnd(7)
                + desc.toBytesString();
    }
    
    @Override
    public String toString(){
        return super.toString() + 
                "; PortStatus:" +
                "rea=" + reason + 
                desc.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 313;
        int result = super.hashCode();
        result = prime * result + ((desc == null) ? 0 : desc.hashCode());
        result = prime * result + reason;
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
        if (!(obj instanceof OFPortStatus)) {
            return false;
        }
        OFPortStatus other = (OFPortStatus) obj;
        if (desc == null) {
            if (other.desc != null) {
                return false;
            }
        } else if (!desc.equals(other.desc)) {
            return false;
        }
        if (reason != other.reason) {
            return false;
        }
        return true;
    }
    
    public void computeLength()
    {
    	this.length=(short) MINIMUM_LENGTH;
    }
}
