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
    public final static ThingTypeUID THING_TYPE_CAFE1000 = new ThingTypeUID(BINDING_ID, "cafe1000");
    public final static ThingTypeUID THING_TYPE_CAFE1001 = new ThingTypeUID(BINDING_ID, "cafe1001");
    public final static ThingTypeUID THING_TYPE_CAFE0EDF = new ThingTypeUID(BINDING_ID, "cafe0edf");
    public final static ThingTypeUID THING_TYPE_CAFEAD00 = new ThingTypeUID(BINDING_ID, "cafead00");

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
    public final static String AD0DIO0 = "AD0DIO0";
    public final static String AD1DIO1 = "AD1DIO1";
    public final static String AD2DIO2 = "AD2DIO2";
    public final static String AD3DIO3 = "AD3DIO3";
    public final static String AD4DIO4 = "AD4DIO4";
    public final static String AD5DIO5 = "AD5DIO5";
    public final static String DIO6 = "DIO6";
    public final static String DIO7 = "DIO7";
    public final static String DIO8 = "DIO8";
    public final static String DIO9 = "DIO9";
    public final static String PWM0DIO10 = "PWM0DIO10";
    public final static String PWM1DIO11 = "PWM1DIO11";
    public final static String DIO12 = "DIO12";
    public final static String DIO13 = "DIO13";
    public final static String DIO14 = "DIO14";
    public final static String DIO15 = "DIO15";
    public final static String DIO16 = "DIO16";
    public final static String DIO17 = "DIO17";
    public final static String DIO18 = "DIO18";
    public final static String DIO19 = "DIO19";
    public final static String S1 = "S1";
    public final static String S2 = "S2";
    public final static String S3 = "S3";

    public final static String ADCO = "ADCO";
    public final static String OPTARIF = "OPTARIF";
    public final static String ISOUSC = "ISOUSC";
    public final static String BASE = "BASE";
    public final static String HCHC = "HCHC";
    public final static String HCHP = "HCHP";
    public final static String EJPHN = "EJPHN";
    public final static String EJPHPM = "EJPHPM";
    public final static String BBRHCJB = "BBRHCJB ";
    public final static String BBRHPJB = "BBRHPJB";
    public final static String BBRHCJW = "BBRHCJW";
    public final static String BBRHPJW = "BBRHPJW";
    public final static String BBRHCJR = "BBRHCJR";
    public final static String BBRHPJR = "BBRHPJR";
    public final static String PEJP = "PEJP";
    public final static String PTEC = "PTEC";
    public final static String DEMAIN = "DEMAIN";
    public final static String IINST = "IINST";
    public final static String ADPS = "ADPS";
    public final static String IMAX = "IMAX";
    public final static String HHPHC = "HHPHC";
    public final static String PMAX = "PMAX";
    public final static String MOTDETAT = "MOTDETAT";
    public final static String PAPP = "PAPP";
    public final static String PPOT = "PPOT";

    public final static String HUMIDITY = "humidity";
    public final static String LIGHT = "light";
    public final static String ANALOG_TEMPERATURE = "analog_temperature";

    public static final String PARAMETER_XBEE64BITSADDRESS = "Xbee64BitsAddress";
    public static final String PARAMETER_RESETXBEE = "ResetXbee";
    public static final String PARAMETER_XBEEDEVICETYPEID = "XBeeDeviceTypeID";

    // supported channels for tosr0xt devices;
    // Not really sure that it is usefull

    public static final Set<String> SUPPORTED_TOSR0XT_CHANNELS = ImmutableSet.of(TEMPERATURE, RELAY1, RELAY2, RELAY3,
            RELAY4, RELAY5, RELAY6, RELAY7, RELAY8, AD0DIO0, AD1DIO1, AD2DIO2, AD3DIO3, AD4DIO4, AD5DIO5, PWM0DIO10,
            PWM1DIO11, DIO12);
    public static final Set<String> SUPPORTED_TOSR0XT_RELAY_CHANNELS = ImmutableSet.of(RELAY1, RELAY2, RELAY3, RELAY4,
            RELAY5, RELAY6, RELAY7, RELAY8);

    public static final Set<String> SUPPORTED_SAMPLE_CHANNELS = ImmutableSet.of(AD0DIO0, AD1DIO1, AD2DIO2, AD3DIO3,
            AD4DIO4, AD5DIO5, DIO6, DIO7, DIO8, DIO9, PWM0DIO10, PWM1DIO11, DIO12, DIO13, DIO14, DIO15, DIO16, DIO17,
            DIO18, DIO19);

    // supported channels for CAFEdevices;
    // CAFE1000 / cafe1000
    public static final Set<String> SUPPORTED_CAFE1000_CHANNELS = ImmutableSet.of(AD0DIO0, AD1DIO1, AD2DIO2, AD3DIO3,
            AD4DIO4, AD5DIO5, DIO6, DIO7, DIO8, DIO9, PWM0DIO10, PWM1DIO11, DIO12, DIO13, DIO14, DIO15, DIO16, DIO17,
            DIO18, DIO19, S1, S2, S3);

    // supported channels for CAFE0EDF devices ( Teleinformation EDF France);
    public static final Set<String> SUPPORTED_CAFE0EDF_CHANNELS = ImmutableSet.of(ADCO, OPTARIF, ISOUSC, BASE, HCHC,
            HCHP, EJPHN, EJPHPM, BBRHCJB, BBRHPJB, BBRHCJW, BBRHPJW, BBRHCJR, BBRHPJR, PEJP, PTEC, DEMAIN, IINST, ADPS,
            IMAX, HHPHC, PMAX, MOTDETAT, PAPP, PPOT);

    /** ALL Supported Things without bridge */
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_SAMPLE,
            THING_TYPE_TOSR0XT, THING_TYPE_XBEEAPI, THING_TYPE_CAFE1000, THING_TYPE_CAFE1001);

    /** Per handler supported type */
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_CAFE1001 = ImmutableSet.of(THING_TYPE_CAFE1001);
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_CAFE0EDF = ImmutableSet.of(THING_TYPE_CAFE0EDF,
            THING_TYPE_CAFEAD00);
    public final static Set<ThingTypeUID> FIRST_SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_SAMPLE,
            THING_TYPE_TOSR0XT, THING_TYPE_XBEEAPI, THING_TYPE_CAFE1000);

    /** Supported bridges */
    public final static Set<ThingTypeUID> SUPPORTED_BRIDGE_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_BRIDGE);

    /** Supported devices (things + bridges) */
    public final static Set<ThingTypeUID> SUPPORTED_DEVICE_TYPES_UIDS = ImmutableSet.of(THING_TYPE_SAMPLE,
            THING_TYPE_BRIDGE, THING_TYPE_TOSR0XT, THING_TYPE_XBEEAPI, THING_TYPE_CAFE1000, THING_TYPE_CAFE1001,
            THING_TYPE_CAFE0EDF, THING_TYPE_CAFEAD00);

}
