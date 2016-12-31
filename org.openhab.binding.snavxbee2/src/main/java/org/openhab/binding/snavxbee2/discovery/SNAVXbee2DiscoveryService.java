package org.openhab.binding.snavxbee2.discovery;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.snavxbee2.devices.IOLineXBeeDeviceConfig;
import org.openhab.binding.snavxbee2.handler.SNAVXbee2BridgeHandler;
import org.openhab.binding.snavxbee2.utils.IOLineIOModeMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.HexUtils;

public class SNAVXbee2DiscoveryService extends AbstractDiscoveryService {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2DiscoveryService.class);
    private SNAVXbee2BridgeHandler bridgeHandler;
    private boolean TRYIT = true;
    // private XBeeNetwork xbeeNetwork;

    public SNAVXbee2DiscoveryService(SNAVXbee2BridgeHandler bridgeHandler) {
        super(SUPPORTED_DEVICE_TYPES_UIDS, 20000, false);
        logger.debug(" In SNAVXbee2DiscoveryService contructor " + SUPPORTED_THING_TYPES_UIDS);
        this.bridgeHandler = bridgeHandler;
    }

    public void activate() {
        logger.debug("In ACTIVATE !!!!! ");
    }

    @Override
    protected void startScan() {
        // TODO Auto-generated method stub
        logger.debug("In StartScan ! !!!!! ");

        List<RemoteXBeeDevice> xbeeDeviceList = bridgeHandler.startSearch(15000);

        if (xbeeDeviceList != null) {

            logger.debug("number of devices in list : {}", xbeeDeviceList.size());

            ListIterator<RemoteXBeeDevice> xbeeDeviceListIterator = xbeeDeviceList.listIterator();

            ThingUID bridgeUID = bridgeHandler.getThing().getUID();
            // ThingUID discoveredThingUID = BINDING_ID + ":" + THING_TYPE_TOSR0XT + ":" +
            logger.debug("bridge : {} ", bridgeUID);

            while (xbeeDeviceListIterator.hasNext()) {

                RemoteXBeeDevice remote = xbeeDeviceListIterator.next();
                String deviceTypeIdentifier = "X";

                try {

                    deviceTypeIdentifier = HexUtils.byteArrayToHexString(remote.getParameter("DD"));
                    logger.debug("devices : {} type : {} : {} : {} ", remote.get64BitAddress(),
                            remote.getXBeeProtocol(), HexUtils.byteArrayToHexString(remote.getParameter("DD")),
                            HexUtils.byteArrayToHexString(remote.getParameter("SL")));
                } catch (TimeoutException e) {
                    // TODO Auto-generated catch block
                    logger.debug("Unable to get DD for device : {} ", remote.get64BitAddress());
                    try {
                        remote.reset();
                    } catch (XBeeException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    e.printStackTrace();

                } catch (XBeeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                ThingUID uid = null;
                ThingTypeUID tuid = null;
                switch (deviceTypeIdentifier) {
                    case "CAFE0002":
                        tuid = THING_TYPE_TOSR0XT;
                        uid = new ThingUID(THING_TYPE_TOSR0XT, bridgeHandler.getThing().getUID(),
                                remote.get64BitAddress().toString());
                        break;

                    default:
                        tuid = THING_TYPE_SAMPLE;
                        uid = new ThingUID(THING_TYPE_SAMPLE, bridgeHandler.getThing().getUID(),
                                remote.get64BitAddress().toString());
                        // bridgeHandler.sendAPICommandToDevice(remote.get64BitAddress(), IOLine.DIO2_AD2,
                        // IOValue.HIGH);

                        break;
                }

                if (uid != null) {
                    Map<String, Object> properties = new HashMap<>(2);
                    properties.put(PARAMETER_XBEE64BITSADDRESS, remote.get64BitAddress().toString());
                    properties.put(PARAMETER_IOCHANNEL, "not used yet");

                    if (TRYIT) {

                        List<IOLineIOModeMapping> xbeeConfig = new IOLineXBeeDeviceConfig(tuid).getIoLinesConfig();

                        logger.debug("Trying it {} ", xbeeConfig.size());

                        for (IOLineIOModeMapping ioLineIOModeMapping : xbeeConfig) {
                            try {
                                logger.debug(" setting device : {}\t IOLine : {}\t IOMode : {}",
                                        remote.get64BitAddress(), ioLineIOModeMapping.getIoLine(),
                                        ioLineIOModeMapping.getIoMode());
                                remote.setIOConfiguration(ioLineIOModeMapping.getIoLine(),
                                        ioLineIOModeMapping.getIoMode());
                            } catch (XBeeException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                            .withLabel("XbeeDevice Device (" + remote.getNodeID() + " "
                                    + remote.get64BitAddress().toString() + " " + deviceTypeIdentifier + ")")
                            .withBridge(bridgeHandler.getThing().getUID()).build();
                    thingDiscovered(result);

                    logger.debug("Discovered device submitted");

                }

            }

        } else {
            logger.debug("Device List is empty");
        }
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        // TODO Auto-generated method stub
        return super.getSupportedThingTypes();
    }

    @Override
    public void deactivate() {
        removeOlderResults(new Date().getTime());
        // SNAVXbee2BridgeHandler.unregisterXbeeStatusListener(this);
    }
}
