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
 * Counter
 * FIXME Could discard this action counter because flow entry and the group entry have counter already
 * 
 * @author Song Jian (jack.songjian@huawei.com), Huawei Technologies Co., Ltd.
 *
 */
public class OFActionCounter extends OFAction {
    public static int MINIMUM_LENGTH = OFAction.MINIMUM_LENGTH + 8;
    
    protected int counterId;
    
    public OFActionCounter(){
        super.setType(OFActionType.COUNTER);
        super.setLength((short) MINIMUM_LENGTH);        
    }
    
    public void readFrom(ByteBuffer data){
        super.readFrom(data);
        this.counterId = data.getInt();
        data.get(new byte[4]);
    }
    
    public void writeTo(ByteBuffer data){
        super.writeTo(data);
        data.putInt(counterId);
        data.put(new byte[4]);
    }
    
    
    public String toString(){
        return super.toString() +
                ";cid=" + counterId;
    }

    public int getCounterId() {
        return counterId;
    }

    public void setCounterId(int counterId) {
        this.counterId = counterId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + counterId;
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
        OFActionCounter other = (OFActionCounter) obj;
        if (counterId != other.counterId)
            return false;
        return true;
    }
}
