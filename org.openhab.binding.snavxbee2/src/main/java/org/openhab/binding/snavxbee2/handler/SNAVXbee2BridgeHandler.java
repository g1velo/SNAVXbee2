package org.openhab.binding.snavxbee2.handler;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.snavxbee2.devices.Tosr0xTparser;
import org.openhab.binding.snavxbee2.utils.ChannelActionToPerform;
import org.openhab.binding.snavxbee2.utils.IOLineIOModeMapping;
import org.openhab.binding.snavxbee2.utils.RCAndIOValue;
import org.openhab.binding.snavxbee2.utils.SerialPortConfigParameters;
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

public class SNAVXbee2BridgeHandler extends BaseBridgeHandler
        implements IDataReceiveListener, IDiscoveryListener, IIOSampleReceiveListener {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2BridgeHandler.class);
    private SerialPortConfigParameters portconfig = new SerialPortConfigParameters();
    // TODO Replace with the serial port where your receiver module is connected.
    private static String PORT = "COM2";
    // TODO Replace with the baud rate of you receiver module.
    private static int BAUD_RATE = 9600;

    private Configuration config = this.getConfig();
    private XBeeDevice myDevice = null;
    private XBeeNetwork xbeeNetwork = null;
    private List<RemoteXBeeDevice> remoteDeviceList;
    private ScheduledFuture<?> refreshJob;

    public SNAVXbee2BridgeHandler(Bridge bridge) {
        super(bridge);
        // TODO Auto-generated constructor stub
        // logger.debug(" In the brige constructor , can handle commands");
    }

    public List<RemoteXBeeDevice> startSearch(int timeout) {

        logger.trace("StartSerach in Bridge Handler 1");

        if (!xbeeNetwork.isDiscoveryRunning()) {

            try {
                xbeeNetwork.setDiscoveryTimeout(timeout);
                // xbeeNetwork.setDiscoveryOptions(EnumSet.of(DiscoveryOptions.APPEND_DD));

                // .setDiscoveryOptions(EnumSet.of(DiscoveryOptions.APPEND_DD, DiscoveryOptions.DISCOVER_MYSELF));
                xbeeNetwork.addDiscoveryListener(this);
                xbeeNetwork.startDiscoveryProcess();

                while (xbeeNetwork.isDiscoveryRunning()) {
                    logger.trace("Xbee discoverin running 3: {}", xbeeNetwork.isDiscoveryRunning());
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

        logger.trace(" devices discoverered  12 : {} {}", discoveredDevice.getNodeID(),
                discoveredDevice.get64BitAddress());

    }

    @Override
    public void discoveryError(String error) {
        // TODO Auto-generated method stub
        logger.info(">> There was an error discovering devices: {}", error);
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
            logger.trace("Xbee discoverin running 3: {}", xbeeNetwork.isDiscoveryRunning());
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

    public synchronized void sendCommandToDevice(XBee64BitAddress xbee64BitsAddress, String xbeeCommmand) {

        logger.debug(" 111 Synced  sending command : {} to : {} ", xbeeCommmand, xbee64BitsAddress);

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

        // return false;
    }

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

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        logger.debug(" In the brige, Disposing config : " + portconfig.serialPort + " " + portconfig.baudRate);
        // xbeeNetwork.lis
        refreshJob.cancel(true);
        myDevice.removeDataListener(this);
        myDevice.removeIOSampleListener(this);
        xbeeNetwork.removeDiscoveryListener(this);
        myDevice.close();
        super.dispose();
    }

    @Override
    public void initialize() {

        // TODO Auto-generated method stub
        // to remove
        // updateStatus(ThingStatus.ONLINE);

        String port = (String) config.get("serialPort");
        String baud = config.get("baudRate").toString();

        portconfig.baudRate = baud;
        portconfig.serialPort = port;

        PORT = portconfig.serialPort;
        BAUD_RATE = Integer.valueOf(portconfig.baudRate);

        logger.trace(" SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS ");
        logger.trace(" In the brige, Initialising config : " + portconfig.serialPort + " " + portconfig.baudRate);

        try {

            myDevice = new XBeeDevice(PORT, BAUD_RATE);

            myDevice.open();

            logger.debug("------------------------------------------------------------ Local Node ID : {} ",
                    myDevice.getNodeID());

            myDevice.addDataListener(this);
            myDevice.addIOSampleListener(this);
            this.xbeeNetwork = myDevice.getNetwork();

            xbeeNetwork.startDiscoveryProcess();

        } catch (XBeeException e) {
            e.printStackTrace();
            updateStatus(ThingStatus.UNINITIALIZED);
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus(ThingStatus.UNINITIALIZED);
        }

        if (myDevice.isOpen()) {
            logger.debug("Port {} open, ", PORT);
            updateStatus(ThingStatus.ONLINE);
            startAutomaticRefresh();
        } else {
            updateStatus(ThingStatus.UNINITIALIZED);
        }
    }

    @Override
    public void dataReceived(XBeeMessage xbeeMessage) {
        // TODO Auto-generated method stub

        Thing thingToUpdate = null;
        ThingUID thingUIDToUpdate = null;
        String channelToUpdate = null;

        logger.trace("dataReceived bridge method");

        Collection<Thing> things = thingRegistry.getAll();

        logger.trace(" number of things in the collection : {} ", things.size());

        String xbeeAddressToLookup = xbeeMessage.getDevice().get64BitAddress().toString();

        // Looking up for thing with the matching address
        for (Thing thing : things) {

            logger.trace(" tuid found in thing registry : {} ", thing.getThingTypeUID());

            if (thing.getConfiguration().containsKey("Xbee64BitsAddress")
                    && thing.getConfiguration().get("Xbee64BitsAddress").equals(xbeeAddressToLookup)) {

                if (thing.getThingTypeUID().equals(THING_TYPE_TOSR0XT)) {
                    logger.trace("we have to update {} {} ", thing.getUID(), thing.getThingTypeUID());

                    thingUIDToUpdate = thing.getUID();
                    thingToUpdate = thing;

                    Tosr0xTparser tp = new Tosr0xTparser(thingToUpdate, xbeeMessage);

                    ArrayList<ChannelActionToPerform> listOfActionToPerform = tp.getListOfChannelActionToPerform();

                    for (ChannelActionToPerform actionToPerform : listOfActionToPerform) {
                        logger.trace("channel to update : {} to value : {} ", actionToPerform.getChannelUIDToUpdate(),
                                actionToPerform.getState());
                        updateState(actionToPerform.getChannelUIDToUpdate(), actionToPerform.getState());
                    }
                }
                if (thing.getThingTypeUID().equals(THING_TYPE_SAMPLE)) {
                }
            }
        }
    }

    @Override
    public void ioSampleReceived(RemoteXBeeDevice remoteDevice, IOSample ioSample) {
        // TODO Auto-generated method stub

        logger.debug("New sample received from " + remoteDevice.get64BitAddress() + " - " + ioSample);
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

    private void startAutomaticRefresh() {

        logger.trace("bridge startAutomaticRefresh()");

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                logger.trace("automatic discovery started : {} ", xbeeNetwork.isDiscoveryRunning());
                if (!xbeeNetwork.isDiscoveryRunning()) {
                    logger.debug(" Bridge Automatic running Discovery 1");
                    xbeeNetwork.startDiscoveryProcess();
                }
            }
        };
        refreshJob = scheduler.scheduleAtFixedRate(runnable, 60, 300, TimeUnit.SECONDS);
    }
}
