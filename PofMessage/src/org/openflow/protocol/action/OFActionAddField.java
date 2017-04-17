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

package org.openflow.protocol.action;

import java.nio.ByteBuffer;

import org.openflow.protocol.OFGlobal;



/**
 * Add a field at the start {@link #fieldPosition} and length {@link #fieldLength},  with value {@link #fieldValue}.
 * 
 * @author Song Jian (jack.songjian@huawei.com), Huawei Technologies Co., Ltd.
 *
 */
public class OFActionAddField extends OFAction {
    public static int MINIMUM_LENGTH = OFAction.MINIMUM_LENGTH + 8 +OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE;
    
    protected short fieldId;
    protected short fieldPosition;  //bit
    protected int fieldLength;      //bit
    
    protected byte[] fieldValue;
    
    public OFActionAddField(){
        super.setType(OFActionType.ADD_FIELD);
        super.setLength((short) MINIMUM_LENGTH);
    }
    
    public void readFrom(ByteBuffer data){
        super.readFrom(data);
        this.fieldId = data.getShort();
        this.fieldPosition = data.getShort();
        this.fieldLength = data.getInt();
        this.fieldValue=new byte[OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE];
        data.get(fieldValue);
    }
    
    public void writeTo(ByteBuffer data){
        super.writeTo(data);
        data.putShort(fieldId);
        data.putShort(fieldPosition);
        data.putInt(fieldLength);
        if(fieldValue == null){
            data.put(new byte[OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE]);
        }else{
            if(fieldValue.length > OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE){
                data.put(fieldValue, 0, OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE);
            }else{
                data.put(fieldValue);
                data.put(new byte[OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE - fieldValue.length]);                
            }
        }

    }
    

    
    public String toString(){
        return super.toString() +
                ";fid=" + fieldId +
                ";fpos=" + fieldPosition +
                ";flen=" + fieldLength +
                ";fval=" + fieldValue;
    }

    public short getFieldId() {
        return fieldId;
    }

    public void setFieldId(short fieldId) {
        this.fieldId = fieldId;
    }

    public short getFieldPosition() {
        return fieldPosition;
    }

    public void setFieldPosition(short fieldPosition) {
        this.fieldPosition = fieldPosition;
    }

    public int getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(int fieldLength) {
        this.fieldLength = fieldLength;
    }

    public byte[] getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(byte[] fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + fieldId;
        result = prime * result + fieldLength;
        result = prime * result + fieldPosition;
        int temp=0;
        for(byte t:fieldValue)
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
        OFActionAddField other = (OFActionAddField) obj;
        if (fieldId != other.fieldId)
            return false;
        if (fieldLength != other.fieldLength)
            return false;
        if (fieldPosition != other.fieldPosition)
            return false;
        if (fieldValue.length != other.fieldValue.length)
        	return false;
        for(int i=0;i<fieldValue.length;i++)
        {
        	if(fieldValue[i]!=other.fieldValue[i])
        		return false;
        }
        return true;
    }
    
    
}
