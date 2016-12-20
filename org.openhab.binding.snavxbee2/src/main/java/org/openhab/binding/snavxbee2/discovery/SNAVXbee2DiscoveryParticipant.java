package org.openhab.binding.snavxbee2.discovery;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.SUPPORTED_DEVICE_TYPES_UIDS;

import java.util.Set;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.UpnpDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.jupnp.model.meta.RemoteDevice;

public class SNAVXbee2DiscoveryParticipant implements UpnpDiscoveryParticipant {

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        // TODO Auto-generated method stub
        return SUPPORTED_DEVICE_TYPES_UIDS;
    }

    @Override
    public DiscoveryResult createResult(RemoteDevice device) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ThingUID getThingUID(RemoteDevice device) {
        // TODO Auto-generated method stub
        return null;
    }

}
