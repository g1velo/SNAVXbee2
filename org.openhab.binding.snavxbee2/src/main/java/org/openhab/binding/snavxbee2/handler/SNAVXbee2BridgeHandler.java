package org.openhab.binding.snavxbee2.handler;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.THING_TYPE_TOSR0XT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

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
import org.openhab.binding.snavxbee2.utils.SerialPortConfigParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.models.DiscoveryOptions;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

public class SNAVXbee2BridgeHandler extends BaseBridgeHandler implements IDataReceiveListener, IDiscoveryListener {

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

    public SNAVXbee2BridgeHandler(Bridge bridge) {
        super(bridge);
        // TODO Auto-generated constructor stub
        // logger.debug(" In the brige constructor , can handle commands");
    }

    public List<RemoteXBeeDevice> startSearch(int timeout) {

        logger.debug("StartSerach in Bridge Handler 1");

        if (!xbeeNetwork.isDiscoveryRunning()) {
            try {
                xbeeNetwork.setDiscoveryTimeout(timeout);
                xbeeNetwork
                        .setDiscoveryOptions(EnumSet.of(DiscoveryOptions.APPEND_DD, DiscoveryOptions.DISCOVER_MYSELF));
                xbeeNetwork.addDiscoveryListener(this);
                xbeeNetwork.startDiscoveryProcess();

                while (xbeeNetwork.isDiscoveryRunning()) {
                    logger.debug("Xbee discoverin running 3: {}", xbeeNetwork.isDiscoveryRunning());
                    Thread.sleep(timeout + 1000);

                    if (this.remoteDeviceList != null) {
                        logger.debug(" remoteDeviceList.size {} ", this.remoteDeviceList.size());
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

            // logger.debug("Number of dev : {} {} ", xbeeNetwork.getNumberOfDevices(), xbeeNetwork.getDevices());

            if (this.remoteDeviceList != null) {
                logger.debug(" 1 remoteDeviceList.size {} ", this.remoteDeviceList.size());
            } else {
                logger.debug(" remoteDeviceList.size is empty ! ");

            }
        }

        return xbeeNetwork.getDevices();
    }

    @Override
    public void deviceDiscovered(RemoteXBeeDevice discoveredDevice) {

        logger.debug(" devices discoverered  12 : {} {}", discoveredDevice.getNodeID(),
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

    public boolean sendCommandToDevice(XBee64BitAddress xbee64BitsAddress, String xbeeCommmand) {

        logger.debug(" 111 sending command : {} to : {} ", xbeeCommmand, xbee64BitsAddress);

        try {
            // logger.debug("0013A20040E31560 start getting RemoteXbeeDevice : {} with command {} ", xbee64BitsAddress,
            // xbeeCommmand);

            // XBee64BitAddress x64 = new XBee64BitAddress(HexUtils.hexStringToByteArray(xbee64BitsAddress));
            RemoteXBeeDevice remoteDevice = xbeeNetwork.getDevice(xbee64BitsAddress);

            if (remoteDevice != null) {

                logger.debug("222 device Name : {} ", remoteDevice.get64BitAddress());
                // RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice("LULUTEMP");

                // logger.debug("start sending command : {} ", xbeeCommmand);
                myDevice.sendData(remoteDevice, xbeeCommmand.getBytes());
                // logger.debug("end sending command : {} ", xbeeCommmand);

            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            logger.debug("Is device open ? {} ", myDevice.isOpen());
            logger.debug("Is device receive timeout ? {}  / closing now ", myDevice.getReceiveTimeout());
            logger.debug("Exception cause ? {} ", e.getCause());
            logger.debug("Exception message ? {} ", e.getMessage());
            if (!xbeeNetwork.isDiscoveryRunning()) {
                xbeeNetwork.startDiscoveryProcess();
            }
            // this.initialize();
        }

        catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        logger.debug(" In the brige, Disposing config : " + portconfig.serialPort + " " + portconfig.baudRate);
        if (xbeeNetwork.isDiscoveryRunning()) {
            xbeeNetwork.removeDiscoveryListener(this);
        }
        super.dispose();
    }

    // public boolean unregisterLightStatusListener(LightStatusListener lightStatusListener) {
    public boolean unregisterLightStatusListener() {
        /*
         * boolean result = lightStatusListeners.remove(lightStatusListener);
         * if (result) {
         * onUpdate();
         * }
         * return result;
         */
        return true;
    }

    @Override
    public void initialize() {

        // TODO Auto-generated method stub
        // to remove
        // updateStatus(ThingStatus.ONLINE);

        // config = this.getConfig();

        String port = (String) config.get("serialPort");
        String baud = config.get("baudRate").toString();

        portconfig.baudRate = baud;
        portconfig.serialPort = port;

        PORT = portconfig.serialPort;
        BAUD_RATE = Integer.valueOf(portconfig.baudRate);

        logger.debug(" In the brige, Initialising config : " + portconfig.serialPort + " " + portconfig.baudRate);

        try {

            myDevice = new XBeeDevice(PORT, BAUD_RATE);

            myDevice.open();

            // logger.debug(" Local Node ID : {} ", myDevice.getNodeID());

            myDevice.addDataListener(this);
            this.xbeeNetwork = myDevice.getNetwork();

            xbeeNetwork.startDiscoveryProcess();
            // Just for fun and testing !
            // should be removed for production
            // long startTime = System.nanoTime();
            // RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice("LULUTEMP");
            // RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice("0013A20040E31560");
            // long estimatedTime = System.nanoTime() - startTime;

            // logger.debug("xbee address : {} ", remoteDevice.get64BitAddress());
            // logger.debug("getting remote devices took : {} ms", estimatedTime / 1000 / 1000);

            // String xbeeCmd = "n";
            // myDevice.sendData(remoteDevice, xbeeCmd.getBytes());

            // Thread.sleep(500);

            // xbeeCmd = "d";
            // myDevice.sendData(remoteDevice, xbeeCmd.getBytes());

            // Thread.sleep(500);

            // xbeeCmd = "b";
            // myDevice.sendData(remoteDevice, xbeeCmd.getBytes());

            // sendCommandToDevice(remoteDevice.get64BitAddress().toString(), xbeeCmd);

            // if (remoteDevice == null) {
            // logger.debug("Couldn't find the remote XBee device with '" + "LULUTEMP" + "' Node Identifier.");
            // System.exit(1);
            // }

            // }
        } catch (XBeeException e) {
            e.printStackTrace();
            // System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            // System.exit(1);
        }

        if (myDevice.isOpen()) {
            // logger.debug("Port {} open, ", PORT);
            updateStatus(ThingStatus.ONLINE);
        }
    }

    @Override
    public void dataReceived(XBeeMessage xbeeMessage) {
        // TODO Auto-generated method stub

        Thing thingToUpdate = null;
        ThingUID thingUIDToUpdate = null;
        String channelToUpdate = null;

        logger.debug("Data Received 1  ");

        Collection<Thing> things = thingRegistry.getAll();
        // Collection<Thing> things = thingRegistry.get;

        // logger.debug(" number of things in the collection : {} ", things.size());

        String xbeeAddressToLookup = xbeeMessage.getDevice().get64BitAddress().toString();

        // logger.debug("address to lookup : {} ", xbeeAddressToLookup);

        // Looking up for thing with the matching address
        for (Thing thing : things) {

            logger.debug(" tuid : {} ", thing.getThingTypeUID());

            if (thing.getConfiguration().containsKey("Xbee64BitsAddress")
                    && thing.getConfiguration().get("Xbee64BitsAddress").equals(xbeeAddressToLookup)) {

                if (thing.getThingTypeUID().equals(THING_TYPE_TOSR0XT)) {
                    logger.debug("we have to update {} {} ", thing.getUID(), thing.getThingTypeUID());

                    thingUIDToUpdate = thing.getUID();
                    thingToUpdate = thing;

                    Tosr0xTparser tp = new Tosr0xTparser(thingToUpdate, xbeeMessage);

                    ArrayList<ChannelActionToPerform> listOfActionToPerform = tp.getListOfChannelActionToPerform();

                    for (ChannelActionToPerform actionToPerform : listOfActionToPerform) {
                        updateState(actionToPerform.getChannelUIDToUpdate(), actionToPerform.getState());
                    }

                }
            }

        }
    }

}
