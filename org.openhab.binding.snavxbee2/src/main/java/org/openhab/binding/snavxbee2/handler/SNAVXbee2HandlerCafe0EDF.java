/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.snavxbee2.handler;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.THING_TYPE_CAFE0EDF;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.snavxbee2.devices.teleinfoEDF.TeleinfoEDFParser;
import org.openhab.binding.snavxbee2.utils.ChannelActionToPerform;
import org.openhab.binding.snavxbee2.utils.IOLineIOModeMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBee64BitAddress;

/**
 * The {@link SNAVXbee2HandlerCafe0EDF} is responsible for handling commands, which are
 * sent to one of the thing for device identifier CAFE1001
 *
 * @author Stephan NAVARRO - Initial contribution
 */
// public class SNAVXbee2HandlerCafe0EDF extends BaseThingHandler {
public class SNAVXbee2HandlerCafe0EDF extends SNAVXbee2Handler {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2HandlerCafe0EDF.class);
    private Configuration thingConfig;
    private XBee64BitAddress xbee64BitsAddress;
    private String xbeeCommand;
    private ScheduledFuture<?> refreshJob;
    private List<IOLineIOModeMapping> IOsMapping = new ArrayList<>();
    private TeleinfoEDFParser teleinfoParser = new TeleinfoEDFParser();
    private Date lastPoke = new Date();
    private StringBuffer linkyMessages;

    public SNAVXbee2HandlerCafe0EDF(Thing thing) {
        super(thing);
        logger.debug("in {} method : contructor(Thing) ", this.getClass());

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO: handle command
        // Note: if communication with thing fails for some reason,
        // indicate that by setting the status with detail information
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // "Could not control device at IP address x.x.x.x");

        logger.debug("This channel  : {}  received command : {} ", channelUID.getId(), command);

        if (thing.getThingTypeUID().equals(THING_TYPE_CAFE0EDF)) {
            logger.debug("This channel  : " + channelUID.getId() + " is readonly");
        }

    }

    @Override
    public void initialize() {

        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");

        scheduler.execute(new InitialConfig());
        logger.warn("Initialise has ended");

        /*
         * this.thingConfig = thing.getConfiguration();
         * Map<String, Object> m = thing.getConfiguration().getProperties();
         * logger.debug(" Number of things in config : {} ", m.size());
         * this.xbee64BitsAddress = new XBee64BitAddress((String) m.get("Xbee64BitsAddress"));
         *
         * // Getting IOline configuration
         * updateStatus(ThingStatus.ONLINE);
         */
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        // refreshJob.cancel(true);
        super.dispose();
    }

    public synchronized void sendMessage(byte[] message) {

        long startTime = System.nanoTime();

        teleinfoParser.putMessage(this.getThing(), message);
        // linkyMessages.append(message);

        ArrayList<ChannelActionToPerform> listOfActionToPerform = new ArrayList<>();
        listOfActionToPerform = teleinfoParser.getListOfChannelActionToPerform();

        logger.trace("list : {} ", listOfActionToPerform.size());

        ListIterator<ChannelActionToPerform> listIterator = listOfActionToPerform
                .listIterator(listOfActionToPerform.size());

        // while (listIterator.hasNext()) {
        // ChannelActionToPerform a = listIterator.next();
        // }

        for (ChannelActionToPerform actionToPerform : listOfActionToPerform) {
            logger.trace("channel to update : {} to value : {} ", actionToPerform.getChannelUIDToUpdate(),
                    actionToPerform.getState());
            updateState(actionToPerform.getChannelUIDToUpdate(), actionToPerform.getState());
        }

        logger.trace("ellapsed Final : {} ", (System.nanoTime() - startTime) / 1000);

    }

    @Override
    public synchronized SNAVXbee2BridgeHandler getBridgeHandler() {
        SNAVXbee2BridgeHandler myBridgeHandler = null;
        // logger.debug(" Bridge Status" + getBridge().getStatus());
        Bridge bridge = getBridge();
        myBridgeHandler = (SNAVXbee2BridgeHandler) bridge.getHandler();
        return myBridgeHandler;
    }

    private class InitialConfig implements Runnable {
        @Override
        public void run() {

            logger.warn("Running device config in Thread");
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            thingConfig = thing.getConfiguration();
            Map<String, Object> m = thing.getConfiguration().getProperties();
            xbee64BitsAddress = new XBee64BitAddress((String) m.get("Xbee64BitsAddress"));

            // Getting IOline configuration
            updateStatus(ThingStatus.ONLINE);
            logger.warn("ConfigThread has ended");

        }
    };

    private class LinkyParser implements Runnable {
        @Override
        public void run() {
            logger.debug("Starting Parsing Data for CAFEEDF0 : {}");

            long startTime = System.nanoTime();

            // teleinfoParser.putMessage(this.getThing(), message);

            ArrayList<ChannelActionToPerform> listOfActionToPerform = new ArrayList<>();
            listOfActionToPerform = teleinfoParser.getListOfChannelActionToPerform();

            logger.trace("list : {} ", listOfActionToPerform.size());

            ListIterator<ChannelActionToPerform> listIterator = listOfActionToPerform
                    .listIterator(listOfActionToPerform.size());

            // while (listIterator.hasNext()) {
            // ChannelActionToPerform a = listIterator.next();
            // }

            for (ChannelActionToPerform actionToPerform : listOfActionToPerform) {
                logger.trace("channel to update : {} to value : {} ", actionToPerform.getChannelUIDToUpdate(),
                        actionToPerform.getState());
                updateState(actionToPerform.getChannelUIDToUpdate(), actionToPerform.getState());
            }

            logger.trace("ellapsed Final : {} ", (System.nanoTime() - startTime) / 1000);
        }
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {

        // Linky mode will set the remoteXbeeDevice Baud Rate
        // - 1200 for Mode 0 ( Mode Historique )
        // - 9600 For Mode 1 ( Mode standard )

        logger.warn("linkyMode is : {} {}", configurationParameters.get("linkyMode"));

        Configuration configuration = editConfiguration();
        // configuration = getThing().getConfiguration();

        for (Entry<String, Object> configurationParameter : configurationParameters.entrySet()) {
            configuration.put(configurationParameter.getKey(), configurationParameter.getValue());
        }

        if (configurationParameters.containsKey("linkyMode")) {

            BigDecimal linkyMode = (BigDecimal) configurationParameters.get("linkyMode");

            logger.trace("linkyMode is : {} hist {} ", configurationParameters.get("linkyMode"), linkyMode);
            // thingConfig = editConfiguration();

            if (linkyMode.intValue() == 0) {
                // 0x0 represente 1200 Baud rate
                logger.warn("Should set BD to 1200");
                this.getBridgeHandler().setRemoteATConfig(xbee64BitsAddress, "BD", new byte[] { (byte) 0x0 });
                configuration.put("linkyMode", 0);
            } else {
                // 0x3 represente 9600 Baud rate
                logger.warn("Should set BD to 9600");
                this.getBridgeHandler().setRemoteATConfig(xbee64BitsAddress, "BD", new byte[] { (byte) 0x3 });
                configuration.put("linkyMode", 1);
            }

            logger.warn("XBee Adress {} ", this.xbee64BitsAddress);
            updateConfiguration(configuration);

        }

        super.handleConfigurationUpdate(configurationParameters);

    }

}
