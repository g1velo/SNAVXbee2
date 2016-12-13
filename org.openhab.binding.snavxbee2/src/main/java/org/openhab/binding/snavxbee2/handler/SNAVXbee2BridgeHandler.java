package org.openhab.binding.snavxbee2.handler;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.snavxbee.util.MyDataReceiveListener;
import org.openhab.binding.snavxbee2.utils.SerialPortConfigParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;

public class SNAVXbee2BridgeHandler extends BaseBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2BridgeHandler.class);
    private SerialPortConfigParameters portconfig = new SerialPortConfigParameters();
    // TODO Replace with the serial port where your receiver module is connected.
    private static String PORT = "COM2";
    // TODO Replace with the baud rate of you receiver module.
    private static int BAUD_RATE = 9600;
    private Configuration config = this.getConfig();

    public XBeeDevice myDevice = null;
    public XBeeNetwork xbeeNetwork = null;

    public SNAVXbee2BridgeHandler(Bridge bridge) {
        super(bridge);
        // TODO Auto-generated constructor stub
        logger.debug(" In the brige constructor , can handle commands");
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub
        logger.debug(" In the brige, can handle commands" + channelUID.getThingUID());
        logger.debug("é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~é~~~");
    }

    public boolean sendCommandToDevice(String xbee64BitsAddress, String xbeeCommmand) {

        logger.debug(" sending command : {} to : {} ", xbeeCommmand, xbee64BitsAddress);

        try {
            logger.debug("0013A20040E31560 start getting RemoteXbeeDevice :  {} with command {}  ", xbee64BitsAddress,
                    xbeeCommmand);
            RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice(xbee64BitsAddress);
            // RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice("LULUTEMP");

            logger.debug("start sending command : {} ", xbeeCommmand);
            myDevice.sendData(remoteDevice, xbeeCommmand.getBytes());
        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        logger.debug(" In the brige, Disposing config : " + portconfig.serialPort + " " + portconfig.baudRate);

        super.dispose();
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

            myDevice.addDataListener(new MyDataReceiveListener());
            // XBeeNetwork xbeeNetwork = myDevice.getNetwork();
            this.xbeeNetwork = myDevice.getNetwork();

            // No discovery so far
            /*
             * xbeeNetwork.setDiscoveryTimeout(15000);
             *
             * MyDiscoveryListener discovery = new MyDiscoveryListener();
             * xbeeNetwork.addDiscoveryListener(discovery);
             */

            long startTime = System.nanoTime();
            RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice("LULUTEMP");
            long estimatedTime = System.nanoTime() - startTime;

            logger.debug("getting remote devices took : {} ms", estimatedTime / 1000 / 1000);

            String xbeeCmd = "d";
            myDevice.sendData(remoteDevice, xbeeCmd.getBytes());

            Thread.sleep(500);

            xbeeCmd = "n";
            myDevice.sendData(remoteDevice, xbeeCmd.getBytes());

            Thread.sleep(500);

            xbeeCmd = "a";
            myDevice.sendData(remoteDevice, xbeeCmd.getBytes());

            if (remoteDevice == null) {
                logger.debug("Couldn't find the remote XBee device with '" + "LULUTEMP" + "' Node Identifier.");
                // System.exit(1);
            }

        } catch (XBeeException e) {
            e.printStackTrace();
            // System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            // System.exit(1);
        }

        if (myDevice.isOpen()) {
            logger.debug("Port {} open, ", PORT);
            updateStatus(ThingStatus.ONLINE);
        }
    }

}
