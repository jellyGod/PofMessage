package org.openflow.io;

import java.io.IOException;
import java.util.List;
import org.openflow.protocol.OFMessage;

public abstract interface OFMessageOutStream
{
  public abstract void write(OFMessage paramOFMessage)
    throws IOException;
  
  public abstract void write(List<OFMessage> paramList)
    throws IOException;
  
  public abstract void flush()
    throws IOException;
  
  public abstract boolean needsFlush();
}
