/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.snavxbee2.internal;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.THING_TYPE_BRIDGE;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.snavxbee2.handler.SNAVXbee2BridgeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SNAVXbee2HandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Stephan NAVARRO - Initial contribution
 */
public class SNAVXbee2HandlerFactory extends BaseThingHandlerFactory {

    // private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_SAMPLE);
    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_BRIDGE);
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        logger.debug("Creating SNAV HANDLER ------------------------------------------------------------");

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        Configuration configuration = thing.getConfiguration();

        // configuration.getProperties();

        // if (thingTypeUID.equals(THING_TYPE_SAMPLE)) {
        if (thingTypeUID.equals(THING_TYPE_BRIDGE)) {
            // return new SNAVXbee2Handler(thing);
            return new SNAVXbee2BridgeHandler((Bridge) thing);
        }

        return null;
    }
}
