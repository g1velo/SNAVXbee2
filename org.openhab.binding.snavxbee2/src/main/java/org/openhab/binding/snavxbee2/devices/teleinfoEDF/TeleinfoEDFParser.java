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

    public void putMessage(byte[] message) {

        logger.trace(" Current thread :  {}", Thread.currentThread().getName());

        // messageToParse.append(message);

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

        // int next = messageToParse.indexOf("\u0003\u0002", first + 1);

        logger.trace("first : {}  last : {} ", first, last);
        logger.trace("MESSAGE Before  : {} ", messageToParse.toString());
        logger.trace("Frame length : {}", messageToParse.length());

        ThingUID thingUIDToUpdate = thingToUpdate.getUID();
        // Collection<Channel> thingChannels = thingToUpdate.getChannels();

        if (last > first) {

            Scanner frame = new Scanner(messageToParse.toString());

            // while (frame.hasNextLine()) {
            while (frame.hasNextLine()) {

                ChannelActionToPerform channelActionToPerform = new ChannelActionToPerform();

                String etiquette = frame.nextLine();
                logger.trace("etiquette : {} ", etiquette);

                if (etiquette.length() != 0) {

                    boolean isDouble = false;

                    TeleinfoMessage tim = new TeleinfoMessage(etiquette);
                    logger.trace("result :  {} ", tim.isChecksumValid());

                    String[] msg = etiquette.split(" ");

                    for (int p = 0; p < msg.length; p++) {

                        if (msg.length == 3) {

                            if (SUPPORTED_CAFE0EDF_CHANNELS.contains(msg[p]) && p == 0) {
                                logger.trace("Etiquette: {} ", msg[p]);
                                ChannelUID cuid = new ChannelUID(thingUIDToUpdate + ":" + msg[p]);
                                channelActionToPerform.setChannelUIDToUpdate(cuid);
                                // Channel ch = new ChannelUID(channelUid)
                            } else {
                                if (p == 0 && msg[p].length() > 0) {
                                    logger.warn("Strange etiquette : {} : {} {}", p, msg[p], msg.length);
                                }
                            }

                            if (p == 1) {
                                logger.trace("Value: {} ", msg[1]);

                                String value = msg[p].trim();

                                try {
                                    Double.parseDouble(value);
                                    logger.trace("is a Double");
                                    isDouble = true;
                                } catch (NumberFormatException e) {
                                    logger.trace("Not a Double");
                                    isDouble = false;
                                }

                                Class<?> dataType = msg[1].getClass();

                                if (isDouble) {
                                    logger.trace("BigDecimal");
                                    channelActionToPerform.setState(new DecimalType(Double.parseDouble(value)));
                                } else {
                                    if (String.class.isAssignableFrom(dataType)) {
                                        logger.trace("String");
                                        channelActionToPerform.setState(new StringType(value));
                                    }
                                }

                                // channelActionToPerform.setState(new DecimalType("123"));
                            }
                            if (p == 2) {
                                logger.trace("is correct : {} ", TeleInfoEDFCheckSum.isCheckSumCorrect(etiquette));
                                logger.trace("CheckSum: {} ", msg[p]);
                                logger.trace("adding value : {} to {} ", msg[1], msg[0]);
                                listOfChannelActionToPerform.add(channelActionToPerform);
                            }

                        }

                    }
                }

            }

            frame.close();

            logger.trace("MESSAGE After : {} ", messageToParse.length());

            messageToParse.delete(0, last);
            logger.trace("first : {} last : {} ", first, last);

        }

        if (messageToParse.length() > 512) {
            logger.trace("Message parser is too big discarding ");
            messageToParse.delete(0, messageToParse.length());
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
