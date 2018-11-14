package org.openhab.binding.snavxbee2.devices;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.snavxbee2.utils.IOLineIOModeMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;

public class IOLineXBeeDeviceConfig {

    private List<IOLine> ioLinesToUse = new ArrayList<IOLine>();
    private List<IOLineIOModeMapping> ioLinesConfig = new ArrayList<>();
    private ThingUID thingUID;
    private ThingTypeUID thingTypeUID;
    private Logger logger = LoggerFactory.getLogger(IOLineXBeeDeviceConfig.class);

    public IOLineXBeeDeviceConfig(ThingTypeUID thingTypeUID) {

        this.thingTypeUID = thingTypeUID;

        logger.debug("thingUID : {} ", thingTypeUID);

        if (thingTypeUID.equals(THING_TYPE_SAMPLE)) {
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO1_AD1, IOMode.ADC));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO2_AD2, IOMode.ADC));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO3_AD3, IOMode.ADC));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO4_AD4, IOMode.DIGITAL_IN));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO5_AD5, IOMode.DIGITAL_IN));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO11_PWM1, IOMode.DIGITAL_OUT_LOW));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO12, IOMode.DIGITAL_OUT_LOW));
        }

        if (thingTypeUID.equals(THING_TYPE_TOSR0XT)) {
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO1_AD1, IOMode.DISABLED));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO2_AD2, IOMode.DISABLED));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO3_AD3, IOMode.DISABLED));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO4_AD4, IOMode.DISABLED));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO5_AD5, IOMode.DISABLED));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO11_PWM1, IOMode.DISABLED));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO12, IOMode.DISABLED));
        }

        if (thingTypeUID.equals(THING_TYPE_XBEEAPI)) {
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO1_AD1, IOMode.ADC));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO2_AD2, IOMode.ADC));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO3_AD3, IOMode.ADC));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO4_AD4, IOMode.DISABLED));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO5_AD5, IOMode.DISABLED));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO11_PWM1, IOMode.DISABLED));
            ioLinesConfig.add(new IOLineIOModeMapping(IOLine.DIO12, IOMode.DISABLED));
        }

    }

    public List<IOLineIOModeMapping> getIoLinesConfig() {
        return ioLinesConfig;
    }

}
