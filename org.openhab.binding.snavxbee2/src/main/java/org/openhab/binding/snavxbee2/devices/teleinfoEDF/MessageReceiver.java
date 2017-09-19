package org.openhab.binding.snavxbee2.devices.teleinfoEDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageReceiver {

    private static MessageReceiver singleton = new MessageReceiver();
    private static Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    private MessageReceiver() {
        logger.debug("demoMethod for singleton");
    }

    public static MessageReceiver getInstance() {
        return singleton;
    }

    protected static void putMessage(String message) {
        logger.debug("Message received : {} ", message);
    }

}
