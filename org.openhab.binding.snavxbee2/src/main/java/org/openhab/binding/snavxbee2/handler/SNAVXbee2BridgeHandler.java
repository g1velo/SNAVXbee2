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
import com.digi.xbee.api.discoverdevices.MyDiscoveryListener;
import com.digi.xbee.api.exceptions.XBeeException;

public class SNAVXbee2BridgeHandler extends BaseBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2BridgeHandler.class);
    private SerialPortConfigParameters portconfig = new SerialPortConfigParameters();
    // TODO Replace with the serial port where your receiver module is connected.
    private static String PORT = "COM2";
    // TODO Replace with the baud rate of you receiver module.
    private static int BAUD_RATE = 9600;

    public SNAVXbee2BridgeHandler(Bridge bridge) {
        super(bridge);
        // TODO Auto-generated constructor stub
        logger.debug(" In the brige constructor , can handle commands");
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub
        logger.debug(" In the brige, can handle commands" + channelUID.getThingUID());
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
        Configuration config = this.getConfig();

        String port = (String) config.get("serialPort");
        String baud = config.get("baudRate").toString();

        portconfig.baudRate = baud;
        portconfig.serialPort = port;

        PORT = portconfig.serialPort;
        BAUD_RATE = Integer.valueOf(portconfig.baudRate);

        logger.debug(" In the brige, Initialising config : " + portconfig.serialPort + " " + portconfig.baudRate);

        try {
            XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);

            myDevice.open();
            // myDevice.liste

            if (myDevice.isOpen()) {
                logger.debug("Port {} open, ", PORT);
                updateStatus(ThingStatus.ONLINE);
            }

            myDevice.addDataListener(new MyDataReceiveListener());
            XBeeNetwork xbeeNetwork = myDevice.getNetwork();

            xbeeNetwork.setDiscoveryTimeout(15000);

            MyDiscoveryListener discovery = new MyDiscoveryListener();
            xbeeNetwork.addDiscoveryListener(discovery);

            RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice("LULUTEMP");

            String xbeeCmd = "o";
            myDevice.sendData(remoteDevice, xbeeCmd.getBytes());

            Thread.sleep(500);

            xbeeCmd = "e";
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

    }

}
