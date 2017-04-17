package org.openflow.protocol.instruction;

import java.nio.ByteBuffer;

import org.openflow.protocol.OFMatch20;

public class OFInstructionMovePacketOffset extends OFInstruction{
	public static final int MINIMUM_LENGTH = OFInstruction.MINIMUM_LENGTH + 8 + OFMatch20.MINIMUM_LENGTH;
	protected byte direction;		//0: forward; 1: backward
	protected byte valueType;
	protected int move_value;
	protected OFMatch20 move_field;

	public OFInstructionMovePacketOffset(){
        super.setType(OFInstructionType.MOVE_PACKET_OFFSET);
        super.setLength((short) MINIMUM_LENGTH);
	}

	@Override
    public void readFrom(ByteBuffer data){
        super.readFrom(data);
        
        direction = data.get();
        valueType = data.get();
        data.get(new byte[6]);
        
        if(valueType == 0){
        	move_value = data.getInt();
        	data.get(new byte[4]);
        	move_field = null;
        }else if(valueType == 1){
        	move_value = 0;
        	move_field = new OFMatch20();
        	move_field.readFrom(data);
        }else{
        	move_value = 0;
        	move_field = null;
        	data.get(new byte[OFMatch20.MINIMUM_LENGTH]);
        }
    }
	
	@Override
    public void writeTo(ByteBuffer data){
    	super.writeTo(data);
    	
    	data.put(direction);
    	data.put(valueType);
    	data.put(new byte[6]);
    	
        if(valueType == 0){
        	data.putInt(move_value);
        	data.put(new byte[4]);
        }else if(valueType == 1 && move_field != null){
        	move_field.writeTo(data);
        }else{
        	data.put(new byte[OFMatch20.MINIMUM_LENGTH]);
        }
    }

	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte direction) {
		this.direction = direction;
	}

	public byte getValueType() {
		return valueType;
	}

	public void setValueType(byte valueType) {
		this.valueType = valueType;
	}

	public int getMove_value() {
		return move_value;
	}

	public void setMove_value(int move_value) {
		this.move_value = move_value;
	}

	public OFMatch20 getMove_field() {
		return move_field;
	}

	public void setMove_field(OFMatch20 move_field) {
		this.move_field = move_field;
	}

	@Override
	public String toString() {
		return 
				super.toString() + "," +
				"OFInstructionMovePacketOffset [direction=" + direction + ", valueType=" + valueType + ", move_value="
				+ move_value + ", move_field=" + move_field + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + direction;
		result = prime * result + ((move_field == null) ? 0 : move_field.hashCode());
		result = prime * result + move_value;
		result = prime * result + valueType;
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
		OFInstructionMovePacketOffset other = (OFInstructionMovePacketOffset) obj;
		if (direction != other.direction)
			return false;
		if (move_field == null) {
			if (other.move_field != null)
				return false;
		} else if (!move_field.equals(other.move_field))
			return false;
		if (move_value != other.move_value)
			return false;
		if (valueType != other.valueType)
			return false;
		return true;
	}

	

}
