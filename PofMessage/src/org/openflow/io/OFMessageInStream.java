package org.openflow.io;

import java.io.IOException;
import java.util.List;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.factory.OFMessageFactory;

public abstract interface OFMessageInStream
{
  public abstract List<OFMessage> read()
    throws IOException;
  
  public abstract List<OFMessage> read(int paramInt)
    throws IOException;
  
  public abstract void setMessageFactory(OFMessageFactory paramOFMessageFactory);
  
  public abstract OFMessageFactory getMessageFactory();
}
