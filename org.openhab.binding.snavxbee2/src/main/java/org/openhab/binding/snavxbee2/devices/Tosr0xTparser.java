package org.openhab.binding.snavxbee2.devices;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.snavxbee2.utils.ChannelActionToPerform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

/**
 * {@link Tosr0xTparser} provides can parse message coming from Xbee netowrk for TOSR0xT devices .
 * <p>
 * Arguments should be ( the thing to update , and the xbee nessage.
 * Tosr0xTparser will send back channel to update and State
 * <p>
 *
 * @author Stephan Navarro - Initial contribution
 */

public class Tosr0xTparser {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ArrayList<ChannelActionToPerform> listOfChannelActionToPerform = new ArrayList<>();

    public Tosr0xTparser(Thing thingToUpdate, XBeeMessage xbeeMessage) {

        ThingUID thingUIDToUpdate = thingToUpdate.getUID();

        Collection<Channel> thingChannels = thingToUpdate.getChannels();

        // ChannelActionToPerform actionToPerform;
        logger.debug("xbeemsg length : {} ", xbeeMessage.getData().length);

        if (xbeeMessage.getData().length == 7) {

            ChannelActionToPerform channelActionToPerform = new ChannelActionToPerform();
            channelActionToPerform.setChannelUIDToUpdate(new ChannelUID(thingUIDToUpdate + ":temperature"));
            channelActionToPerform.setState(new DecimalType(Double.valueOf(new String(xbeeMessage.getData()))));
            this.listOfChannelActionToPerform.add(channelActionToPerform);
        }

        if (xbeeMessage.getData().length == 2) {
            // MSB + LSB temp = ( MSB*256 + LSB ) /16
            // Given by TosR0xT Device
            Byte msb = xbeeMessage.getData()[0];
            Byte lsb = xbeeMessage.getData()[1];

            float temperature = (msb.floatValue() * 256 + lsb.floatValue()) / 16;
            ChannelActionToPerform channelActionToPerform = new ChannelActionToPerform();
            channelActionToPerform.setChannelUIDToUpdate(new ChannelUID(thingUIDToUpdate + ":temperature"));
            channelActionToPerform.setState(new DecimalType(temperature));
            listOfChannelActionToPerform.add(channelActionToPerform);
        }

        if (xbeeMessage.getData().length == 1) {
            logger.info(" This is the relay State : "
                    + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())));

            // Instanciating the parser
            Tosr0xTMsg t = new Tosr0xTMsg(xbeeMessage.getData());

            // Collection<Channel> thingChannels = thing.getChannels();
            for (Channel channel : thingChannels) {
                ChannelActionToPerform channelActionToPerform = new ChannelActionToPerform();
                if (channel.getUID().getId().contains("relay")) {
                    // logger.debug("updating : {} ", channel.getUID().getId());
                    // channelToUpdate = thingUIDToUpdate + ":" + channel.getUID().getId();
                    channelActionToPerform.setChannelUIDToUpdate(channel.getUID());
                    switch (channel.getUID().getId()) {
                        case "relay1":
                            // logger.debug("sss updating : {} to {} ", channelToUpdate, t.getRelay1State());
                            // this.state = t.getRelay1State();
                            channelActionToPerform.setState(t.getRelay1State());
                            listOfChannelActionToPerform.add(channelActionToPerform);
                            break;
                        case "relay2":
                            channelActionToPerform.setState(t.getRelay2State());
                            listOfChannelActionToPerform.add(channelActionToPerform);
                            break;
                        case "relay3":
                            channelActionToPerform.setState(t.getRelay3State());
                            listOfChannelActionToPerform.add(channelActionToPerform);
                            break;
                        case "relay4":
                            channelActionToPerform.setState(t.getRelay4State());
                            listOfChannelActionToPerform.add(channelActionToPerform);
                            break;
                        case "relay5":
                            channelActionToPerform.setState(t.getRelay5State());
                            listOfChannelActionToPerform.add(channelActionToPerform);
                            break;
                        case "relay6":
                            channelActionToPerform.setState(t.getRelay6State());
                            listOfChannelActionToPerform.add(channelActionToPerform);
                            break;
                        case "relay7":
                            channelActionToPerform.setState(t.getRelay7State());
                            listOfChannelActionToPerform.add(channelActionToPerform);
                            break;
                        case "relay8":
                            channelActionToPerform.setState(t.getRelay8State());
                            listOfChannelActionToPerform.add(channelActionToPerform);
                            break;
                    }

                    // logger.debug(" channel : {} {} ", thing.getThingTypeUID(), channel.getUID().getId());
                }
            }
        }

    }

    public ArrayList<ChannelActionToPerform> getListOfChannelActionToPerform() {
        return listOfChannelActionToPerform;
    }

}
