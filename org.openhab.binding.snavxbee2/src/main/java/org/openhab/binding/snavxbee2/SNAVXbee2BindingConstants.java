/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.snavxbee2;

import java.util.Collection;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * The {@link SNAVXbee2Binding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Stephan NAVARRO - Initial contribution
 */
public class SNAVXbee2BindingConstants {

    public static final String BINDING_ID = "snavxbee2";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "sample");
    public final static ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");

    // List of all Channel ids
    public final static String CHANNEL_1 = "channel1";
    public final static String TEMPERATURE = "temperature";

    public static final String PARAMETER_XBEE64BITSADDRESS = "Xbee64BitsAddress";

    /** Supported Things without bridge */
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_SAMPLE);

    /** Supported bridges */
    public final static Set<ThingTypeUID> SUPPORTED_BRIDGE_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_BRIDGE);

    /** Supported devices (things + bridges) */
    public final static Collection<ThingTypeUID> SUPPORTED_DEVICE_TYPES_UIDS = Lists.newArrayList(THING_TYPE_SAMPLE,
            THING_TYPE_BRIDGE);

}
