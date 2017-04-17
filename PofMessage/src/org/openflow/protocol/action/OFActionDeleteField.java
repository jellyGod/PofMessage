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

import org.openflow.protocol.OFMatch20;



/**
 * Delete a field with field position and length.
 * 
 * @author Song Jian (jack.songjian@huawei.com), Huawei Technologies Co., Ltd.
 *
 */
public class OFActionDeleteField extends OFAction {
    public static int MINIMUM_LENGTH = OFAction.MINIMUM_LENGTH + 8 +OFMatch20.MINIMUM_LENGTH;
    
    //variable
    protected short tagPosition;
    protected byte tagLengthValueType;
    protected int tagLengthValue;
    protected OFMatch20 tagLengthField;
    
    
    public OFActionDeleteField(){
        super.setType(OFActionType.DELETE_FIELD);
        super.setLength((short) MINIMUM_LENGTH);
    }
    
    public void readFrom(ByteBuffer data){
    	 super.readFrom(data);
         this.tagPosition = data.getShort();
         this.tagLengthValueType = data.get();
         data.get(new byte[5]);
         if(tagLengthValueType == 0){
         	tagLengthValue = data.getInt();
         	data.get(new byte[4]);
         	tagLengthField = null;
         }else if(tagLengthValueType == 1){
         	tagLengthValue = 0;
         	tagLengthField = new OFMatch20();
         	tagLengthField.readFrom(data);
         }else{
         	tagLengthValue = 0;
         	tagLengthField = null;
         	data.get(new byte[OFMatch20.MINIMUM_LENGTH]);
         }

    }
    
    public void writeTo(ByteBuffer data){
    	 super.writeTo(data);
         data.putShort(tagPosition);
         data.put(tagLengthValueType);
         data.put(new byte[5]);
         if(tagLengthValueType == 0){
         	data.putInt(tagLengthValue);
         	data.put(new byte[4]);
         }else if(tagLengthValueType == 1 && tagLengthField != null){
         	tagLengthField.writeTo(data);
         }else{
         	data.put(new byte[OFMatch20.MINIMUM_LENGTH]);
         }

    }
    

    
    public String toString(){
        return super.toString() +
              ";tagPosition=" + tagPosition +
              ";tagLengthValueType=" + tagLengthValueType +
              ";tagLengthValue=" + tagLengthValue +
              ";tagLengthField=" + tagLengthField;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + tagPosition;
        result = prime * result + tagLengthValueType;
        result = prime * result + tagLengthValue;
        result = prime * result + tagLengthField.hashCode();
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
        OFActionDeleteField other = (OFActionDeleteField) obj;
        if(tagPosition != other.tagPosition)
        	return false;
        if(tagLengthValueType != other.tagLengthValueType)
        	return false;
        if(tagLengthValue != other.tagLengthValue)
        	return false;
        if(!tagLengthField.equals(other.tagLengthField))
        	return false;
        return true;
    }
    
    
}
