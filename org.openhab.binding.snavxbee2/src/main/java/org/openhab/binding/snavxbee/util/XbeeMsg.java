package org.openhab.binding.snavxbee.util;

public class XbeeMsg {

    public String xbeeCmd;

    public XbeeMsg() {
        super();
        // TODO Auto-generated constructor stub
    }

    public synchronized String getXbeeCmd() {
        return xbeeCmd;
    }

    public synchronized void setXbeeCmd(String xbeeCmd) {
        this.xbeeCmd = xbeeCmd;
    }

}
