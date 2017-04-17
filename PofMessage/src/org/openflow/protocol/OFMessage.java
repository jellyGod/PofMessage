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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.openflow.util.HexString;
import org.openflow.util.U16;
import org.openflow.util.U32;
import org.openflow.util.U8;



/**
 * The base class for all OpenFlow protocol messages. This class contains the
 * equivalent of the ofp_header which is present in all OpenFlow messages.
 *
 * @author David Erickson (daviderickson@cs.stanford.edu) - Feb 3, 2010
 * @author Rob Sherwood (rob.sherwood@stanford.edu) - Feb 3, 2010
 */
public class OFMessage {
    public final static byte OFP_VERSION = 0x04;
    public final static int MINIMUM_LENGTH = 8;
    
    protected byte version;
    protected OFType type;
    protected short length;
    protected int xid;
    

    
    public OFMessage() {
        this.version = OFP_VERSION;
    }
    
    

    /**
     * Get the length of this message
     *
     * @return length
     */
    public short getLength() {
        return length;
    }

    /**
     * Get the length of this message, unsigned
     *
     * @return length
     */
    public int getLengthU() {
        return U16.f(length);
    }

    /**
     * Set the length of this message
     *
     * @param length
     */
    public OFMessage setLength(short length) {
        this.length = length;
        return this;
    }

    /**
     * Set the length of this message, unsigned
     *
     * @param length
     */
    public OFMessage setLengthU(int length) {
        this.length = U16.t(length);
        return this;
    }

    /**
     * Get the type of this message
     *
     * @return type
     */
    public OFType getType() {
        return type;
    }

    /**
     * Set the type of this message
     *
     * @param type
     */
    public void setType(OFType type) {
        this.type = type;
    }

    /**
     * Get the OpenFlow version of this message
     *
     * @return version
     */
    public byte getVersion() {
        return version;
    }

    /**
     * Set the OpenFlow version of this message
     *
     * @param version
     */
    public void setVersion(byte version) {
        this.version = version;
    }

    /**
     * Get the transaction id of this message
     *
     * @return xid
     */
    public int getXid() {
        return xid;
    }

    /**
     * Set the transaction id of this message
     *
     * @param xid
     */
    public void setXid(int xid) {
        this.xid = xid;
    }

    /**
     * Read this message off the wire from the specified ByteBuffer
     * @param data
     */
    public void readFrom(ByteBuffer data) {
        this.version = data.get();
        this.type = OFType.valueOf(data.get());
        this.length = data.getShort();
        this.xid = data.getInt();
    }

    /**
     * Write this message's binary format to the specified ByteBuffer
     * @param data
     */
    public void writeTo(ByteBuffer data) {
    	 computeLength();
    	 data.put(version);
         data.put(type.getTypeValue());
         data.putShort(length);
         data.putInt(xid);
    }
    
    /**
     * This method is called during the writeTo method for serialization and
     * is expected to set the length of the message. If your class manually
     * sets the length you should override this to do nothing.
     *
     */
    public void computeLength() {
        this.length = (short) MINIMUM_LENGTH;
    }
    

    /**
     * Returns a summary of the message
     * @return "ofmsg=v=$version;t=$type:l=$len:xid=$xid"
     */
    public String toString() {
        return "ofmsg" +
            ":v=" + U8.f(this.getVersion()) +
            ";t=" + this.getType() +
            ";l=" + this.getLengthU() +
            ";x=" + U32.f(this.getXid());
    }
    
    /**
     * @return this message's hex format string
     */
    public String toBytesString(){
        String bytesString;
        bytesString = HexString.toHex( version );
        bytesString += HexString.toHex( type.getTypeValue() );
        bytesString += HexString.toHex( length );        
        bytesString += " ";
        
        bytesString += HexString.toHex( xid );
        
        return bytesString;
    }
    
    
    //add by qsmywxd  and remain to discuss
    /*public String toBytesString() {
		// TODO Auto-generated method stub
    	 String string = HexString.toHex(this.getVersion());
         string += HexString.toHex(this.getType().getTypeValue());
         string += HexString.toHex(this.getLengthU());
         string += HexString.toHex(this.getXid());         
         return string;
	}*/
    
    

    @Override
    public int hashCode() {
        final int prime = 97;
        int result = 1;
        result = prime * result + length;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + version;
        result = prime * result + xid;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OFMessage)) {
            return false;
        }
        OFMessage other = (OFMessage) obj;
        if (length != other.length) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (version != other.version) {
            return false;
        }
        if (xid != other.xid) {
            return false;
        }
        return true;
    }
    

}
