package org.openhab.binding.snavxbee2.handler;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.snavxbee2.utils.SerialPortConfigParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SNAVXbee2BridgeHandler extends BaseBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2BridgeHandler.class);
    private SerialPortConfigParameters portconfig = new SerialPortConfigParameters();

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

        logger.debug(
                " In the brige, can handle commands config : " + portconfig.serialPort + " " + portconfig.baudRate);

        updateStatus(ThingStatus.ONLINE);
    }

}
