package org.openhab.binding.snavxbee2.devices.teleinfoEDF;

public class TeleinfoMsgInfo {

    public String clearMessage;
    public int length;
    public String unit;
    public String messageID;

    public TeleinfoMsgInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public TeleinfoMsgInfo(String clearMessage, String messageID, int length, String unit) {
        super();

        this.clearMessage = clearMessage;
        this.length = length;
        this.unit = unit;
        this.messageID = messageID;

    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getClearMessage() {
        return clearMessage;
    }

    public void setClearMessage(String clearMessage) {
        this.clearMessage = clearMessage;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
