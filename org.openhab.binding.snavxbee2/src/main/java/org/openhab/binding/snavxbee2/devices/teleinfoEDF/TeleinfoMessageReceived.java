package org.openhab.binding.snavxbee2.devices.teleinfoEDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeleinfoMessageReceived {

    private Logger logger = LoggerFactory.getLogger(TeleinfoMessageReceived.class);
    private String messageID;
    private String value;
    private String checksum;
    private boolean ChecksumValid;

    public TeleinfoMessageReceived(String frame) {

        super();
        // logger.debug("ready to parse : {} ", frame);
        // this.messageID = frame;
        // this.thing = thing;

        String[] splitted_frame = frame.split(" ");
        if (splitted_frame.length == 3) {
            this.setMessageID(splitted_frame[0]);
            this.setValue(splitted_frame[1]);
            this.setChecksum(splitted_frame[2]);
            // logger.debug("message {} {} {} ", splitted_frame[0], splitted_frame[1], splitted_frame[2]);
        } else {

            this.messageID = splitted_frame[0];
            this.value = splitted_frame[1];
            this.setChecksum(" ");
            // logger.debug("error : {} ", splitted_frame.length);
        }

        // this.run();
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {

        this.checksum = checksum;

    }

    public boolean isChecksumValid() {
        return ChecksumValid;
    }

    public void setChecksumValid(boolean checksumValid) {
        ChecksumValid = checksumValid;
    }

    // @Override
    public void run() {
        // TODO Auto-generated method stub

        StringBuffer message = new StringBuffer(this.messageID + " " + this.value);

        // logger.debug("message is : {} {} ", message.substring(0, message.length()), this.checksum);

        int intsum = 0;

        for (int i = 0; i < (message.length()); i++) {
            int ascii = message.charAt(i);
            intsum += ascii;
        }

        // logger.debug("Integer sum is : {} interger checksum is : {} ", (int) checksum.charAt(0), (intsum & 63) + 32);

        if (checksum.charAt(0) == (intsum & 63) + 32) {
            // logger.debug("checksum valid");
            this.setChecksumValid(true);
        } else {
            // logger.debug("checksum error");
            this.setChecksumValid(false);
        }

        if (ChecksumValid) {

            logger.debug(" hello : {} : {} ", Thread.currentThread().getName(), this.getMessageID());
            // thing.getUID();
            // logger.debug(" hello1 : {} : {} ", thing.getUID(), this.getMessageID());
            // ChannelUID c = new ChannelUID(thing.getUID() + ":" + this.messageID);
            // thing.getHandler().handleCommand(c, new DecimalType(10));

        }

    }

}
