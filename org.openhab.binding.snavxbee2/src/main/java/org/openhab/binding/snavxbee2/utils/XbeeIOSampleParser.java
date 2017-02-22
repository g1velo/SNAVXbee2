package org.openhab.binding.snavxbee2.devices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.snavxbee2.utils.ChannelActionToPerform;
import org.openhab.binding.snavxbee2.utils.ChannelToXBeePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;

/**
 * {@link XbeeIOSampleParser} provides can parse message coming from Xbee devices.
 * <p>
 * Arguments should be ( The thing to update , and the XBee iosample.
 * XbeeIOSampleParser will send back channel to update ( If exists in the OH registry and State
 * <p>
 *
 * @author Stephan Navarro - Initial contribution
 */

public class XbeeIOSampleParser {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ArrayList<ChannelActionToPerform> listOfChannelActionToPerform = new ArrayList<>();

    public XbeeIOSampleParser(Thing thingToUpdate, IOSample ioSample) {

        ThingUID thingUIDToUpdate = thingToUpdate.getUID();

        Collection<Channel> thingChannels = thingToUpdate.getChannels();

        // logger.debug("xbeemsg length : {} ", xbeeMessage.getData().length);
        // Processing Analog values from Xbee Device ( Analog is readOnlyValues )
        if (ioSample.hasAnalogValues()) {

            // TODO handle handle analog value Options DONE

            HashMap<IOLine, Integer> analogMap = ioSample.getAnalogValues();
            // Set set = analogMap.entrySet();
            Set<Entry<IOLine, Integer>> set = analogMap.entrySet();
            Iterator<Entry<IOLine, Integer>> iterator = set.iterator();

            while (iterator.hasNext()) {
                Entry<IOLine, Integer> mentry = iterator.next();
                logger.trace("key is : {} & Value is : {}", mentry.getKey(), mentry.getValue());
                for (Channel channel : thingChannels) {
                    // logger.debug(" channel list : {} ", channel.getUID());
                    // logger.debug("comparing : {} to : {}", channel.getUID().getId(), formattedKey[1] +
                    // formattedKey[0]);

                    // Comparing define in the OH thing
                    // To the ones in the message.
                    // formatting the key from : DIO0/AD0 to AD0DOI0 for comparing

                    // if (channel.getUID().getId().toString().equals(formattedKey[1] + formattedKey[0])) {
                    if (channel.getUID().getId().toString()
                            .equals(ChannelToXBeePort.xBeePortToChannel(mentry.getKey()))) {
                        ChannelActionToPerform channelActionToPerform = new ChannelActionToPerform();
                        channelActionToPerform.setChannelUIDToUpdate(channel.getUID());

                        channelActionToPerform.setState(new DecimalType(Double.valueOf(mentry.getValue())));
                        logger.trace("existing channels fr thing : {} : {} ", thingUIDToUpdate, channel.getUID());
                        listOfChannelActionToPerform.add(channelActionToPerform);
                    }
                }
            }
        }

        if (ioSample.hasDigitalValues()) {
            // TODO handle handle analog value Options
            HashMap<IOLine, IOValue> digitalMap = ioSample.getDigitalValues();
            Set<Entry<IOLine, IOValue>> set = digitalMap.entrySet();
            Iterator<Entry<IOLine, IOValue>> iterator = set.iterator();

            while (iterator.hasNext()) {
                Entry<IOLine, IOValue> mentry = iterator.next();
                // logger.debug("key is : {} & Value is : {}", mentry.getKey(), mentry.getValue());
                for (Channel channel : thingChannels) {

                    // logger.debug("comparing : {} to : {}", ChannelToXBeePort.xBeePortToChannel(mentry.getKey()),
                    // channel.getUID().getId());

                    if (channel.getUID().getId().toString()
                            .equals(ChannelToXBeePort.xBeePortToChannel(mentry.getKey()))) {

                        logger.trace("matched : {} to : {}", ChannelToXBeePort.xBeePortToChannel(mentry.getKey()),
                                channel.getUID().getId().toString());
                        ChannelActionToPerform channelActionToPerform = new ChannelActionToPerform();
                        channelActionToPerform.setChannelUIDToUpdate(channel.getUID());

                        switch (mentry.getValue()) {
                            case HIGH:
                                channelActionToPerform.setState(OnOffType.ON);
                                break;
                            case LOW:
                                channelActionToPerform.setState(OnOffType.OFF);
                                break;

                            default:
                                logger.debug("This should Never happen");
                                break;
                        }
                        // channelActionToPerform.setState(mentry.getValue());

                        listOfChannelActionToPerform.add(channelActionToPerform);
                    }
                }
            }
        }
    }

    public ArrayList<ChannelActionToPerform> getListOfChannelActionToPerform() {
        return listOfChannelActionToPerform;
    }

}
