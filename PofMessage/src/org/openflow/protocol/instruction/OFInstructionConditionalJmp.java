package org.openflow.protocol.instruction;

import java.nio.ByteBuffer;

import org.openflow.protocol.OFMatch20;
import org.openflow.protocol.OFMessage;

public class OFInstructionConditionalJmp extends OFInstruction{
	public static final int MINIMUM_LENGTH = OFMessage.MINIMUM_LENGTH + 8 + 2 * OFMatch20.MINIMUM_LENGTH + 3 * OFMatch20.MINIMUM_LENGTH;

	protected byte field2_valueType;			//compare field2, 0 means to use field2_value, 1 means to use field2
	protected byte offset1_direction;	//jump direction. 0: forward; 1:backward
	protected byte offset1_valueType;
	protected byte offset2_direction;
	protected byte offset2_valueType;		//0 means to use value, 1 means to use field
	protected byte offset3_direction;
	protected byte offset3_valueType;
	
	protected OFMatch20 field1;			//compare field1 must be OFMatch20
	
	protected int field2_value;
	protected OFMatch20 field2;
	
	protected int offset1_value;		//if f1 < f2, jump ins number
	protected OFMatch20 offset1_field;
	
	protected int offset2_value;		//if f1 == f2, jump ins number
	protected OFMatch20 offset2_field;
	
	protected int offset3_value;		//if f2 > f2, jump ins number
	protected OFMatch20 offset3_field;

	public OFInstructionConditionalJmp()
	{
		super.setType(OFInstructionType.CONDITIONAL_JMP);
		super.setLength((short)MINIMUM_LENGTH);
	}
	
	@Override
    public void readFrom(ByteBuffer data){
        super.readFrom(data);
        
        field2_valueType = data.get();
        offset1_direction = data.get();
        offset1_valueType = data.get();
        offset2_direction = data.get();
        offset2_valueType = data.get();
        offset3_direction = data.get();
        offset3_valueType = data.get();
        data.get(1);
        
     
        field1 = new OFMatch20();
        field1.readFrom(data);
        
        if(field2_valueType == 0){
        	field2_value = data.getInt();
        	data.get(new byte[4]);
        	field2 = null;
        }else if(field2_valueType == 1){
        	field2_value = 0;
        	field2 = new OFMatch20();
        	field2.readFrom(data);
        }else{
        	field2_value = 0;
        	field2 = null;
        	data.get(new byte[OFMatch20.MINIMUM_LENGTH]);
        }
        
        if(offset1_valueType == 0){
        	offset1_value = data.getInt();
        	data.get(new byte[4]);
        	offset1_field = null;
        }else if(offset1_valueType == 1){
        	offset1_value = 0;
        	offset1_field = new OFMatch20();
        	offset1_field.readFrom(data);
        }else{
        	offset1_value = 0;
        	offset1_field = null;
        	data.get(new byte[OFMatch20.MINIMUM_LENGTH]);
        }
        
        if(offset2_valueType == 0){
        	offset2_value = data.getInt();
        	data.get(new byte[4]);
        	offset2_field = null;
        }else if(offset2_valueType == 1){
        	offset2_value = 0;
        	offset2_field = new OFMatch20();
        	offset2_field.readFrom(data);
        }else{
        	offset2_value = 0;
        	offset2_field = null;
        	data.get(new byte[OFMatch20.MINIMUM_LENGTH]);
        }
        
        if(offset3_valueType == 0){
        	offset3_value = data.getInt();
        	data.get(new byte[4]);
        	offset3_field = null;
        }else if(offset3_valueType == 1){
        	offset3_value = 0;
        	offset3_field = new OFMatch20();
        	offset3_field.readFrom(data);
        }else{
        	offset3_value = 0;
        	offset3_field = null;
        	data.get(new byte[OFMatch20.MINIMUM_LENGTH]);
        }
     }
	
	@Override
    public void writeTo(ByteBuffer data){
    	super.writeTo(data);
    	
    	data.put(field2_valueType);
    	data.put(offset1_direction);
    	data.put(offset1_valueType);
    	data.put(offset2_direction);
    	data.put(offset2_valueType);
    	data.put(offset3_direction);
    	data.put(offset3_valueType);
    	data.put((byte) 0);
    	
    	if(field1 != null){
    		field1.writeTo(data);
    	}else{
    		data.put(new byte[OFMatch20.MINIMUM_LENGTH]);
    	}
    	
        if(field2_valueType == 0){
        	data.putInt(field2_value);
        	data.put(new byte[4]);
        }else if(field2_valueType == 1 && field2 != null){
        	field2.writeTo(data);
        }else{
        	data.put(new byte[OFMatch20.MINIMUM_LENGTH]);
        }
    	
        if(offset1_valueType == 0){
        	data.putInt(offset1_value);
        	data.put(new byte[4]);
        }else if(offset1_valueType == 1 && offset1_field != null){
        	offset1_field.writeTo(data);
        }else{
        	data.put(new byte[OFMatch20.MINIMUM_LENGTH]);
        }
        
        if(offset2_valueType == 0){
        	data.putInt(offset2_value);
        	data.put(new byte[4]);
        }else if(offset2_valueType == 1 && offset2_field != null){
        	offset2_field.writeTo(data);
        }else{
        	data.put(new byte[OFMatch20.MINIMUM_LENGTH]);
        }
        
        if(offset3_valueType == 0){
        	data.putInt(offset3_value);
        	data.put(new byte[4]);
        }else if(offset3_valueType == 1 && offset3_field != null){
        	offset3_field.writeTo(data);
        }else{
        	data.put(new byte[OFMatch20.MINIMUM_LENGTH]);
        }
    }

	public byte getField2_valueType() {
		return field2_valueType;
	}

	public void setField2_valueType(byte field2_valueType) {
		this.field2_valueType = field2_valueType;
	}

	public byte getOffset1_direction() {
		return offset1_direction;
	}

	public void setOffset1_direction(byte offset1_direction) {
		this.offset1_direction = offset1_direction;
	}

	public byte getOffset1_valueType() {
		return offset1_valueType;
	}

	public void setOffset1_valueType(byte offset1_valueType) {
		this.offset1_valueType = offset1_valueType;
	}

	public byte getOffset2_direction() {
		return offset2_direction;
	}

	public void setOffset2_direction(byte offset2_direction) {
		this.offset2_direction = offset2_direction;
	}

	public byte getOffset2_valueType() {
		return offset2_valueType;
	}

	public void setOffset2_valueType(byte offset2_valueType) {
		this.offset2_valueType = offset2_valueType;
	}

	public byte getOffset3_direction() {
		return offset3_direction;
	}

	public void setOffset3_direction(byte offset3_direction) {
		this.offset3_direction = offset3_direction;
	}

	public byte getOffset3_valueType() {
		return offset3_valueType;
	}

	public void setOffset3_valueType(byte offset3_valueType) {
		this.offset3_valueType = offset3_valueType;
	}

	public OFMatch20 getField1() {
		return field1;
	}

	public void setField1(OFMatch20 field1) {
		this.field1 = field1;
	}

	public int getField2_value() {
		return field2_value;
	}

	public void setField2_value(int field2_value) {
		this.field2_value = field2_value;
	}

	public OFMatch20 getField2() {
		return field2;
	}

	public void setField2(OFMatch20 field2) {
		this.field2 = field2;
	}

	public int getOffset1_value() {
		return offset1_value;
	}

	public void setOffset1_value(int offset1_value) {
		this.offset1_value = offset1_value;
	}

	public OFMatch20 getOffset1_field() {
		return offset1_field;
	}

	public void setOffset1_field(OFMatch20 offset1_field) {
		this.offset1_field = offset1_field;
	}

	public int getOffset2_value() {
		return offset2_value;
	}

	public void setOffset2_value(int offset2_value) {
		this.offset2_value = offset2_value;
	}

	public OFMatch20 getOffset2_field() {
		return offset2_field;
	}

	public void setOffset2_field(OFMatch20 offset2_field) {
		this.offset2_field = offset2_field;
	}

	public int getOffset3_value() {
		return offset3_value;
	}

	public void setOffset3_value(int offset3_value) {
		this.offset3_value = offset3_value;
	}

	public OFMatch20 getOffset3_field() {
		return offset3_field;
	}

	public void setOffset3_field(OFMatch20 offset3_field) {
		this.offset3_field = offset3_field;
	}

    

	@Override
	public String toString() {
		return 
				super.toString() + "," +
				"OFInstructionConditionalJmp [field2_valueType=" + field2_valueType + ", offset1_direction="
				+ offset1_direction + ", offset1_valueType=" + offset1_valueType + ", offset2_direction="
				+ offset2_direction + ", offset2_valueType=" + offset2_valueType + ", offset3_direction="
				+ offset3_direction + ", offset3_valueType=" + offset3_valueType + ", field1=" + field1
				+ ", field2_value=" + field2_value + ", field2=" + field2 + ", offset1_value=" + offset1_value
				+ ", offset1_field=" + offset1_field + ", offset2_value=" + offset2_value + ", offset2_field="
				+ offset2_field + ", offset3_value=" + offset3_value + ", offset3_field=" + offset3_field+
				"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((field1 == null) ? 0 : field1.hashCode());
		result = prime * result + ((field2 == null) ? 0 : field2.hashCode());
		result = prime * result + field2_value;
		result = prime * result + field2_valueType;
		result = prime * result + offset1_direction;
		result = prime * result + ((offset1_field == null) ? 0 : offset1_field.hashCode());
		result = prime * result + offset1_value;
		result = prime * result + offset1_valueType;
		result = prime * result + offset2_direction;
		result = prime * result + ((offset2_field == null) ? 0 : offset2_field.hashCode());
		result = prime * result + offset2_value;
		result = prime * result + offset2_valueType;
		result = prime * result + offset3_direction;
		result = prime * result + ((offset3_field == null) ? 0 : offset3_field.hashCode());
		result = prime * result + offset3_value;
		result = prime * result + offset3_valueType;
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
		OFInstructionConditionalJmp other = (OFInstructionConditionalJmp) obj;
		if (field1 == null) {
			if (other.field1 != null)
				return false;
		} else if (!field1.equals(other.field1))
			return false;
		if (field2 == null) {
			if (other.field2 != null)
				return false;
		} else if (!field2.equals(other.field2))
			return false;
		if (field2_value != other.field2_value)
			return false;
		if (field2_valueType != other.field2_valueType)
			return false;
		if (offset1_direction != other.offset1_direction)
			return false;
		if (offset1_field == null) {
			if (other.offset1_field != null)
				return false;
		} else if (!offset1_field.equals(other.offset1_field))
			return false;
		if (offset1_value != other.offset1_value)
			return false;
		if (offset1_valueType != other.offset1_valueType)
			return false;
		if (offset2_direction != other.offset2_direction)
			return false;
		if (offset2_field == null) {
			if (other.offset2_field != null)
				return false;
		} else if (!offset2_field.equals(other.offset2_field))
			return false;
		if (offset2_value != other.offset2_value)
			return false;
		if (offset2_valueType != other.offset2_valueType)
			return false;
		if (offset3_direction != other.offset3_direction)
			return false;
		if (offset3_field == null) {
			if (other.offset3_field != null)
				return false;
		} else if (!offset3_field.equals(other.offset3_field))
			return false;
		if (offset3_value != other.offset3_value)
			return false;
		if (offset3_valueType != other.offset3_valueType)
			return false;
		return true;
	}

	
	

}
