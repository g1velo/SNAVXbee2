package org.openhab.binding.snavxbee2.devices;

import static org.eclipse.smarthome.core.library.types.OnOffType.*;

import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

public class Tosr0xTMsg {

    private Logger logger = LoggerFactory.getLogger(Tosr0xTMsg.class);

    private XBeeMessage payload;
    private byte[] byteRelayState;

    // private static String allRelay = "d";
    private boolean relay1;
    private boolean relay2;
    private boolean relay3;
    private boolean relay4;
    private boolean relay5;
    private boolean relay6;
    private boolean relay7;
    private boolean relay8;
    private State relay1State;
    private State relay2State;
    private State relay3State;
    private State relay4State;
    private State relay5State;
    private State relay6State;
    private State relay7State;
    private State relay8State;

    public Tosr0xTMsg(byte[] byteRelayState) {
        super();
        this.byteRelayState = byteRelayState;

        // byte bytes = byteRelayState[1];
        // logger.debug("relay0 : {} ", b);
        // logger.debug("relay1 : SN " + byteRelayState.length);
        // logger.debug("relay2 : {} ", byteRelayState[2]);

        for (byte bit : byteRelayState) {
            logger.debug(Integer.toBinaryString(bit & 255 | 256).substring(1));
            // boolean a = (bit & 0x1) != 0;
            setRelay1((bit & 0x1) != 0);
            setRelay2((bit & 0x2) != 0);
            setRelay3((bit & 0x4) != 0);
            setRelay4((bit & 0x8) != 0);
            setRelay5((bit & 0x10) != 0);
            setRelay6((bit & 0x20) != 0);
            setRelay7((bit & 0x40) != 0);
            setRelay8((bit & 0x80) != 0);
            // logger.debug("bites : ", a, b, c, d, e, f, g, h);

        }

    }

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

    public State getRelay1State() {
        if (relay1) {
            relay1State = ON;

        } else {
            relay1State = OFF;

        }
        return relay1State;
    }

    public State getRelay2State() {
        if (relay2) {
            relay2State = ON;

        } else {
            relay2State = OFF;

        }
        return relay2State;
    }

    public State getRelay3State() {
        if (relay3) {
            relay3State = ON;

        } else {
            relay3State = OFF;

        }
        return relay3State;
    }

    public State getRelay4State() {
        if (relay4) {
            relay4State = ON;

        } else {
            relay4State = OFF;

        }
        return relay4State;
    }

    public State getRelay5State() {
        if (relay5) {
            relay5State = ON;

        } else {
            relay5State = OFF;

        }
        return relay5State;
    }

    public State getRelay6State() {
        if (relay6) {
            relay6State = ON;

        } else {
            relay6State = OFF;

        }
        return relay6State;
    }

    public State getRelay7State() {
        if (relay7) {
            relay7State = ON;

        } else {
            relay7State = OFF;

        }
        return relay7State;
    }

    public State getRelay8State() {
        if (relay8) {
            relay8State = ON;

        } else {
            relay8State = OFF;

        }
        return relay8State;
    }
}
