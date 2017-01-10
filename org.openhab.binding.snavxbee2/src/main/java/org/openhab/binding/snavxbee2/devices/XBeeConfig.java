package org.openhab.binding.snavxbee2.devices;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.*;

import java.util.List;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.snavxbee2.utils.IOLineIOModeMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;

public class XBeeConfig {

    private ThingTypeUID thingTypeUID;
    private Logger logger = LoggerFactory.getLogger(XBeeConfig.class);
    private RemoteXBeeDevice remoteDevice;

    public XBeeConfig(ThingTypeUID thingTypeUID, RemoteXBeeDevice remoteDevice) {
        super();
        this.thingTypeUID = thingTypeUID;
        this.remoteDevice = remoteDevice;

        List<IOLineIOModeMapping> IOLinesxbeeConfig = new IOLineXBeeDeviceConfig(this.thingTypeUID).getIoLinesConfig();

        logger.debug("Trying it {} ", IOLinesxbeeConfig.size());

        try {

            if (thingTypeUID.equals(THING_TYPE_TOSR0XT)) {
                logger.debug("Setting timeout for to 3001 for {} tuid : {} ", remoteDevice.get64BitAddress(),
                        thingTypeUID);
                remoteDevice.setIOSamplingRate(30001);
            }

            if (thingTypeUID.equals(THING_TYPE_SAMPLE)) {
                logger.debug("Setting timeout for to 3000 for {} tuid : {} ", remoteDevice.get64BitAddress(),
                        thingTypeUID);
                remoteDevice.setIOSamplingRate(30000);
            }

            for (IOLineIOModeMapping ioLineIOModeMapping : IOLinesxbeeConfig) {

                logger.debug(" setting device : {}\t IOLine : {}\t IOMode : {}", remoteDevice.get64BitAddress(),
                        ioLineIOModeMapping.getIoLine(), ioLineIOModeMapping.getIoMode());
                remoteDevice.setIOConfiguration(ioLineIOModeMapping.getIoLine(), ioLineIOModeMapping.getIoMode());
            }
            remoteDevice.applyChanges();

        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
