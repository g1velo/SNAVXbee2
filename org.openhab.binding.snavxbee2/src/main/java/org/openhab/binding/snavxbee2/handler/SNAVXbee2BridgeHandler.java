package org.openhab.binding.snavxbee2.handler;

import java.util.Collection;
import java.util.List;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.snavxbee2.utils.SerialPortConfigParameters;
import org.openhab.binding.snavxbee2.utils.Tosr0xTMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.discoverdevices.MyDiscoveryListener;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

public class SNAVXbee2BridgeHandler extends BaseBridgeHandler implements IDataReceiveListener, IDiscoveryListener {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2BridgeHandler.class);
    private SerialPortConfigParameters portconfig = new SerialPortConfigParameters();
    // TODO Replace with the serial port where your receiver module is connected.
    private static String PORT = "COM2";
    // TODO Replace with the baud rate of you receiver module.
    private static int BAUD_RATE = 9600;
    private Configuration config = this.getConfig();
    public XBeeDevice myDevice = null;
    public XBeeNetwork xbeeNetwork = null;
    public List<RemoteXBeeDevice> remoteDeviceList = null;

    public SNAVXbee2BridgeHandler(Bridge bridge) {
        super(bridge);
        // TODO Auto-generated constructor stub
        // logger.debug(" In the brige constructor , can handle commands");
    }

    public synchronized List<RemoteXBeeDevice> startSearch(int timeout) {
        logger.debug("StartSerach in Bridge Handler 1");
        try {
            logger.debug("Xbee discoverin running 0: {}", xbeeNetwork.isDiscoveryRunning());
            xbeeNetwork.setDiscoveryTimeout(timeout);
            xbeeNetwork.addDiscoveryListener(this);
            logger.debug("Xbee discoverin running 1: {}", xbeeNetwork.isDiscoveryRunning());
            xbeeNetwork.startDiscoveryProcess();
            logger.debug("Xbee discoverin running 2: {}", xbeeNetwork.isDiscoveryRunning());

            while (xbeeNetwork.isDiscoveryRunning()) {
                logger.debug("Xbee discoverin running 3: {}", xbeeNetwork.isDiscoveryRunning());
                Thread.sleep(1000);
            }
        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            e.getCause();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return remoteDeviceList;
    }

    @Override
    public void deviceDiscovered(RemoteXBeeDevice discoveredDevice) {
        // TODO Auto-generated method stub
        logger.debug(" devices discoverered : {} ", discoveredDevice.getNodeID());
        this.remoteDeviceList.add(discoveredDevice);
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

    public boolean sendCommandToDevice(String xbee64BitsAddress, String xbeeCommmand) {

        logger.debug("sending command : {} to : {} ", xbeeCommmand, xbee64BitsAddress);

        try {
            // logger.debug("0013A20040E31560 start getting RemoteXbeeDevice : {} with command {} ", xbee64BitsAddress,
            // xbeeCommmand);
            RemoteXBeeDevice remoteDevice = xbeeNetwork.getDevice(new XBee64BitAddress(xbee64BitsAddress));
            // RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice("LULUTEMP");

            // logger.debug("start sending command : {} ", xbeeCommmand);
            myDevice.sendData(remoteDevice, xbeeCommmand.getBytes());
            // logger.debug("end sending command : {} ", xbeeCommmand);

        } catch (TimeoutException e) {
            e.printStackTrace();
            logger.debug("Is device open ? {} ", myDevice.isOpen());
            logger.debug("Is device receive timeout ? {}  / closing now ", myDevice.getReceiveTimeout());
            logger.debug("Exception cause ? {} ", e.getCause());
            logger.debug("Exception message ? {} ", e.getMessage());
            // this.initialize();
        }

        catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public synchronized Collection<String> discoverDevices() {

        try {
            xbeeNetwork.setDiscoveryTimeout(15000);
            MyDiscoveryListener discovery = new MyDiscoveryListener();
            xbeeNetwork.addDiscoveryListener(discovery);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        logger.debug(" In the brige, Disposing config : " + portconfig.serialPort + " " + portconfig.baudRate);
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

            // Just for fun and testing !
            // should be removed for production
            long startTime = System.nanoTime();
            RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice("LULUTEMP");
            // RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice("0013A20040E31560");
            long estimatedTime = System.nanoTime() - startTime;

            // logger.debug("xbee address : {} ", remoteDevice.get64BitAddress());
            // logger.debug("getting remote devices took : {} ms", estimatedTime / 1000 / 1000);

            String xbeeCmd = "n";
            myDevice.sendData(remoteDevice, xbeeCmd.getBytes());

            Thread.sleep(500);

            xbeeCmd = "d";
            myDevice.sendData(remoteDevice, xbeeCmd.getBytes());

            Thread.sleep(500);

            xbeeCmd = "b";
            myDevice.sendData(remoteDevice, xbeeCmd.getBytes());

            sendCommandToDevice(remoteDevice.get64BitAddress().toString(), xbeeCmd);

            // if (remoteDevice == null) {
            // logger.debug("Couldn't find the remote XBee device with '" + "LULUTEMP" + "' Node Identifier.");
            // System.exit(1);
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

        ThingUID thingToUpdate = null;
        String channelToUpdate = null;

        // logger.debug("Data Received ");

        Collection<Thing> things = thingRegistry.getAll();

        // logger.debug(" number of things in the collection : {} ", things.size());

        String xbeeAddressToLookup = xbeeMessage.getDevice().get64BitAddress().toString();

        // logger.debug("address to lookup : {} ", xbeeAddressToLookup);

        // Looking up for thing with the matching address
        for (Thing thing : things) {

            // logger.debug(" tuid : {} ", thing.getThingTypeUID());

            if (thing.getConfiguration().containsKey("Xbee64BitsAddress")
                    && thing.getConfiguration().get("Xbee64BitsAddress").equals(xbeeAddressToLookup)) {
                // logger.debug("we have to update {} ", thing.getUID());
                thingToUpdate = thing.getUID();
                Collection<Channel> thingChannels = thing.getChannels();
                for (Channel channel : thingChannels) {
                    // logger.debug(" channel : {} {}", thing.getThingTypeUID(), channel.getUID());

                }

            }

        }

        // logger.info("From 1 : " + xbeeMessage.getDevice().get64BitAddress() + " >> "
        // + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())) + " | "
        // + new String(xbeeMessage.getData()));

        float temperature = 200;
        // logger.debug("xbeemsg length : {} ", xbeeMessage.getData().length);

        if (xbeeMessage.getData().length == 7 || xbeeMessage.getData().length == 2) {
            if (xbeeMessage.getData().length == 7) {

                channelToUpdate = thingToUpdate + ":temperature";

                temperature = Float.valueOf(new String(xbeeMessage.getData()));
                // logger.info(" The temperature is : " + new String(xbeeMessage.getData()));
                // logger.debug("building the thing to update " + BINDING_ID);
                // logger.debug("building the thing to update " + xbeeMessage.getDevice().get64BitAddress());

            }

            if (xbeeMessage.getData().length == 2) {
                // MSB + LSB temp = ( MSB*256 + LSB ) /16
                // logger.info(" The temperature is in HEXA : "
                // + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())));

                Byte msb = xbeeMessage.getData()[0];
                Byte lsb = xbeeMessage.getData()[1];

                temperature = (msb.floatValue() * 256 + lsb.floatValue()) / 16;
                // logger.info(" The temperature is in Float : " + temperature);
                // logger.info(" The temperature msb is in Float : " + msb.floatValue());
                // logger.info(" The temperature lsb is in Float : " + lsb.floatValue());

                channelToUpdate = thingToUpdate + ":temperature";

            }

            // logger.info(" The temperature ??????? : " + temperature);
            // logger.info(" The channel ??????? : " + channelToUpdate);

            // ChannelUID c = new ChannelUID(channelToUpdate);
            // logger.debug("updating : {} to {} ", channelToUpdate, temperature);
            updateState(new ChannelUID(channelToUpdate), new DecimalType(temperature));

        }
        if (xbeeMessage.getData().length == 1) {
            logger.info(" This is the relay State : "
                    + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())));
            Tosr0xTMsg t = new Tosr0xTMsg(xbeeMessage.getData());
        }

    }

}
