package org.openhab.binding.snavxbee2.devices.teleinfoEDF;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.SUPPORTED_CAFE0EDF_CHANNELS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import org.eclipse.smarthome.core.thing.Channel;
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
        Collection<Channel> thingChannels = thingToUpdate.getChannels();
        ChannelActionToPerform channelActionToPerform = new ChannelActionToPerform();

        if (last > first) {

            Scanner frame = new Scanner(messageToParse.toString());

            // while (frame.hasNextLine()) {
            while (frame.hasNextLine()) {

                String etiquette = frame.nextLine();

                String[] msg = etiquette.split(" ");

                for (int p = 0; p < msg.length; p++) {

                    if (SUPPORTED_CAFE0EDF_CHANNELS.contains(msg[p]) && p == 0) {
                        logger.warn("msg : {} ", msg[p]);
                    } else {
                        if (p == 0 && msg[p].length() > 0) {
                            logger.warn("Strange etiquette : {} : {} {}", p, msg[p], msg.length);
                        }
                    }
                    if (p == 1) {
                        logger.trace("Value: {} ", msg[p]);
                    }
                    if (p == 2) {
                        logger.trace("is correct : {} ", TeleInfoEDFCheckSum.isCheckSumCorrect(etiquette));
                        logger.trace("CheckSum: {} ", msg[p]);
                    }

                    channelActionToPerform.setChannelUIDToUpdate(new ChannelUID(thingUIDToUpdate + msg[0]));
                    if (true) {
                        // channelActionToPerform.setState(new StringType(msg[1].toString()));
                        // channelActionToPerform.setState(new StringType(new String(msg[1])));
                        listOfChannelActionToPerform.add(channelActionToPerform);
                    }
                }

                logger.warn("message : {} ", etiquette);

            }

            // for (ChannelActionToPerform actionToPerform : listOfChannelActionToPerform) {
            // logger.warn("channel to update : {} to value : {} ", actionToPerform.getChannelUIDToUpdate(),
            // actionToPerform.getState());
            // updateState(actionToPerform.getChannelUIDToUpdate(), actionToPerform.getState());
            // }
            frame.close();

            logger.trace("MESSAGE After : {} ", messageToParse.length());

            messageToParse.delete(0, last);
            logger.trace("first : {} last : {} ", first, last);

        }

        if (messageToParse.length() > 512) {
            logger.warn("Message parser is too big discarding ");
            messageToParse.delete(0, messageToParse.length());
        }

    }

    public ArrayList<ChannelActionToPerform> getListOfChannelActionToPerform() {
        return listOfChannelActionToPerform;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }
}
