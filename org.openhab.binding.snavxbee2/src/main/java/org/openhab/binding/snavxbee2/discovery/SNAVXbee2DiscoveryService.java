package org.openhab.binding.snavxbee2.discovery;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.SUPPORTED_THING_TYPES_UIDS;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.snavxbee2.handler.SNAVXbee2BridgeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteXBeeDevice;

public class SNAVXbee2DiscoveryService extends AbstractDiscoveryService {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2DiscoveryService.class);
    private SNAVXbee2BridgeHandler bridgeHandler;
    // private XBeeNetwork xbeeNetwork;

    public SNAVXbee2DiscoveryService(SNAVXbee2BridgeHandler bridgeHandler) {
        super(SUPPORTED_THING_TYPES_UIDS, 20000, false);
        logger.debug(" In SNAVXbee2DiscoveryService contructor ");
        this.bridgeHandler = bridgeHandler;
    }

    public void activate() {
        logger.debug("In ACTIVATE !!!!! ");
    }

    @Override
    protected void startScan() {
        // TODO Auto-generated method stub
        logger.debug("In StartScan 1 !!!!! ");

        List<RemoteXBeeDevice> xbeeDeviceList = bridgeHandler.startSearch(15000);

        // logger.debug("number of devices in list : {}", xbeeDeviceList.size());

        // ListIterator<RemoteXBeeDevice> xbeeDeviceListIterator = xbeeDeviceList.listIterator();

        // while (xbeeDeviceListIterator.hasNext()) {
        // RemoteXBeeDevice remote = xbeeDeviceListIterator.next();
        // logger.debug("devices : {}", remote.get64BitAddress());
        // }

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
