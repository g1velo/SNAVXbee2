package org.openhab.binding.snavxbee2.devices;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.*;

import java.util.List;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.snavxbee2.utils.IOLineIOModeMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;

public class XBeeConfig implements Runnable {

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

            if (thingTypeUID.equals(THING_TYPE_CAFE0EDF)) {
                logger.debug("configuring thing type : {}", THING_TYPE_CAFE0EDF);

            }
            if (thingTypeUID.equals(THING_TYPE_TOSR0XT)) {
                logger.debug("Setting timeout for to 30000 for {} tuid : {} compared to : {} ",
                        remoteDevice.get64BitAddress(), thingTypeUID, THING_TYPE_TOSR0XT);
                remoteDevice.setIOSamplingRate(0);

                for (IOLineIOModeMapping ioLineIOModeMapping : IOLinesxbeeConfig) {

                    logger.debug(" setting device : {}\t IOLine : {}\t IOMode : {}", remoteDevice.get64BitAddress(),
                            ioLineIOModeMapping.getIoLine(), ioLineIOModeMapping.getIoMode());
                    remoteDevice.setIOConfiguration(ioLineIOModeMapping.getIoLine(), ioLineIOModeMapping.getIoMode());

                }
                remoteDevice.applyChanges();
                remoteDevice.writeChanges();
                logger.warn(" Config Changes : {} ", remoteDevice.isApplyConfigurationChangesEnabled());
            }

            if (thingTypeUID.equals(THING_TYPE_SAMPLE)) {
                logger.debug(" Thung UID : {}  no config change , has to be done manually", thingTypeUID);
            }

        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
