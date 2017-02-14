/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.snavxbee2.handler;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.snavxbee2.devices.Tosr0xT;
import org.openhab.binding.snavxbee2.utils.IOLineIOModeMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBee64BitAddress;

/**
 * The {@link SNAVXbee2Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Stephan NAVARRO - Initial contribution
 */
public class SNAVXbee2Handler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2Handler.class);
    private Configuration thingConfig;
    private XBee64BitAddress xbee64BitsAddress;
    private String xbeeCommand;
    private ScheduledFuture<?> refreshJob;
    private List<IOLineIOModeMapping> IOsMapping = new ArrayList<>();

    public SNAVXbee2Handler(Thing thing) {
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

        this.xbeeCommand = null;

        if (thing.getThingTypeUID().equals(THING_TYPE_XBEEAPI)) {
            if (channelUID.getId().equals(TEMPERATURE)) {
                // updatest
                this.xbeeCommand = "b";
            }

        }

        if (thing.getThingTypeUID().equals(THING_TYPE_TOSR0XT)) {

            if (channelUID.getId().equals(TEMPERATURE)) {
                // updatest
                this.xbeeCommand = "b";
            }

            if (SUPPORTED_TOSR0XT_RELAY_CHANNELS.contains(channelUID.getId())) {

                // logger.debug(" Coudl be handling command ----------------------------------");
                /// logger.debug(getThing().getStatusInfo() + " " + getThing().getUID() + getThing().getThingTypeUID());
                // + " Could be handling command ----------------------------------");

                switch (command.toString()) {
                    case "ON":
                        logger.debug("sending to : " + channelUID.getId() + " command " + command);
                        // this.xbeeCommand = "e";
                        this.xbeeCommand = Tosr0xT.getRelayCommandToSend(channelUID.getId(), command.toString());
                        break;
                    case "OFF":
                        logger.debug("sending to : " + channelUID.getId() + " command " + command);
                        // this.xbeeCommand = "o";
                        this.xbeeCommand = Tosr0xT.getRelayCommandToSend(channelUID.getId(), command.toString());
                        break;
                    case "REFRESH":
                        logger.debug("sending to : " + channelUID.getId() + " command " + command);
                        this.xbeeCommand = Tosr0xT.getRelayCommandToSend("", command.toString());
                        // logger.debug("setting : " + channelUID.getId() + " to {} with {}", command,
                        // this.xbeeCommand);

                        break;
                    default:
                        logger.info("Don't know what to do with command : " + command);
                        // this.xbeeCommand = "[";
                        break;

                }

            }

        }

        if (this.xbeeCommand != null) {
            logger.debug(" handler xbee64BitsAddress : {}  this.xbeeCommand : {} ", this.xbee64BitsAddress,
                    this.xbeeCommand);
            getBridgeHandler().sendCommandToDevice(xbee64BitsAddress, this.xbeeCommand);
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

        thingConfig = thing.getConfiguration();

        Map<String, Object> m = thing.getConfiguration().getProperties();

        logger.debug(" Number of things in config : {} ", m.size());

        for (String key : m.keySet()) {
            logger.debug(" SN KKKEYS : {} ", key);
            logger.debug(" VVVValues: {} ", m.get(key));
        }

        // this.xbee64BitsAddress. m.get("Xbee64BitsAddress");

        this.xbee64BitsAddress = new XBee64BitAddress((String) m.get("Xbee64BitsAddress"));

        logger.debug("thing : {} compared : {} ", thing, THING_TYPE_TOSR0XT);

        if (thing.getThingTypeUID().equals(THING_TYPE_TOSR0XT)) {
            startAutomaticRefresh();
        } else {
            logger.debug("getting IOLine config for device : {} ", this.xbee64BitsAddress);
            this.IOsMapping = getBridgeHandler().getXBeeDeviceIOLineConfig(this.xbee64BitsAddress);
            for (IOLineIOModeMapping map : this.IOsMapping) {
                logger.debug("IOLIne : {} IOMode : {} ", map.getIoLine(), map.getIoMode());
            }

        }

        updateStatus(ThingStatus.ONLINE);

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        refreshJob.cancel(true);
        super.dispose();
    }

    public synchronized SNAVXbee2BridgeHandler getBridgeHandler() {
        SNAVXbee2BridgeHandler myBridgeHandler = null;
        // logger.debug(" Bridge Status" + getBridge().getStatus());
        Bridge bridge = getBridge();
        myBridgeHandler = (SNAVXbee2BridgeHandler) bridge.getHandler();
        return myBridgeHandler;
    }

    private void startAutomaticRefresh() {

        logger.debug("startAutomaticRefresh()");

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                logger.debug("The scheduled thread is running ! ");
                getBridgeHandler().sendCommandToDevice(xbee64BitsAddress, "b");
            }
        };

        refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, 450, TimeUnit.SECONDS);

    }

}
