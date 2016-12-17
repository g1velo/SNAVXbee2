package org.openhab.binding.snavxbee2.utils;

import org.openhab.binding.snavxbee2.handler.SNAVXbee2Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

public class Tosr0xTMsg {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2Handler.class);

    // private Tostemperature;
    private String temperature;
    private XBeeMessage payload;

    // private static String allRelay = "d";
    private boolean relay1;
    private boolean relay2;
    private boolean relay3;
    private boolean relay4;
    private boolean relay5;
    private boolean relay6;
    private boolean relay7;
    private boolean relay8;

    public Tosr0xTMsg(XBeeMessage payload) {
        super();
        this.payload = payload;

        if (payload.getData().length == 7) {

            logger.info(" The temperature is : " + new String(payload.getData()));
            logger.debug("building the thing to update " + payload.getDevice().get64BitAddress());

        }

        if (payload.getData().length == 2) {
            // MSB + LSB temp = ( MSB*256 + LSB ) /16
            logger.info(" The temperature is in HEXA : "
                    + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(payload.getData())));

            Byte msb = payload.getData()[0];
            Byte lsb = payload.getData()[1];

            float temperature = (msb.floatValue() * 256 + lsb.floatValue()) / 16;
            logger.info(" The temperature is in Float : " + temperature);
            logger.info(" The temperature msb is in Float : " + msb.floatValue());
            logger.info(" The temperature lsb is in Float : " + lsb.floatValue());

            // updateState(new ChannelUID(getThing().getUID(), TEMPERATURE), temperature);

        }

        if (payload.getData().length == 1) {
            logger.info(" This is the relay State : "
                    + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(payload.getData())));
        }

    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public boolean isRelay1() {
        return relay1;
    }

    private void setRelay1(boolean relay1) {
        this.relay1 = relay1;
    }

    public boolean isRelay2() {
        return relay2;
    }

    private void setRelay2(boolean relay2) {
        this.relay2 = relay2;
    }

    public boolean isRelay3() {
        return relay3;
    }

    private void setRelay3(boolean relay3) {
        this.relay3 = relay3;
    }

    public boolean isRelay4() {
        return relay4;
    }

    private void setRelay4(boolean relay4) {
        this.relay4 = relay4;
    }

    public boolean isRelay5() {
        return relay5;
    }

    private void setRelay5(boolean relay5) {
        this.relay5 = relay5;
    }

    public boolean isRelay6() {
        return relay6;
    }

    private void setRelay6(boolean relay6) {
        this.relay6 = relay6;
    }

    public boolean isRelay7() {
        return relay7;
    }

    private void setRelay7(boolean relay7) {
        this.relay7 = relay7;
    }

    private boolean isRelay8() {
        return relay8;
    }

    private void setRelay8(boolean relay8) {
        this.relay8 = relay8;
    }
}
