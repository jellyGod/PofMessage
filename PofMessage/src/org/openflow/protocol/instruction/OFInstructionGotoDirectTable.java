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

import org.openflow.protocol.OFMatch20;
import org.openflow.util.HexString;

/**
 * Goto next <B>direct</B> table.<br>
 * If next table is not direct table, use {@link OFInstructionGotoTable} instead.
 * @author Song Jian (jack.songjian@huawei.com), Huawei Technologies Co., Ltd.
 */
public class OFInstructionGotoDirectTable extends OFInstruction {
    public static final int MINIMUM_LENGTH = OFInstruction.MINIMUM_LENGTH + 8 +OFMatch20.MINIMUM_LENGTH;
    
    protected byte nextTableId;
    protected byte indexType;	//0: value; 1: field
    protected short packetOffset;       //byte
    protected int indexValue;
    protected OFMatch20 indexField;

    

    public OFInstructionGotoDirectTable(){
        super.setType(OFInstructionType.GOTO_DIRECT_TABLE);
        super.setLength((short) MINIMUM_LENGTH);
    }
    
    public byte getNextTableId()
    {
    	return nextTableId;
    }
    public void setNextTableId(byte nextTableId)
    {
    	this.nextTableId=nextTableId;
    }
    public byte getIndexType()
    {
    	return indexType;
    }
    public void setIndexType(byte indexType)
    {
    	this.indexType = indexType;
    }
    public short getPacketOffset()
    {
    	return packetOffset;
    }
    public void setPacketOffset(short packetOffset)
    {
    	this.packetOffset = packetOffset;
    }
    public int getIndexValue()
    {
    	return indexValue;
    }
    public void setIndexValue(int indexValue)
    {
    	this.indexValue = indexValue;
    }
    public OFMatch20 getIndexField()
    {
    	return this.indexField;
    }
    public void setIndexField(OFMatch20 indexField)
    {
    	this.indexField = indexField;
    }
    @Override
    public void readFrom(ByteBuffer data){
    	super.readFrom(data);
        nextTableId = data.get();
        indexType = data.get();
        packetOffset = data.getShort();
        data.get(new byte[4]);
        if(indexType == 0){
        	indexValue = data.getInt();
        	data.get(new byte[4]);
        	indexField = null;
        }else if(indexType == 1){
        	indexField = new OFMatch20();
        	indexField.readFrom(data);
        	this.indexValue = 0;
        }else{
        	indexValue = 0;
        	indexField = null;
        	data.get(new byte[OFMatch20.MINIMUM_LENGTH]);
        }

    }
    
 
    
    @Override
    public void writeTo(ByteBuffer data){
    	 super.writeTo(data);
         data.put(nextTableId);
         data.put(indexType);
         data.putShort(packetOffset);
         data.put(new byte[4]);
         if(indexType == 0){
         	data.putInt(indexValue);
         	data.put(new byte[4]);
         }else if(indexType == 1 && indexField != null){
         	indexField.writeTo(data);
         }else{
         	data.put(new byte[OFMatch20.MINIMUM_LENGTH]);
         }

    }
    

    
    @Override
    public String toString(){
        return super.toString() +
                ";nextTableId=" + nextTableId +
                ";indexType=" + indexType +
                ";packetOffset=" + packetOffset +
                ";indexValue=" + indexValue +
                ";indexField=" + indexField ;
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + nextTableId;
        result = prime * result + indexType;
        result = prime * result + packetOffset;
        result = prime * result + indexValue;
        result = prime * result + indexField.hashCode();
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
        OFInstructionGotoDirectTable other = (OFInstructionGotoDirectTable) obj;
        if (nextTableId != other.nextTableId)
            return false;
        if (indexType != other.indexType)
        	return false;
        if (packetOffset != other.packetOffset)
            return false;
        if (indexValue != other.indexValue)
        	return false;
        if (!indexField.equals(other.indexField))
        	return false;
        return true;
    }    
}
