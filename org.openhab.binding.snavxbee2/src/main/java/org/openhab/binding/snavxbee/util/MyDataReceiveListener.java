package org.openhab.binding.snavxbee.util;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.*;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Class to manage the XBee received data that was sent by other modules in the
 * same network.
 *
 * <p>
 * Acts as a data listener by implementing the
 * {@link IDataReceiveListener} interface, and is notified when new
 * data for the module is received.
 * </p>
 *
 * @see IDataReceiveListener
 *
 */

public class MyDataReceiveListener implements IDataReceiveListener {

    private Logger logger = LoggerFactory.getLogger(MyDataReceiveListener.class);
    private float temp = 0;
    private ThingRegistry thingRegistry;
    private Thing myThing;

    /*
     * (non-Javadoc)
     *
     * @see com.digi.xbee.api.listeners.IDataReceiveListener#dataReceived(com.digi.xbee.api.models.XBeeMessage)
     */
    @Override
    public void dataReceived(XBeeMessage xbeeMessage) {

        logger.debug(" I Found ? ???  ");

        // Thing anotherThing = new Th

        // myThing.getStatus();
        // myThing.getChannels();

        // Collection<Thing> things = getAll();

        // Collection<Thing> things = thingRegistry.getAll();

        logger.debug(" number of things in the collection : {}"); // , things.size());

        // logger.debug(" I Found : " + things.size() + " Things ");

        logger.info("From " + xbeeMessage.getDevice().get64BitAddress() + " >> "
                + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())) + " | "
                + new String(xbeeMessage.getData()));

        logger.info("From " + xbeeMessage.getDevice().getNodeID() + " >> "
                + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())) + " | "
                + new String(xbeeMessage.getData()));

        if (xbeeMessage.getData().length == 7) {
            logger.info(" The temperature is : " + new String(xbeeMessage.getData()));

            logger.debug(" I shoulb be updating : " + CHANNEL_1);
            // logger.debug(" I shoulb be updating : " + TEMPERATURE);
            logger.debug("building the thing to update " + BINDING_ID);
            logger.debug("building the thing to update " + xbeeMessage.getDevice().get64BitAddress());
            // logger.debug("building the thing to update " + getAll());
        }

        if (xbeeMessage.getData().length == 2) {
            // MSB + LSB temp = ( MSB*256 + LSB ) /16
            logger.info(" The temperature is in HEXA : "
                    + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())));

            Byte msb = xbeeMessage.getData()[0];
            Byte lsb = xbeeMessage.getData()[1];

            float temperature = (msb.floatValue() * 256 + lsb.floatValue()) / 16;
            logger.info(" The temperature is in Float : " + temperature);
            logger.info(" The temperature msb is in Float : " + msb.floatValue());
            logger.info(" The temperature lsb is in Float : " + lsb.floatValue());
            this.setTemp(temperature);

            // updateState(new ChannelUID(getThing().getUID(), TEMPERATURE), temperature);

        }

        if (xbeeMessage.getData().length == 1) {
            logger.info(" This is the relay State : "
                    + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())));
        }

    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

}