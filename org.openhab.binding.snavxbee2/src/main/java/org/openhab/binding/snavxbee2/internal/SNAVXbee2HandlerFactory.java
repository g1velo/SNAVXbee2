/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.snavxbee2.internal;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.snavxbee2.discovery.SNAVXbee2DiscoveryService;
import org.openhab.binding.snavxbee2.handler.SNAVXbee2BridgeHandler;
import org.openhab.binding.snavxbee2.handler.SNAVXbee2Handler;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SNAVXbee2HandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Stephan NAVARRO - Initial contribution
 */
public class SNAVXbee2HandlerFactory extends BaseThingHandlerFactory {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {

        // logger.debug("!! thingTypeUID : {} SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID) : {} ", thingTypeUID,
        // SUPPORTED_DEVICE_TYPES_UIDS.contains(thingTypeUID));
        return SUPPORTED_DEVICE_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        logger.debug("Creating SNA1V HANDLER ------------------------------------------------------------");

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID)) {
            // logger.debug("????????1 in {} for thing type uid : {}", THING_TYPE_SAMPLE, thing.getThingTypeUID());
            // if (thingTypeUID.equals(THING_TYPE_TOSR0XT)) {
            return new SNAVXbee2Handler(thing);
            // }
            // if (thingTypeUID.equals(THING_TYPE_SAMPLE)) {
            // return new SNAVXbee2SampleHandler(thing);
            // }
        }

        if (SUPPORTED_BRIDGE_THING_TYPES_UIDS.contains(thingTypeUID)) {
            SNAVXbee2BridgeHandler handler = new SNAVXbee2BridgeHandler((Bridge) thing);
            registerXbeeDiscoveryService(handler);
            return handler;
        }

        return null;
    }

    private synchronized void registerXbeeDiscoveryService(SNAVXbee2BridgeHandler bridgeHandler) {

        SNAVXbee2DiscoveryService discoveryService = new SNAVXbee2DiscoveryService(bridgeHandler);
        discoveryService.activate();
        this.discoveryServiceRegs.put(bridgeHandler.getThing().getUID(), bundleContext
                .registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));
    }

    @Override
    protected synchronized void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof SNAVXbee2BridgeHandler) {
            ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.get(thingHandler.getThing().getUID());
            if (serviceReg != null) {
                // remove discovery service, if bridge handler is removed
                SNAVXbee2DiscoveryService service = (SNAVXbee2DiscoveryService) bundleContext
                        .getService(serviceReg.getReference());
                service.deactivate();
                serviceReg.unregister();
                discoveryServiceRegs.remove(thingHandler.getThing().getUID());
            }
        }
    }

}
