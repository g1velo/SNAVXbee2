package org.openhab.binding.snavxbee2.utils;

import com.digi.xbee.api.io.IOLine;

public class ChannelToXBeePort {

    public static String xBeePortToChannel(IOLine ioLine) {
        String channel[] = ioLine.toString().split("/");
        String returnvalue = "";
        switch (channel.length) {
            case 1:
                returnvalue = channel[0];
                break;
            case 2:
                returnvalue = channel[1] + channel[0];
                break;
        }
        return returnvalue;
    }
}
