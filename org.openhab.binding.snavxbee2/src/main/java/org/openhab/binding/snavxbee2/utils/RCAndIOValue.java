package org.openhab.binding.snavxbee2.utils;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;

import com.digi.xbee.api.io.IOValue;

public class RCAndIOValue {

    public boolean rc;
    public IOValue iovalue;
    public State state;

    public boolean isRc() {
        return rc;
    }

    public void setRc(boolean rc) {
        this.rc = rc;
    }

    public IOValue getIovalue() {
        return iovalue;
    }

    public void setIovalue(IOValue iovalue) {
        this.iovalue = iovalue;
    }

    public State getState() {

        if (this.iovalue.equals(IOValue.HIGH)) {
            this.state = OnOffType.ON;
        }
        if (this.iovalue.equals(IOValue.LOW)) {
            this.state = OnOffType.OFF;
        }

        return state;
    }

}
