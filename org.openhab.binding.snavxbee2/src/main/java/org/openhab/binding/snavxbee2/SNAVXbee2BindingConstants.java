/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.snavxbee2;

import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableSet;

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
    public final static ThingTypeUID THING_TYPE_XBEEAPI = new ThingTypeUID(BINDING_ID, "xbeeapi");
    public final static ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");
    public final static ThingTypeUID THING_TYPE_TOSR0XT = new ThingTypeUID(BINDING_ID, "tosr0xt");

    // List of all Channel ids
    public final static String CHANNEL_1 = "channel1";
    public final static String TEMPERATURE = "temperature";
    public final static String RELAY1 = "relay1";
    public final static String RELAY2 = "relay2";
    public final static String RELAY3 = "relay3";
    public final static String RELAY4 = "relay4";
    public final static String RELAY5 = "relay5";
    public final static String RELAY6 = "relay6";
    public final static String RELAY7 = "relay7";
    public final static String RELAY8 = "relay8";
    public final static String HUMIDITY = "humidity";
    public final static String LIGHT = "light";
    public final static String ANALOG_TEMPERATURE = "analog_temperature";

    public static final String PARAMETER_XBEE64BITSADDRESS = "Xbee64BitsAddress";
    public static final String PARAMETER_IOCHANNEL = "IOChannel";

    // supported channels for tosr0xt devices;
    public static final Set<String> SUPPORTED_TOSR0XT_CHANNELS = ImmutableSet.of(TEMPERATURE, RELAY1, RELAY2, RELAY3,
            RELAY4, RELAY5, RELAY6, RELAY7, RELAY8);
    public static final Set<String> SUPPORTED_TOSR0XT_RELAY_CHANNELS = ImmutableSet.of(RELAY1, RELAY2, RELAY3, RELAY4,
            RELAY5, RELAY6, RELAY7, RELAY8);

    // supported channels for CAFEdevices;

    /** Supported Things without bridge */
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_SAMPLE,
            THING_TYPE_TOSR0XT, THING_TYPE_XBEEAPI);

    /** Supported bridges */
    public final static Set<ThingTypeUID> SUPPORTED_BRIDGE_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_BRIDGE);

    /** Supported devices (things + bridges) */
    public final static Set<ThingTypeUID> SUPPORTED_DEVICE_TYPES_UIDS = ImmutableSet.of(THING_TYPE_SAMPLE,
            THING_TYPE_BRIDGE, THING_TYPE_TOSR0XT, THING_TYPE_XBEEAPI);

}
