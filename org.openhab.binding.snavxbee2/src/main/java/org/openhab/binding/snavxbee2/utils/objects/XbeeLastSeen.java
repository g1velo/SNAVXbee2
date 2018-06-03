package org.openhab.binding.snavxbee2.utils.objects;

import org.eclipse.smarthome.core.thing.ThingUID;

public class XbeeLastSeen {
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
