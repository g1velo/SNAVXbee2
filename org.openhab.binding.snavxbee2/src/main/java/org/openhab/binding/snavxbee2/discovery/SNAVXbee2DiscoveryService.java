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
import org.openhab.binding.snavxbee2.devices.XBeeConfig;
import org.openhab.binding.snavxbee2.handler.SNAVXbee2BridgeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.HexUtils;

/**
 * The {@link SNAVXbee2DiscoveryService } class launch Xbee discovery service and put
 * list of device in the INBOX
 * Some devices can be recognized using Xbee Device identifier ( DD )
 *
 * @author Stephan NAVARRO - Initial contribution
 */

public class SNAVXbee2DiscoveryService extends AbstractDiscoveryService {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2DiscoveryService.class);
    // private DiscoveryServiceCallback discoveryServiceCallback;
    private SNAVXbee2BridgeHandler bridgeHandler;
    // private boolean TRYIT = true;
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

        // First removing old results !
        // removeOlderResults(0);
        logger.info("Starting Xbee Device Discovery");

        List<RemoteXBeeDevice> xbeeDeviceList = bridgeHandler.startSearch(15000);

        if (xbeeDeviceList != null) {

            logger.debug("number of devices in list : {}", xbeeDeviceList.size());

            ListIterator<RemoteXBeeDevice> xbeeDeviceListIterator = xbeeDeviceList.listIterator();

            ThingUID bridgeUID = bridgeHandler.getThing().getUID();
            // ThingUID discoveredThingUID = BINDING_ID + ":" + THING_TYPE_TOSR0XT + ":" +
            logger.debug("bridge : {} ", bridgeUID);

            while (xbeeDeviceListIterator.hasNext()) {

                RemoteXBeeDevice remote = xbeeDeviceListIterator.next();
                remote.String deviceTypeIdentifier = "X";

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
                String thingLabel = "learn from XBee";

                switch (deviceTypeIdentifier) {
                    case "CAFE0002":
                        tuid = THING_TYPE_TOSR0XT;
                        // uid = new ThingUID(THING_TYPE_TOSR0XT, bridgeHandler.getThing().getUID(),
                        // remote.get64BitAddress().toString());
                        thingLabel = "XBee with TosR0xT";
                        break;
                    case "CAFE1000":
                        tuid = THING_TYPE_CAFE1000;
                        // uid = new ThingUID(THING_TYPE_CAFE1000, bridgeHandler.getThing().getUID(),
                        // remote.get64BitAddress().toString());
                        thingLabel = "XBee with CAFE1000 controller attached";
                        break;
                    case "CAFE1001":
                        tuid = THING_TYPE_CAFE1001;
                        // uid = new ThingUID(THING_TYPE_CAFE1001, bridgeHandler.getThing().getUID(),
                        // remote.get64BitAddress().toString());
                        thingLabel = "XBee with CAFE1001 controller attached";
                        break;
                    default:
                        tuid = THING_TYPE_SAMPLE;
                        // uid = new ThingUID(THING_TYPE_SAMPLE, bridgeHandler.getThing().getUID(),
                        // remote.get64BitAddress().toString());
                        thingLabel = "default XBee";
                        break;
                }

                uid = new ThingUID(tuid, bridgeHandler.getThing().getUID(), remote.get64BitAddress().toString());
                logger.debug("::::::::::::::::::: THingUID Discovered : {} ", uid);

                if (uid != null) {
                    Map<String, Object> properties = new HashMap<>(2);
                    properties.put(PARAMETER_XBEE64BITSADDRESS, remote.get64BitAddress().toString());
                    properties.put(PARAMETER_IOCHANNEL, "not used yet");

                    XBeeConfig xbeeCfg = new XBeeConfig(tuid, remote);

                    DiscoveryResult result = DiscoveryResultBuilder.create(uid)
                            .withProperties(properties).withLabel(thingLabel + " " + remote.getNodeID() + " "
                                    + remote.get64BitAddress().toString() + " " + deviceTypeIdentifier)
                            .withBridge(bridgeHandler.getThing().getUID()).build();
                    thingDiscovered(result);

                    logger.debug("Discovered device submitted");

                }

            }

        } else {
            logger.info("Device List is empty");
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
