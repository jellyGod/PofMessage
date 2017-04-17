/**
 * Copyright (c) 2012, 2013, Huawei Technologies Co., Ltd.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.openflow.protocol.instruction;

import java.nio.ByteBuffer;

import org.openflow.protocol.OFGlobal;
import org.openflow.util.HexString;

/**
 * Write metadata at {@link #metadataOffset} : {@link #writeLength} using {@link #value}
 * @author Song Jian (jack.songjian@huawei.com), Huawei Technologies Co., Ltd.
 */
public class OFInstructionWriteMetadata extends OFInstruction {
	//the length should be more elegant
    public static int MINIMUM_LENGTH = OFInstruction.MINIMUM_LENGTH + 8+OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE;
    
    protected short metadataOffset;     //bit
    protected short writeLength;        //bit
    protected byte[] value;
    
    public OFInstructionWriteMetadata(){
        super.setType(OFInstructionType.WRITE_METADATA);
        super.setLength((short)MINIMUM_LENGTH);
    }
    
    @Override
    public void readFrom(ByteBuffer data){
        super.readFrom(data);
        metadataOffset = data.getShort();
        writeLength = data.getShort();
        value=new byte[OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE];
        data.get(value);
        data.get(new byte[4]);
    }
    
    @Override
    public void writeTo(ByteBuffer data){
        super.writeTo(data);
        data.putShort(metadataOffset);
        data.putShort(writeLength);
        if(value==null)
        {
        	data.put(new byte[OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE]);
        }
        else{
        	if(value.length>OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE)
        		data.put(value,0,OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE);
        	else{
        		data.put(value);
        		data.put(new byte[OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE-value.length]);
        	}
        }
        data.put(new byte[4]);
    }
    
    
    @Override
    public String toString(){
        return super.toString() +
                ";mos=" + metadataOffset +
                ";wl=" + writeLength +
                ";val=" + value;
    }

    public short getMetadataOffset() {
        return metadataOffset;
    }

    public void setMetadataOffset(short metadataOffset) {
        this.metadataOffset = metadataOffset;
    }

    public short getWriteLength() {
        return writeLength;
    }

    public void setWriteLength(short writeLength) {
        this.writeLength = writeLength;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + writeLength;
        result = prime * result + metadataOffset;
        //计算一个value的取值哦
        int temp=0;
        for(byte t:value)
        {
        	temp+=t;
        }
        result = prime * result + temp;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        OFInstructionWriteMetadata other = (OFInstructionWriteMetadata) obj;
        if (writeLength != other.writeLength)
            return false;
        if (metadataOffset != other.metadataOffset)
            return false;
        if (value != other.value)
            return false;
        return true;
    }
}
