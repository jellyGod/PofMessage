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


/**
 * re-calculate the checksum start from {@link #calcStartPosition} with length {@link #calcLength},
 * and write the result to {@link #checksumPosition} with length {@link #checksumLength} 
 * 
 * @author Song Jian (jack.songjian@huawei.com), Huawei Technologies Co., Ltd.
 *
 */
public class OFActionCalculateCheckSum extends OFAction {
    public static int MINIMUM_LENGTH = OFAction.MINIMUM_LENGTH + 16;
    protected byte  checksumPosType; //0:packet 1:metadata
    protected byte  calcPosType;//0:packet 1:metadata
    protected short checksumPosition;   //bit
    protected short checksumLength;     //bit
    protected short calcStartPosition;  //bit
    protected short calcLength;         //bit
    
    public OFActionCalculateCheckSum(){
        super.setType(OFActionType.CALCULATE_CHECKSUM);
        super.setLength((short) MINIMUM_LENGTH);
    }
    
    public void readFrom(ByteBuffer data){
        super.readFrom(data);
        checksumPosType = data.get();
        calcPosType = data.get();
        checksumPosition = data.getShort();
        checksumLength = data.getShort();
        calcStartPosition = data.getShort();
        calcLength = data.getShort();    
        data.get(new byte[6]);
    }
    
    public void writeTo(ByteBuffer data){
        super.writeTo(data);
        data.put(checksumPosType);
        data.put(calcPosType);
        data.putShort(checksumPosition);
        data.putShort(checksumLength);
        data.putShort(calcStartPosition);
        data.putShort(calcLength);
        data.put(new byte[6]);
    }
    

    
    public String toString(){
        return super.toString() +
        		";checksumPosType=" + checksumPosType +
        		";calcPosType=" + calcPosType +
                ";ckpos=" + checksumPosition +
                ";cklen=" + checksumLength +
                ";clpos=" + calcStartPosition +
                ";cllen=" + calcLength;
    }
    public byte getChecksumPosType()
    {
    	return checksumPosType;
    }
    public void setChecksumPosType(byte checksumPosType)
    {
    	this.checksumPosType=checksumPosType;
    }
    public byte getCalcPosType()
    {
    	return calcPosType;
    }
    public void setCalcPosType(byte calcPosType)
    {
    	this.calcPosType=calcPosType;
    }
    public short getChecksumPosition() {
        return checksumPosition;
    }

    public void setChecksumPosition(short checksumPosition) {
        this.checksumPosition = checksumPosition;
    }

    public short getChecksumLength() {
        return checksumLength;
    }

    public void setChecksumLength(short checksumLength) {
        this.checksumLength = checksumLength;
    }

    public short getCalcStartPosition() {
        return calcStartPosition;
    }

    public void setCalcStartPosition(short calcStartPosition) {
        this.calcStartPosition = calcStartPosition;
    }

    public short getCalcLength() {
        return calcLength;
    }

    public void setCalcLength(short calcLength) {
        this.calcLength = calcLength;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + checksumPosType;
        result = prime * result + calcPosType;
        result = prime * result + calcLength;
        result = prime * result + calcStartPosition;
        result = prime * result + checksumLength;
        result = prime * result + checksumPosition;
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
        OFActionCalculateCheckSum other = (OFActionCalculateCheckSum) obj;
        if (checksumPosType != other.checksumPosType)
        	return false;
        if (calcPosType != other.calcPosType)
        	return false;
        if (calcLength != other.calcLength)
            return false;
        if (calcStartPosition != other.calcStartPosition)
            return false;
        if (checksumLength != other.checksumLength)
            return false;
        if (checksumPosition != other.checksumPosition)
            return false;
        return true;
    }
    
    
}
