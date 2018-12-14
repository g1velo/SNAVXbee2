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
    private SNAVXbee2BridgeHandler bridgeHandler;

    public SNAVXbee2DiscoveryService(SNAVXbee2BridgeHandler bridgeHandler) {
        super(SUPPORTED_DEVICE_TYPES_UIDS, 20000, false);
        logger.debug(" In SNAVXbee2DiscoveryService contructor " + SUPPORTED_THING_TYPES_UIDS);
        this.bridgeHandler = bridgeHandler;
    }

    public void activate() {
        logger.debug("Discovery activate() Method");
    }

    @Override
    protected void startScan() {

        // First removing old results !
        removeOlderResults(new Date().getTime());

        logger.info("Starting Xbee Device Discovery");

        List<RemoteXBeeDevice> xbeeDeviceList = bridgeHandler.startSearch(15000);

        if (xbeeDeviceList != null) {

            logger.debug("number of devices in list : {}", xbeeDeviceList.size());

            ListIterator<RemoteXBeeDevice> xbeeDeviceListIterator = xbeeDeviceList.listIterator();

            ThingUID bridgeUID = bridgeHandler.getThing().getUID();

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
                String thingLabel;

                switch (deviceTypeIdentifier) {
                    case "CAFE0002":
                        tuid = THING_TYPE_TOSR0XT;
                        thingLabel = "XBee with TosR0xT";
                        break;
                    case "CAFE1000":
                        tuid = THING_TYPE_CAFE1000;
                        thingLabel = "XBee with CAFE1000 controller attached";
                        break;
                    case "CAFE1001":
                        tuid = THING_TYPE_CAFE1001;
                        thingLabel = "XBee with CAFE1001 controller attached";
                        break;
                    case "CAFE0EDF":
                        tuid = THING_TYPE_CAFE0EDF;
                        thingLabel = "XBee controller attached To teleinfo EDF";
                        break;
                    case "CAFEAD00":
                        logger.warn("Discovered CAFEAD00");
                        tuid = THING_TYPE_CAFEAD00;
                        thingLabel = "Test XBee controller attached To teleinfo EDF";
                        break;
                    default:
                        tuid = THING_TYPE_SAMPLE;
                        thingLabel = "default XBee";
                        break;
                }

                uid = new ThingUID(tuid, bridgeHandler.getThing().getUID(), remote.get64BitAddress().toString());
                logger.debug("THingUID Discovered : {} ", uid);

                if (uid != null) {

                    Map<String, Object> properties = new HashMap<>(2);
                    properties.put(PARAMETER_XBEE64BITSADDRESS, remote.get64BitAddress().toString());
                    properties.put(PARAMETER_RESETXBEE, false);

                    // try {
                    // properties.put(PARAMETER_XBEEDEVICETYPEID, remote.getParameter("DD").toString());
                    // } catch (TimeoutException e) {
                    // // TODO Auto-generated catch block
                    // e.printStackTrace();
                    // } catch (XBeeException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                    // }

                    // XBeeConfig xbeeCfg = new XBeeConfig(tuid, remote);

                    DiscoveryResult result = DiscoveryResultBuilder
                            .create(uid).withProperties(properties).withLabel(thingLabel + " " + remote.getNodeID()
                                    + " " + remote.get64BitAddress().toString() + " " + deviceTypeIdentifier)
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
