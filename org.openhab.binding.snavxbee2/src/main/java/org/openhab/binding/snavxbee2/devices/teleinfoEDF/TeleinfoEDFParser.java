package org.openhab.binding.snavxbee2.devices.teleinfoEDF;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephan Navarro - Initial contribution
 */

public class TeleinfoEDFParser {

    private Logger logger = LoggerFactory.getLogger(TeleinfoEDFParser.class);
    private StringBuffer messageToParse = new StringBuffer();
    private StringBuffer messageModifiedToParse = new StringBuffer();
    // private ExecutorService executor = Executors.newFixedThreadPool(10);

    // private byte[] b;

    public void putMessage(byte[] message) {

        logger.debug(" JSJSS {}", Thread.currentThread().getName());
        // messageModifiedToParse.append(message);

        byte[] b = new byte[message.length + 1];

        for (int j = 0; j < message.length; j++) {
            // logger.debug(" Byte [{}] : {} ", j, message[j] & 127);
            b[j] = (byte) (message[j] & 127); // b[j] = (byte) (message[j] & 127);

            // logger.debug(" Byte [{}] : {} ", j, b[j]);

        }

        try {
            // logger.debug(new String(b, "UTF-8"));
            messageToParse.append(new String(b, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.parse();
    }

    private void parse() {

        boolean frame_begin_found = false;
        // boolean frame_end_found_once = false;
        boolean frame_begin_found_once = false;
        int frame_begin = 0;
        int firstFrameToDelete = 0;
        // int lastFrameToDelete = -1;
        int last_frame_end = 0;

        for (int i = 0; i < messageToParse.length(); i++) {
            // logger.debug(messageToParse.charAt(i));

            int ascii = messageToParse.charAt(i);

            // logger.debug("Value : {} {} ", i, ascii);
            // logger.debug("start Frame : {}", i);

            switch (ascii) {
                case 10:
                    // logger.debug("start Frame : {}", i);
                    if (!frame_begin_found_once) {
                        frame_begin_found_once = true;
                    }

                    frame_begin_found = true;
                    frame_begin = i;
                    break;
                case 13:
                    if (frame_begin_found && frame_begin_found_once) {

                        String stringMessageToParse = (String) messageToParse.subSequence(frame_begin + 1, i);
                        logger.debug("Message to parse : {} ", stringMessageToParse);
                        // executor.execute(new TeleinfoMessageReceived(stringMessageToParse, thing));
                        TeleinfoMessageReceived timr = new TeleinfoMessageReceived(stringMessageToParse); // , thing);
                        timr.run();

                        last_frame_end = i;
                        frame_begin_found = false;

                    } else {
                        // if not start Frame was previously found, we delete the beginning
                        // of the Frame
                        firstFrameToDelete = 0;
                        // messageToPa// rse.delete(0, i);
                    }
                    // frame_end_found = true;
                    // frame_end = i;
                    // logger.debug("end Frame : {}", i);
                    break;
                default:
                    // useless so far
                    if (ascii > 20 && ascii < 127) {
                        // logger.debug("good caracter : {} i: {} ", ascii, i);
                    } else {
                        logger.trace("Stange caracter : {} at : {} ", ascii, i);
                    }
                    break;
            }
        }

        // logger.debug("message length 1 : {} ", messageToParse.length());

        if (last_frame_end != 0) {//
            // logger.debug("firstFrameToDelete : {} last_frame_end : {} ", firstFrameToDelete, last_frame_end + 1);
            messageToParse.delete(firstFrameToDelete, last_frame_end);
            // logger.debug("message length : {} ", messageToParse.length());
        }

        if (messageToParse.length() > 256) {
            logger.debug("Message parser to big discarding ");
            messageToParse = new StringBuffer();
            // messageToParse.delete(0, 1024);
        }

        // logger.debug("message length 2 : {} ", messageToParse.length());
        // logger.debug("full msg : {} ", messageToParse.capacity());
        // messageToParse.trimToSize();

        // String stringMessageToParse = (String) messageToParse.subSequence(0, messageToParse.length());
        // logger.debug("Mess to par look lke : {} ", stringMessageToParse.toCharArray());

        // messageToParse = new StringBuffer();
    }

    private void reset() {
    }
}
