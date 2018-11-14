package org.openhab.binding.snavxbee2.devices.teleinfoEDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeleInfoEDFCheckSum {
    private static Logger logger = LoggerFactory.getLogger(TeleInfoEDFCheckSum.class);

    public static boolean isCheckSumCorrect(String msg) {

        boolean correct = false;
        String mymsg = msg;

        int sum = 0;
        // for (int i : mysmg.toCharArray()) {
        // sum += i;
        // }

        byte[] b = mymsg.getBytes();
        int last = b.length;

        for (int i = 0; i < (b.length - 1); i++) {
            // String tmp = msg[i];
            sum += b[i];
            // logger.warn("Value of : {} ", b[i]);
        }

        if (b[last - 1] == (sum & 63)) {
            correct = true;
            logger.trace(" msg : {}", msg);
            logger.trace("CheckSum : {} : {} : {} ", b[last - 1], sum, (sum & 63));
        } else {
            logger.trace(" Bad : {}", msg);
            logger.trace("CheckSum : {} : {} : {} ", b[last - 1], sum, (sum & 63));

        }

        return correct;
    }
}
