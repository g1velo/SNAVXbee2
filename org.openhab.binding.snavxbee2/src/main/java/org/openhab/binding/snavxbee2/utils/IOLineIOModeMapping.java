package org.openhab.binding.snavxbee2.utils;

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;

public class IOLineIOModeMapping {
    private IOLine ioLine;
    private IOMode ioMode;

    public IOLineIOModeMapping(IOLine ioLine, IOMode ioMode) {
        super();
        this.ioLine = ioLine;
        this.ioMode = ioMode;
    }

    public IOLine getIoLine() {
        return ioLine;
    }

    public void setIoLine(IOLine ioLine) {
        this.ioLine = ioLine;
    }

    public IOMode getIoMode() {
        return ioMode;
    }

    public void setIoMode(IOMode ioMode) {
        this.ioMode = ioMode;
    }
}
