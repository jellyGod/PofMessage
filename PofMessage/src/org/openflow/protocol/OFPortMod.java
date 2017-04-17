package org.openflow.protocol;

public class OFPortMod extends OFPortStatus{

	public OFPortMod()
	{
		super();
		this.type=OFType.PORT_MOD;
	}
}
