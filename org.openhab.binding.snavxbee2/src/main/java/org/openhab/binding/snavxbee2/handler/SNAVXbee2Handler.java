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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.thing.binding.firmware.Firmware;
import org.eclipse.smarthome.core.thing.binding.firmware.FirmwareUpdateHandler;
import org.eclipse.smarthome.core.thing.binding.firmware.ProgressCallback;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.snavxbee2.devices.Tosr0xT;
import org.openhab.binding.snavxbee2.utils.ChannelToXBeePort;
import org.openhab.binding.snavxbee2.utils.IOLineIOModeMapping;
import org.openhab.binding.snavxbee2.utils.RCAndIOValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.models.XBee64BitAddress;

/**
 * The {@link SNAVXbee2Handler} is responsible for handling commands, which are
 * sent to one of the thing.
 *
 * @author Stephan NAVARRO - Initial contribution
 */
public class SNAVXbee2Handler extends BaseThingHandler implements FirmwareUpdateHandler {
    // Handler for thing types :
    // CAFE1000 ( With TOSR0X Attached
    // Any Xbee with no type in learn Mode

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2Handler.class);
    private Configuration thingConfig = this.getConfig();
    private XBee64BitAddress xbee64BitsAddress;
    private String xbeeCommand;
    private ScheduledFuture<?> refreshJob;
    private List<IOLineIOModeMapping> IOsMapping = new ArrayList<>();
    private Map<String, Object> bridgeconfigMap;
    private Date lastPoke = new Date();

    public SNAVXbee2Handler(Thing thing) {
        super(thing);
        logger.debug("in {} method : contructor(Thing) ", this.getClass());

    }

    public void pokeIt() {
        this.lastPoke = new Date();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO: handle command
        // Note: if communication with thing fails for some reason,
        // indicate that by setting the status with detail information
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // "Could not control device at IP address x.x.x.x");

        this.xbeeCommand = null;

        // managing to send the right ASCII command to the device cafe1000

        if (thing.getThingTypeUID().equals(THING_TYPE_CAFE1000)) {

            logger.debug("sending to : " + channelUID.getId() + " command " + command);
            // this.xbeeCommand = "e";
            this.xbeeCommand = channelUID.getId() + "=" + command.toString();

            if (this.xbeeCommand != null) {
                logger.debug(" handler xbee64BitsAddress : {}  this.xbeeCommand : {} ", this.xbee64BitsAddress,
                        this.xbeeCommand);

            }
        }

        // managing to send ON/OFF command to DIO port configured as IOMode.DIGITAL_OUT_HIGH or IOMode.DIGITAL_OUT_LOW

        if (thing.getThingTypeUID().equals(THING_TYPE_XBEEAPI) || thing.getThingTypeUID().equals(THING_TYPE_SAMPLE)
                || thing.getThingTypeUID().equals(THING_TYPE_CAFE1000)) {
            logger.debug("IOmaps : {} Channel to update : {}  port : {} ", IOsMapping.size(), channelUID,
                    channelUID.getId());

            if (IOsMapping != null) {
                for (IOLineIOModeMapping map : IOsMapping) {
                    logger.trace("IOLine : {}", map.getIoLine());

                    // if (map.getIoLine().toString().equals(channelUID.getId())
                    if (ChannelToXBeePort.xBeePortToChannel(map.getIoLine()).equals(channelUID.getId())
                            && (map.getIoMode().equals(IOMode.DIGITAL_OUT_HIGH)
                                    || map.getIoMode().equals(IOMode.DIGITAL_OUT_LOW))) {

                        logger.trace("Channel to update : {}  port : {} ", channelUID, channelUID.getId());

                        switch (command.toString()) {
                            case "ON":
                                getBridgeHandler().sendAPICommandToDevice(xbee64BitsAddress, map.getIoLine(),
                                        IOValue.HIGH);
                                break;
                            case "OFF":
                                getBridgeHandler().sendAPICommandToDevice(xbee64BitsAddress, map.getIoLine(),
                                        IOValue.LOW);
                                break;
                            case "REFRESH":
                                RCAndIOValue rcvalue = getBridgeHandler().getIOLineValue(xbee64BitsAddress,
                                        map.getIoLine());
                                if (rcvalue.isRc()) {
                                    Collection<Channel> thingChannels = this.getThing().getChannels();
                                    for (Channel channel : thingChannels) {
                                        if (channel.getUID().getId().toString().equals(channelUID.getId())) {
                                            // Channel matching found updatin to value
                                            updateState(channelUID, rcvalue.getState());
                                        }
                                    }

                                } else {
                                    logger.info("An error occured get ioline : {}  from device  : {} ", map.getIoLine(),
                                            xbee64BitsAddress);
                                }
                                break;
                            default:
                                logger.debug("don't know what to do with command : {}", command.toString());
                                break;
                        }

                    }
                }

            } else {
                logger.debug("IOsMapping was not found");
            }
        }

        if (thing.getThingTypeUID().equals(THING_TYPE_TOSR0XT)) {

            if (channelUID.getId().equals(TEMPERATURE)) {
                // updates
                this.xbeeCommand = "b";
            }

            if (SUPPORTED_TOSR0XT_RELAY_CHANNELS.contains(channelUID.getId())) {

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
            if (thingConfig.get("SyncCommunutation").toString() == "true") {
                getBridgeHandler().sendSyncCommandToDevice(xbee64BitsAddress, this.xbeeCommand);
            } else {
                getBridgeHandler().sendAsyncCommandToDevice(xbee64BitsAddress, this.xbeeCommand);
            }
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

        bridgeconfigMap = thing.getConfiguration().getProperties();

        logger.trace(" Number of things in config : {} ", bridgeconfigMap.size());

        for (Entry<String, Object> configParameter : bridgeconfigMap.entrySet()) {
            logger.info("updating {} Key : {} with : {} ", thing.getThingTypeUID(), configParameter.getKey(),
                    configParameter.getValue());
        }

        this.xbee64BitsAddress = new XBee64BitAddress((String) bridgeconfigMap.get("Xbee64BitsAddress"));
        // this.xbee64BitsAddress =

        updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.HANDLER_CONFIGURATION_PENDING, "In initialise() Method ");

        if (thing.getThingTypeUID().equals(THING_TYPE_TOSR0XT)) {
            // starting automatic refresh for this device
            startAutomaticRefresh();
        }

        checkDeviceConfig();

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        refreshJob.cancel(true);
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NONE, "Done by Dispose()");
        super.dispose();
    }

    @Override
    public void updateFirmware(Firmware firmware, ProgressCallback progressCallback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isUpdateExecutable() {
        // TODO Auto-generated method stub
        return false;
    }

    public synchronized SNAVXbee2BridgeHandler getBridgeHandler() {
        SNAVXbee2BridgeHandler myBridgeHandler = null;
        // logger.debug(" Bridge Status" + getBridge().getStatus());
        Bridge bridge = getBridge();
        myBridgeHandler = (SNAVXbee2BridgeHandler) bridge.getHandler();
        return myBridgeHandler;
    }

    private void checkDeviceConfig() {

        logger.debug("checkDeviceConfig()");

        Runnable checkDevice = new Runnable() {
            @Override
            public void run() {

                IOsMapping = getBridgeHandler().getXBeeDeviceIOLineConfig(xbee64BitsAddress);

                logger.debug("Checking Device {} is on the network ! ", xbee64BitsAddress);

                for (IOLineIOModeMapping map : IOsMapping) {
                    logger.trace("IOLIne : {} IOMode : {} ", map.getIoLine(), map.getIoMode());
                }
                logger.trace("IOsMapping has : {} Lines", IOsMapping.size());

                if (!IOsMapping.isEmpty()) {
                    if (!getThing().getStatus().equals(ThingStatus.ONLINE)) {
                        updateStatus(ThingStatus.ONLINE);
                    }
                } else {
                    if (!getThing().getStatus().equals(ThingStatus.OFFLINE)) {
                        updateStatus(ThingStatus.OFFLINE);
                    }
                    logger.info("{} Could not be set online ", getThing().getUID());
                }
            }

        };

        refreshJob = scheduler.scheduleWithFixedDelay(checkDevice, 20, 300, TimeUnit.SECONDS);

    }

    @Override
    public void setCallback(ThingHandlerCallback thingHandlerCallback) {
        // TODO Auto-generated method stub
        logger.info("ThingHandlerCallBack was called for {}", this.thing.getUID());
        super.setCallback(thingHandlerCallback);
    }

    private void startAutomaticRefresh() {

        logger.debug("startAutomaticRefresh()");

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                logger.debug("The scheduled thread is running ! ");

                getBridgeHandler().sendAsyncCommandToDevice(xbee64BitsAddress, "b");

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                getBridgeHandler().sendAsyncCommandToDevice(xbee64BitsAddress, "[");
            }
        };

        refreshJob = scheduler.scheduleWithFixedDelay(runnable, 120, 60, TimeUnit.SECONDS);
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        // can be overridden by subclasses

        logger.info("handle ConfigurationUpdate for :", this.thing.getUID());
        logger.info("handle ConfigurationUpdate for :", this.xbee64BitsAddress);

        thingConfig = editConfiguration();

        // if(configurationParameters.containsKey(key))
        xbee64BitsAddress = new XBee64BitAddress(configurationParameters.get("Xbee64BitsAddress").toString());

        logger.info("handle ConfigurationUpdate for :", this.xbee64BitsAddress);

        for (Entry<String, Object> configurationParameter : configurationParameters.entrySet()) {

            logger.info("updating : {} with : {} ", configurationParameter.getKey(), configurationParameter.getValue());
            thingConfig.put(configurationParameter.getKey(), configurationParameter.getValue());

            if (configurationParameter.getKey().equals("ResetXBee") && configurationParameter.getValue().equals(true)) {
                // scheduler.execute(new resetXbeeDevice());
                logger.info("doing something to Reset device {} ", getThing().getUID());
                getBridgeHandler().resetXBeeDevice(xbee64BitsAddress);

                updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.HANDLER_CONFIGURATION_PENDING,
                        "In handleConfigurationUpdate() method");

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                IOsMapping = getBridgeHandler().getXBeeDeviceIOLineConfig(xbee64BitsAddress);

                logger.debug("Checking Device {} is on the network ! ", xbee64BitsAddress);

                for (IOLineIOModeMapping map : IOsMapping) {
                    logger.trace("IOLIne : {} IOMode : {} ", map.getIoLine(), map.getIoMode());
                }

                logger.trace("IOsMapping has : {} Lines", IOsMapping.size());

                if (!IOsMapping.isEmpty()) {
                    if (!getThing().getStatus().equals(ThingStatus.ONLINE)) {
                        updateStatus(ThingStatus.ONLINE);
                    }
                    logger.info("device {} looks to be OK", getThing().getUID());
                } else {
                    if (!getThing().getStatus().equals(ThingStatus.OFFLINE)) {
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
                    }
                    logger.info("{} Could not be set online ", getThing().getUID());
                }

                logger.info("thingConfig.put(\"Reset\", false);");
                thingConfig.put("ResetXBee", false);
            }

            if (configurationParameter.getKey().equals("XBeeDeviceTypeID")) {
                // this.getBridgeHandler().setRemoteATConfig(xbee64BitsAddress, "DD", "CAFE0EDF".getBytes());
                // ((String) configurationParameter.getValue()).getBytes());
                logger.warn("Setting : {} to : {}", configurationParameter.getKey(), configurationParameter.getValue());

            }

        }

        updateConfiguration(thingConfig);

    }

    /**
     * A thread to do something.....
     */
    private class resetXbeeDevice implements Runnable {

        @Override
        public void run() {
            // https://community.openhab.org/t/oh2-major-bug-with-scheduled-jobs/12350/11
            // If any execution of the task encounters an exception, subsequent executions are
            // suppressed. Otherwise, the task will only terminate via cancellation or
            // termination of the executor.

            try {
                // XBee bridge have to be ONLINE because configuration is needed

                SNAVXbee2BridgeHandler bridgeHandler = getBridgeHandler();
                if (bridgeHandler == null || !bridgeHandler.getThing().getStatus().equals(ThingStatus.ONLINE)) {
                    logger.warn("bridge handler not found or not ONLINE.");
                    return;
                }

                logger.info("doing something to Reset device {} ", getThing().getUID());
                getBridgeHandler().resetXBeeDevice(xbee64BitsAddress);

                updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.HANDLER_CONFIGURATION_PENDING,
                        "In handleConfigurationUpdate() method");

                Thread.sleep(10000);

                IOsMapping = getBridgeHandler().getXBeeDeviceIOLineConfig(xbee64BitsAddress);

                logger.debug("Checking Device {} is on the network ! ", xbee64BitsAddress);

                for (IOLineIOModeMapping map : IOsMapping) {
                    logger.trace("IOLIne : {} IOMode : {} ", map.getIoLine(), map.getIoMode());
                }

                logger.trace("IOsMapping has : {} Lines", IOsMapping.size());
                if (!IOsMapping.isEmpty()) {
                    if (!getThing().getStatus().equals(ThingStatus.ONLINE)) {
                        updateStatus(ThingStatus.ONLINE);
                    }
                    logger.info("device {} looks to be OK", getThing().getUID());
                } else {
                    if (!getThing().getStatus().equals(ThingStatus.OFFLINE)) {
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
                    }
                    logger.info("{} Could not be set online ", getThing().getUID());
                }

                logger.info("thingConfig.put(\"Reset\", false);");
                thingConfig.put("ResetXBee", false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
