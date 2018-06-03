package org.openhab.binding.snavxbee2.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tosr0xT {

    private static String idVersion = "Z";
    private static String all_relay_states = "[";
    private static String voltage = "]";
    private static String temperature = "b";

    // commands to set Relay On Or Off
    private static String allRelayOn = "d";
    private static String relay1On = "e";
    private static String relay2On = "f";
    private static String relay3On = "g";
    private static String relay4On = "h";
    private static String relay5On = "i";
    private static String relay6On = "j";
    private static String relay7On = "k";
    private static String relay8On = "l";

    private static String allRelayOff = "n";
    private static String relay1Off = "o";
    private static String relay2Off = "p";
    private static String relay3Off = "q";
    private static String relay4Off = "r";
    private static String relay5Off = "s";
    private static String relay6Off = "t";
    private static String relay7Off = "u";
    private static String relay8Off = "v";

    private Logger logger = LoggerFactory.getLogger(Tosr0xT.class);

    public static String getRelayCommandToSend(String relay, String status) {

        String actionToPeform = relay + status;
        String commandToSend = null;

        switch (actionToPeform) {
            case "relay1ON":
                commandToSend = relay1On;
                break;
            case "relay2ON":
                commandToSend = relay2On;
                break;
            case "relay3ON":
                commandToSend = relay3On;
                break;
            case "relay4ON":
                commandToSend = relay4On;
                break;
            case "relay5ON":
                commandToSend = relay5On;
                break;
            case "relay6ON":
                commandToSend = relay6On;
                break;
            case "relay7ON":
                commandToSend = relay7On;
                break;
            case "relay8ON":
                commandToSend = relay8On;
                break;
            case "relay1OFF":
                commandToSend = relay1Off;
                break;
            case "relay2OFF":
                commandToSend = relay2Off;
                break;
            case "relay3OFF":
                commandToSend = relay3Off;
                break;
            case "relay4OFF":
                commandToSend = relay4Off;
                break;
            case "relay5OFF":
                commandToSend = relay5Off;
                break;
            case "relay6OFF":
                commandToSend = relay6Off;
                break;
            case "relay7OFF":
                commandToSend = relay7Off;
                break;
            case "relay8OFF":
                commandToSend = relay8Off;
                break;
            case "REFRESH":
                commandToSend = all_relay_states;
                break;
            default:
                break;
        }
        return commandToSend;
    }

}
