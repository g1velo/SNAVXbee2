package org.openhab.binding.snavxbee2.objects;

import org.eclipse.smarthome.core.thing.ThingUID;

public class ThingLastseen {

    private ThingUID uid;
    private Integer lastseen;
    // default alert time
    private Integer alert = 300;

    public ThingLastseen(ThingUID uid, Integer lastseen, Integer alert) {
        super();
        this.uid = uid;
        this.lastseen = lastseen;
        this.alert = alert;
    }

    public ThingUID getUid() {
        return uid;
    }

    public void setUid(ThingUID uid) {
        this.uid = uid;
    }

    public Integer getLastseen() {
        return lastseen;
    }

    public void setLastseen(Integer lastseen) {
        this.lastseen = lastseen;
    }

    public Integer getAlert() {
        return alert;
    }

    public void setAlert(Integer alert) {
        this.alert = alert;
    }

}
