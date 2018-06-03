package org.openhab.binding.snavxbee2.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBee64BitAddress;

public class XBeeContactList {

    private static Logger logger = LoggerFactory.getLogger(XBeeContactList.class);

    Map<XBee64BitAddress, Date> XBeeAlive = new HashMap<XBee64BitAddress, Date>();

    public void updateXbee(XBee64BitAddress xbee64BitAddress) {
        if (XBeeAlive.containsKey(xbee64BitAddress)) {
            logger.debug("XbeeAdress {} Already in List", xbee64BitAddress);
            XBeeAlive.replace(xbee64BitAddress, new Date());

        } else {
            XBeeAlive.put(xbee64BitAddress, new Date());
        }

    }

}
