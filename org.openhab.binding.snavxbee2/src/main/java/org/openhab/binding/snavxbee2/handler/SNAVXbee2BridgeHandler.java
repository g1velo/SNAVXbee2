package org.openhab.binding.snavxbee2.handler;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.thing.binding.firmware.Firmware;
import org.eclipse.smarthome.core.thing.binding.firmware.FirmwareUpdateHandler;
import org.eclipse.smarthome.core.thing.binding.firmware.ProgressCallback;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.snavxbee2.devices.Tosr0xTparser;
import org.openhab.binding.snavxbee2.utils.ChannelActionToPerform;
import org.openhab.binding.snavxbee2.utils.IOLineIOModeMapping;
import org.openhab.binding.snavxbee2.utils.RCAndIOValue;
import org.openhab.binding.snavxbee2.utils.XbeeIOSampleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

/**
 * The {@link SNAVXbee2BridgeHandler} class is handling communication to the XBee world
 * using serial port, each SNAVXbee2Handler will pass commands to the bridge to reach the
 * Xbee device in the network
 *
 * @author Stephan NAVARRO - Initial contribution
 */

public class SNAVXbee2BridgeHandler extends BaseBridgeHandler
        implements IDataReceiveListener, IDiscoveryListener, IIOSampleReceiveListener, FirmwareUpdateHandler {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2BridgeHandler.class);
    // PORT parameter Should be update to what is in the Thing Definition
    private static String PORT = "COM2";
    // BAUD_RATE parameter Should be update to what is in the Thing Definition
    private static int BAUD_RATE = 9600;

    private Configuration config = this.getConfig();
    private XBeeDevice myDevice = null;
    private XBeeNetwork xbeeNetwork = null;
    private List<RemoteXBeeDevice> remoteDeviceList;
    private ScheduledFuture<?> refreshJob;
    private ConcurrentMap<String, Timestamp> xBeeList = new ConcurrentHashMap<>();
    private ConcurrentNavigableMap<String, Timestamp> navmap = new ConcurrentSkipListMap();

    public SNAVXbee2BridgeHandler(Bridge bridge) {
        super(bridge);
        // TODO Auto-generated constructor stub
        // logger.debug(" In the brige constructor , can handle commands");
    }

    /**
     * This is the method perform a discovery and return discovered devices.
     *
     * @param timeout set the DiscoveryTimetout.
     * @return List of XBee devices discovered.
     */
    public List<RemoteXBeeDevice> startSearch(int timeout) {

        logger.trace("Start Search in Bridge Handler");

        if (!xbeeNetwork.isDiscoveryRunning()) {

            try {
                xbeeNetwork.clearDeviceList();
                xbeeNetwork.setDiscoveryTimeout(timeout);
                // xbeeNetwork.setDiscoveryOptions(EnumSet.of(DiscoveryOptions.APPEND_DD));
                // .setDiscoveryOptions(EnumSet.of(DiscoveryOptions.APPEND_DD, DiscoveryOptions.DISCOVER_MYSELF));
                xbeeNetwork.addDiscoveryListener(this);
                xbeeNetwork.startDiscoveryProcess();

                while (xbeeNetwork.isDiscoveryRunning()) {
                    logger.trace("Xbee discovery  running : {}", xbeeNetwork.isDiscoveryRunning());
                    Thread.sleep(timeout + 2000);

                    if (this.remoteDeviceList != null) {
                        logger.trace(" remoteDeviceList.size {} ", this.remoteDeviceList.size());
                    }

                }
            } catch (XBeeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                e.getCause();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return xbeeNetwork.getDevices();
    }

    @Override
    public void deviceDiscovered(RemoteXBeeDevice discoveredDevice) {
        // discoveredDevice.getParameter("")
        logger.warn(" devices discoverered : {} {} ", discoveredDevice.getNodeID(), discoveredDevice.get64BitAddress());

    }

    @Override
    public void discoveryError(String error) {
        // TODO Auto-generated method stub
        logger.error(">> There was an error discovering devices: {}", error);
    }

    @Override
    public void discoveryFinished(String error) {
        // TODO Auto-generated method stub
        if (error == null) {
            logger.info(">> Discovery process finished successfully.");
        } else {
            logger.info(">> Discovery process finished due to the following error: {} ", error);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub
    }

    public XBeeNetwork getXbeeNetwork() {
        return this.xbeeNetwork;
    }

    public RCAndIOValue getIOLineValue(XBee64BitAddress xbee64BitsAddress, IOLine ioLine) {

        RCAndIOValue rcValue = new RCAndIOValue();

        RemoteXBeeDevice remoteDevice = xbeeNetwork.getDevice(xbee64BitsAddress);
        try {
            rcValue.setIovalue(remoteDevice.getDIOValue(ioLine));
            rcValue.setRc(true);
        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            rcValue.setRc(false);
            e.printStackTrace();
        }

        return rcValue;
    }

    public List<IOLineIOModeMapping> getXBeeDeviceIOLineConfig(XBee64BitAddress xbee64BitsAddress) {
        List<IOLineIOModeMapping> IOsMapping = new ArrayList<>();

        while (xbeeNetwork.isDiscoveryRunning()) {
            logger.trace("Xbee discoverin running : {}", xbeeNetwork.isDiscoveryRunning());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (this.remoteDeviceList != null) {
                logger.trace(" remoteDeviceList.size {} ", this.remoteDeviceList.size());
            }
        }

        RemoteXBeeDevice remoteDevice = xbeeNetwork.getDevice(xbee64BitsAddress);

        for (IOLine ioLine : IOLine.values()) {

            logger.trace("getting IOLine : {} for device : {} ", ioLine, remoteDevice.get64BitAddress());

            try {
                IOLineIOModeMapping mapping = new IOLineIOModeMapping(ioLine, remoteDevice.getIOConfiguration(ioLine));
                IOsMapping.add(mapping);
            } catch (ATCommandException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                logger.trace("IOLine : {} is not supported on device {} ", ioLine, xbee64BitsAddress);
            } catch (TimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XBeeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return IOsMapping;
    }

    public void resetXBeeDevice(XBee64BitAddress xbee64BitsAddress) {
        RemoteXBeeDevice remoteDevice = xbeeNetwork.getDevice(xbee64BitsAddress);
        logger.debug("reseting device !! : {} ", remoteDevice.get64BitAddress());
        try {
            remoteDevice.reset();
        } catch (XBeeException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    public void sendSyncCommandToDevice(XBee64BitAddress xbee64BitsAddress, String xbeeCommmand) {

        logger.debug("Sending Sync command : {} to : {} ", xbeeCommmand, xbee64BitsAddress);

        try {
            RemoteXBeeDevice remoteDevice = xbeeNetwork.getDevice(xbee64BitsAddress);
            if (remoteDevice != null) {
                myDevice.sendData(remoteDevice, xbeeCommmand.getBytes());
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
            logger.trace("Is device open ? {} ", myDevice.isOpen());
            logger.trace("Is device receive timeout ? {}  ", myDevice.getReceiveTimeout());
            logger.trace("Exception cause ? {} ", e.getCause());
            logger.trace("Exception message ? {} ", e.getMessage());
            logger.trace("Discovery running ? ? {} ", xbeeNetwork.isDiscoveryRunning());
            logger.trace("resetting ");
            resetXBeeDevice(xbee64BitsAddress);
            if (!xbeeNetwork.isDiscoveryRunning()) {
                logger.trace("running Discovery {} ");
                xbeeNetwork.startDiscoveryProcess();
            }
        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getFirmwareLevel(XBee64BitAddress xbee64BitsAddress) {

        RemoteXBeeDevice getFWRemoteDevice = xbeeNetwork.getDevice(xbee64BitsAddress);
        getFWRemoteDevice.getFirmwareVersion();
        return getFWRemoteDevice.getFirmwareVersion();

    }

    public void sendAsyncCommandToDevice(XBee64BitAddress xbee64BitsAddress, String xbeeCommmand) {

        logger.debug(" sending Async command : {} to : {} ", xbeeCommmand, xbee64BitsAddress);

        try {
            RemoteXBeeDevice remoteDevice = xbeeNetwork.getDevice(xbee64BitsAddress);
            if (remoteDevice != null) {
                myDevice.sendDataAsync(remoteDevice, xbeeCommmand.getBytes());
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
            logger.trace("Is device open ? {} ", myDevice.isOpen());
            logger.trace("Is device receive timeout ? {}  ", myDevice.getReceiveTimeout());
            logger.trace("Exception cause ? {} ", e.getCause());
            logger.trace("Exception message ? {} ", e.getMessage());
            logger.trace("Discovery running ? ? {} ", xbeeNetwork.isDiscoveryRunning());
            logger.trace("resetting ");
            resetXBeeDevice(xbee64BitsAddress);
            if (!xbeeNetwork.isDiscoveryRunning()) {
                logger.trace("running Discovery {} ");
                xbeeNetwork.startDiscoveryProcess();
            }
            // this.initialize();
        }

        catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * This method is used to change Xbee device IOLine Value
     * the IOLine has to be configured as DIGITAL_OUT.
     *
     * @param xbee64BitsAddress is XBee Device XBee64BitAddress to update
     * @param ioline is the IOLine to update
     * @param iovalue is the IOValue
     * @return Nothing.
     */
    public void sendAPICommandToDevice(XBee64BitAddress xbee64BitsAddress, IOLine ioline, IOValue iovalue) {

        RemoteXBeeDevice remoteDevice = xbeeNetwork.getDevice(xbee64BitsAddress);

        try {
            remoteDevice.setDIOValue(ioline, iovalue);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public byte[] getRemoteATConfig(XBee64BitAddress xbee64BitsAddress, String parameter) {

        byte[] b = null;
        RemoteXBeeDevice remoteDevice = xbeeNetwork.getDevice(xbee64BitsAddress);

        try {

            logger.trace("looking Parameter : {} from  : {} with value {} ", parameter, xbee64BitsAddress.toString());

            b = remoteDevice.getParameter(parameter);

        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return b;
    }

    public void setRemoteATConfig(XBee64BitAddress xbee64BitsAddress, String parameter, byte[] value) {

        RemoteXBeeDevice remoteDevice = xbeeNetwork.getDevice(xbee64BitsAddress);

        try {

            logger.warn("Changing parameter : {} to : {} with value {} ", parameter, xbee64BitsAddress.toString(),
                    value);

            // remoteDevice.setParameter(parameter, value);
            logger.warn(" remoteDevice.setParameter(\"DD\", \"CAFE0EDF\".getBytes());");
            remoteDevice.enableApplyConfigurationChanges(true);
            remoteDevice.setParameter("DD", "CAFE0EDF".getBytes());
            // remoteDevice.set
            remoteDevice.applyChanges();

            logger.warn("DD is : ", remoteDevice.getParameter("DD"));

        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {

        logger.info("handling Bridge Configuration Update");

        Configuration configuration = editConfiguration();

        for (Entry<String, Object> configurationParameter : configurationParameters.entrySet()) {

            logger.info("updating : {} with : {} ", configurationParameter.getKey(), configurationParameter.getValue());
            configuration.put(configurationParameter.getKey(), configurationParameter.getValue());

            if (configurationParameter.getKey().equals("ResetXBee") && configurationParameter.getValue().equals(true)) {
                logger.warn("We need to do something for this Bridge");

                this.dispose();
                this.initialize();

                /*
                 * try {
                 * myDevice.reset();
                 * } catch (TimeoutException e) {
                 * // TODO Auto-generated catch block
                 * e.printStackTrace();
                 * } catch (XBeeException e) {
                 * // TODO Auto-generated catch block
                 * e.printStackTrace();
                 * }
                 */
            }
        }

        updateConfiguration(configuration);

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        logger.debug(" In the brige, Disposing config : " + config.get("SerialPortName").toString() + " "
                + Integer.valueOf(config.get("SerialPortSpeed").toString()));

        refreshJob.cancel(true);
        myDevice.removeDataListener(this);
        myDevice.removeIOSampleListener(this);
        xbeeNetwork.removeDiscoveryListener(this);
        myDevice.close();
        super.dispose();
    }

    @Override
    public void initialize() {

        PORT = config.get("SerialPortName").toString();
        BAUD_RATE = Integer.valueOf(config.get("SerialPortSpeed").toString());

        logger.debug(" In the brige, Initialising config : " + PORT + " " + BAUD_RATE);

        try {

            myDevice = new XBeeDevice(PORT, BAUD_RATE);

            myDevice.open();

            if (myDevice.isOpen()) {
                logger.debug("Port {} open, ", PORT);
                updateStatus(ThingStatus.ONLINE);
                startAutomaticRefresh();
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Port " + PORT + " was not opened");
            }

            logger.debug("------------------------------------------------------------ Local Node ID : {} ",
                    myDevice.getNodeID());

            myDevice.addDataListener(this);
            myDevice.addIOSampleListener(this);
            this.xbeeNetwork = myDevice.getNetwork();
            myDevice.setReceiveTimeout(2000);

            xbeeNetwork.startDiscoveryProcess();

        } catch (XBeeException e) {
            e.printStackTrace();
            logger.error("XBeeException Occured");
            logger.error(e.getMessage());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "had an XBeeException");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception Occured");
            logger.error(e.getMessage());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "had another exception");
        }

        /*
         * try {
         * Thread.sleep(1000);
         * } catch (InterruptedException e) {
         * // TODO Auto-generated catch block
         * e.printStackTrace();
         * }
         */

    }

    /**
     * This method is listening data coming from the Xbee Coordinator
     * serial port.
     * Parse it to update the right item.
     *
     * @param xbeeMessage is the message received on the serial port
     * @return Nothing.
     */
    @Override
    public void dataReceived(XBeeMessage xbeeMessage) {

        Thing thingToUpdate = null;
        ThingUID thingUIDToUpdate = null;

        logger.trace("data Received from : {} : {}", xbeeMessage.getDevice().get64BitAddress().toString(),
                xbeeMessage.getData());

        Collection<Thing> things = getThing().getThings();

        String xbeeAddressToLookup = xbeeMessage.getDevice().get64BitAddress().toString();
        navmap.put(xbeeAddressToLookup, new Timestamp(System.currentTimeMillis()));

        // creating a list XBee devices with timestamp
        if (xBeeList.containsKey(xbeeAddressToLookup)) {
            xBeeList.replace(xbeeAddressToLookup, new Timestamp(System.currentTimeMillis()));
        } else {
            xBeeList.put(xbeeAddressToLookup, new Timestamp(System.currentTimeMillis()));
        }

        // Looking up for thing with the matching address
        for (Thing thing : things) {

            if (thing.getConfiguration().containsKey("Xbee64BitsAddress")
                    && thing.getConfiguration().get("Xbee64BitsAddress").equals(xbeeAddressToLookup)) {

                thingUIDToUpdate = thing.getUID();
                thingToUpdate = thing;

                // Per ThingType action
                if (thing.getThingTypeUID().equals(THING_TYPE_TOSR0XT)) {
                    // logger.trace("we have to update {} {} ", thing.getUID(), thing.getThingTypeUID());

                    Tosr0xTparser tp = new Tosr0xTparser(thingToUpdate, xbeeMessage);
                    ArrayList<ChannelActionToPerform> listOfActionToPerform = tp.getListOfChannelActionToPerform();

                    // logger.trace("THING_TYPE_TOSR0XT : {} ", listOfActionToPerform.size());

                    for (ChannelActionToPerform actionToPerform : listOfActionToPerform) {
                        logger.trace("channel to update : {} to value : {} ", actionToPerform.getChannelUIDToUpdate(),
                                actionToPerform.getState());
                        updateState(actionToPerform.getChannelUIDToUpdate(), actionToPerform.getState());
                    }

                }

                if (thing.getThingTypeUID().equals(THING_TYPE_SAMPLE)) {
                }

                // if (thing.getThingTypeUID().equals(THING_TYPE_CAFE0EDF)) {
                if (SUPPORTED_THING_TYPES_CAFE0EDF.contains(thing.getThingTypeUID())) {
                    // return;
                    logger.trace("thing.getUID() : {} ", thing.getUID());
                    SNAVXbee2HandlerCafe0EDF cafe0edf = (SNAVXbee2HandlerCafe0EDF) thingToUpdate.getHandler();
                    cafe0edf.sendMessage(xbeeMessage.getData());
                }
            }
        }
    }

    @Override
    public void ioSampleReceived(RemoteXBeeDevice remoteDevice, IOSample ioSample) {
        // TODO Auto-generated method stub

        logger.trace("New Sample received from " + remoteDevice.get64BitAddress() + " - " + ioSample);
        Collection<Thing> things = thingRegistry.getAll();

        for (Thing thing : things) {
            // logger.trace("New sample received from " + remoteDevice.get64BitAddress() + " - " + ioSample);

            if (thing.getConfiguration().containsKey("Xbee64BitsAddress") && thing.getConfiguration()
                    .get("Xbee64BitsAddress").equals(remoteDevice.get64BitAddress().toString())) {

                logger.trace("we have to update {} {} ", thing.getUID(), thing.getThingTypeUID());

                XbeeIOSampleParser xbeeSampler = new XbeeIOSampleParser(thing, ioSample);
                ArrayList<ChannelActionToPerform> listOfActionToPerform = xbeeSampler.getListOfChannelActionToPerform();

                for (ChannelActionToPerform actionToPerform : listOfActionToPerform) {
                    logger.trace("channel to update : {} to value : {} ", actionToPerform.getChannelUIDToUpdate(),
                            actionToPerform.getState());
                    updateState(actionToPerform.getChannelUIDToUpdate(), actionToPerform.getState());
                }
            }
        }
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        // TODO Auto-generated method stub
        logger.info("childHandlerInitialized for {}", childThing.getUID());

        super.childHandlerInitialized(childHandler, childThing);
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        // TODO Auto-generated method stub
        logger.info("childHandlerDisposed for {}", childThing.getUID());
        super.childHandlerDisposed(childHandler, childThing);
    }

    private void startAutomaticRefresh() {

        logger.debug("bridge startAutomaticRefresh()");

        Runnable bridgediscovery = new Runnable() {

            @Override
            public void run() {
                logger.trace("automatic discovery started : {} ", xbeeNetwork.isDiscoveryRunning());
                if (!xbeeNetwork.isDiscoveryRunning()) {
                    logger.trace(" Bridge Automatic running Discovery");
                    xbeeNetwork.startDiscoveryProcess();
                }
            }
        };

        Runnable checkXBeeNetwork = new Runnable() {

            @Override
            public void run() {
                // TODO Don't know yet what to do here ... Sure it will come !

                logger.warn("WatchDog checking : {} ", myDevice.getNodeID());
                myDevice.getConnectionInterface();

                if (!myDevice.isOpen()) {
                    logger.error("Device {} is closed", PORT);
                    try {
                        myDevice.open();
                        myDevice.getHardwareVersion();
                    } catch (XBeeException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    logger.debug("WatchDog checked, local device is open");
                }

                logger.trace("navmap size = {} ", navmap.size());
                Collection<Timestamp> lastseen = navmap.values();
                Timestamp t = null;

                for (Timestamp ts : lastseen) {
                    if (t == null) {
                        // logger.warn("t is Null");
                        t = ts;
                        // logger.warn("t is {} ", t);
                    } else {
                        if (t.before(ts)) {
                            t = ts;
                        }
                    }
                }

                if (t != null) {
                    logger.trace("t : {} ", t);
                }

                for (Thing th : thingRegistry.getAll()) {
                    String xbeeAddress = (String) th.getConfiguration().get("Xbee64BitsAddress");

                    if (!th.getThingTypeUID().equals(THING_TYPE_BRIDGE)) {

                        logger.debug("thing : {} {} ", th.getUID(), new Timestamp(System.currentTimeMillis() - 60000));
                        logger.debug("found Xbee address {} at time {} ", xbeeAddress, navmap.get(xbeeAddress));

                        if (navmap.get(xbeeAddress).before(new Timestamp(System.currentTimeMillis() - 60000))) {
                            // TODO What should we do if we didn't hear from a device since a while
                            logger.debug("devices {} didn't talk since {}", th.getUID(), navmap.get(xbeeAddress));

                        } else {
                            logger.debug("devices {} is under threshold", th.getUID());
                        }
                        // logger.warn("found ThingUID {} at time {} ", th.getUID(), navmap.get(xbeeAddress));
                    }

                }

            }

        };

        refreshJob = scheduler.scheduleWithFixedDelay(bridgediscovery, 120, 300, TimeUnit.SECONDS);
        refreshJob = scheduler.scheduleWithFixedDelay(checkXBeeNetwork, 30, 120, TimeUnit.SECONDS);
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

    @Override
    public void setCallback(ThingHandlerCallback thingHandlerCallback) {
        // TODO Auto-generated method stub
        logger.debug("set Callback for : ", this.thing.getBridgeUID());
        super.setCallback(thingHandlerCallback);
    }

    private class ConfigureDevice implements Runnable {
        @Override
        public void run() {
            logger.debug("Starting Parsing Data for CAFEEDF0 : {}");
        }

    };

}