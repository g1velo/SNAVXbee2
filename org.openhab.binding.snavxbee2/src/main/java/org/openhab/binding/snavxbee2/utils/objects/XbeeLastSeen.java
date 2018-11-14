package org.openhab.binding.snavxbee2.utils.objects;

import org.eclipse.smarthome.core.thing.ThingUID;

import com.digi.xbee.api.models.XBee64BitAddress;

public class XbeeLastSeen {

    public XBee64BitAddress address;
    public ThingUID thingUID;
    public long lastseen;

    public ThingUID getThingUID() {
        return thingUID;
    }

    public void setThingUID(ThingUID thingUID) {
        this.thingUID = thingUID;
    }

    public long getLastseen() {
        return lastseen;
    }

    public void setLastseen(long lastseen) {
        this.lastseen = lastseen;
    }
}
