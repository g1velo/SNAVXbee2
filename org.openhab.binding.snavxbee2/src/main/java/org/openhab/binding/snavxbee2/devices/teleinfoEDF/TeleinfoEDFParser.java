package org.openhab.binding.snavxbee2.devices.teleinfoEDF;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.SUPPORTED_CAFE0EDF_CHANNELS;

import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.snavxbee2.utils.ChannelActionToPerform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephan Navarro - Initial contribution
 */

public class TeleinfoEDFParser extends Thread {

    private Logger logger = LoggerFactory.getLogger(TeleinfoEDFParser.class);
    private StringBuffer messageToParse = new StringBuffer();
    private ThingUID thingUIDToUpdate;
    private Thing thingToUpdate;
    private ArrayList<ChannelActionToPerform> listOfChannelActionToPerform = new ArrayList<>();

    // private StringBuffer messageModifiedToParse = new StringBuffer();
    // private ExecutorService executor = Executors.newFixedThreadPool(10);

    // private byte[] b;

    public synchronized void putMessage(Thing thingToUpdate, byte[] message) {
        this.thingToUpdate = thingToUpdate;
        this.thingUIDToUpdate = thingToUpdate.getUID();
        this.putMessage(message);

    }

    public synchronized void putMessage(byte[] message) {

        logger.trace(" Current thread :  {}", Thread.currentThread().getName());

        for (int j = 0; j < message.length; j++) {

            if ((message[j] & 127) != 2 && (message[j] & 127) != 3) {
                logger.trace(new String(Character.toChars((message[j] & 127))));
                messageToParse.append(new String(Character.toChars((message[j] & 127))));
            }
        }

        this.parse();

    }

    private void parse() {

        logger.trace("Parsing : {} ", messageToParse);
        int first = messageToParse.indexOf("\n");
        int last = messageToParse.lastIndexOf("\r");

        logger.trace("first : {}  last : {} length : {}", first, last, messageToParse.length());
        logger.trace("MESSAGE Before  : {} ", messageToParse.toString());
        logger.trace("Frame length : {}", messageToParse.length());

        ThingUID thingUIDToUpdate = thingToUpdate.getUID();
        // Collection<Channel> thingChannels = thingToUpdate.getChannels();

        if (messageToParse.length() > 512) {
            logger.warn("Message parser is too big discarding ");
            messageToParse.delete(0, messageToParse.length());
        }

        if (last > first) {

            Scanner frame = new Scanner(messageToParse.toString());

            while (frame.hasNextLine()) {

                ChannelActionToPerform channelActionToPerform = new ChannelActionToPerform();

                String etiquette = frame.nextLine();

                if (etiquette.length() != 0) {
                    logger.trace("etiquette : {} ", etiquette);

                    boolean isDouble = false;

                    TeleinfoMessage tim = new TeleinfoMessage(etiquette);
                    logger.trace("result :  {} ", tim.isChecksumValid());

                    if (tim.isChecksumValid()) {
                        if (SUPPORTED_CAFE0EDF_CHANNELS.contains(tim.getMessageID())) {
                            logger.trace("Etiquette: {} ", tim.getMessageID());
                            ChannelUID cuid = new ChannelUID(thingUIDToUpdate + ":" + tim.getMessageID());

                            channelActionToPerform.setChannelUIDToUpdate(cuid);

                            logger.trace(" thingToUpdate.getUID() : {} ",
                                    thingToUpdate.getChannel(tim.getMessageID()).getAcceptedItemType());

                            switch (thingToUpdate.getChannel(tim.getMessageID()).getAcceptedItemType()) {
                                case "String":
                                    isDouble = false;
                                    break;
                                case "Number":
                                    isDouble = true;
                                    break;
                            }

                            String value = tim.getValue();

                            if (isDouble) {
                                logger.trace("BigDecimal");
                                channelActionToPerform.setState(new DecimalType(Double.parseDouble(value)));
                            } else {
                                logger.trace("String");
                                channelActionToPerform.setState(new StringType(value));
                            }

                            logger.trace("is correct : {} ", TeleInfoEDFCheckSum.isCheckSumCorrect(etiquette));
                            logger.trace("CheckSum: {} ", tim.getChecksum());
                            logger.trace("adding value : {} to {} ", tim.getValue(), tim.getMessageID());
                            listOfChannelActionToPerform.add(channelActionToPerform);

                        } else {
                            logger.warn("Strange etiquette : {}", tim.getMessageID());
                        }
                    }
                }
            }

            frame.close();

            logger.trace("MESSAGE After : {} ", messageToParse.length());

            messageToParse.delete(0, last);
            logger.trace("first : {} last : {} ", first, last);
        }

    }

    public ArrayList<ChannelActionToPerform> getListOfChannelActionToPerform() {
        ArrayList<ChannelActionToPerform> l = (ArrayList<ChannelActionToPerform>) listOfChannelActionToPerform.clone();
        listOfChannelActionToPerform = new ArrayList();

        // logger.trace(" listOfChannelActionToPerform.size : {} ", listOfChannelActionToPerform.size());
        // logger.trace(" l.size : {} ", l.size());
        return l;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }
}
