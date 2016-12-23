package org.openhab.binding.snavxbee2.utils;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.types.State;

public class ChannelActionToPerform {

    private ChannelUID channelUIDToUpdate;
    private State state;

    public ChannelUID getChannelUIDToUpdate() {
        return channelUIDToUpdate;
    }

    public void setChannelUIDToUpdate(ChannelUID channelUIDToUpdate) {
        this.channelUIDToUpdate = channelUIDToUpdate;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
