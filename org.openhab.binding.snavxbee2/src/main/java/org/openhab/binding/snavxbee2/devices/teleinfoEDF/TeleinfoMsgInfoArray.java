package org.openhab.binding.snavxbee2.devices.teleinfoEDF;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeleinfoMsgInfoArray {

    private List<TeleinfoMsgInfo> teleInfoMsgList = new ArrayList<TeleinfoMsgInfo>();
    public TeleinfoMsgInfo timsg = new TeleinfoMsgInfo();
    private Logger logger = LoggerFactory.getLogger(TeleinfoMsgInfoArray.class);

    private static String ADCO = "Adresse du compteur";
    private static String OPTARIF = "Option tarifaire choisie";
    private static String ISOUSC = "Intensité souscrite";
    private static String BASE = "Index option Base";
    private static String HCHC = "Heures Creuses";
    private static String HCHP = "Heures Pleines";
    private static String EJPHN = "Heures Normales";
    private static String EJPHPM = "Heures de Pointe Mobile";
    private static String BBRHCJB = "Heures Creuses Jours Bleus";
    private static String BBRHPJB = "Heures Pleines Jours Bleus";
    private static String BBRHCJW = "Heures Creuses Jours Blancs";
    private static String BBRHPJW = "Heures Pleines Jours Blancs";
    private static String BBRHCJR = "Heures Creuses Jours Rouges";
    private static String BBRHPJR = "Heures Pleines Jours Rouges";
    private static String PEJP = "Préavis Début EJP (30 min)";
    private static String PTEC = "Période Tarifaire en cours";
    private static String DEMAIN = "Couleur du lendemain";
    private static String IINST = "Intensité Instantanée";
    private static String ADPS = "Avertissement de Dépassement De Puissance Souscrite";
    private static String IMAX = "Intensité maximale appelée";
    private static String HHPHC = "Horaire Heures Pleines Heures Creuses";
    private static String PMAX = "Puissance maximale triphasée atteinte";
    private static String MOTDETAT = "Mot d'Etat du compteur";
    private static String PAPP = "Puissance apparente";
    private static String PPOT = "Présence des potentiels";

    public TeleinfoMsgInfo getTimsg(String messageID) {
        boolean notfound = true;
        for (TeleinfoMsgInfo msg : this.teleInfoMsgList) {
            if (msg.getMessageID().equals(messageID)) {
                timsg = msg;
                notfound = false;
            }
        }

        if (notfound) {
            System.out.println(messageID);
        }
        return timsg;
    }

    public TeleinfoMsgInfoArray() {
        super();
        // TODO Auto-generated constructor stub

        teleInfoMsgList.add(new TeleinfoMsgInfo(ADCO, "ADCO", 12, ""));
        teleInfoMsgList.add(new TeleinfoMsgInfo(OPTARIF, "OPTARIF", 4, ""));
        teleInfoMsgList.add(new TeleinfoMsgInfo(ISOUSC, "ISOUSC", 2, "A"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(BASE, "BASE", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(HCHC, "HCHC", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(HCHP, "HCHP", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(EJPHN, "EJPHN", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(EJPHPM, "EJPHPM", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(BBRHCJB, "BBRHCJB", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(BBRHPJB, "BBRHPJB", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(BBRHCJW, "BBRHCJW", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(BBRHPJW, "BBRHPJW", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(BBRHCJR, "BBRHCJR", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(BBRHPJR, "BBRHPJR", 9, "Wh"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(PEJP, "PEJP", 2, "min"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(PTEC, "PTEC", 4, ""));
        teleInfoMsgList.add(new TeleinfoMsgInfo(DEMAIN, "DEMAIN", 4, ""));
        teleInfoMsgList.add(new TeleinfoMsgInfo(IINST, "IINST", 3, "A"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(ADPS, "ADPS", 3, "A"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(IMAX, "IMAX", 3, "A"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(HHPHC, "HHPHC", 1, ""));
        teleInfoMsgList.add(new TeleinfoMsgInfo(PMAX, "PMAX", 5, "W"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(MOTDETAT, "MOTDETAT", 6, ""));
        teleInfoMsgList.add(new TeleinfoMsgInfo(PAPP, "PAPP", 5, "VA"));
        teleInfoMsgList.add(new TeleinfoMsgInfo(PPOT, "PPOT", 2, ""));
    }

    public int getLength(String messageID) {
        // TODO Auto-generated method stub
        // return super.toString();
        int l = 0;

        for (TeleinfoMsgInfo msg : this.teleInfoMsgList) {

            if (msg.getMessageID().equals(messageID)) {

                // System.out.println(msg.getMessageID() + " " + messageID);
                l = msg.length;
                // System.out.println(l);
            }
        }

        if (l == 0) {
            System.out.println(messageID);
        }

        return l;
    }

    public boolean match(String messageID) {
        boolean found = false;

        for (TeleinfoMsgInfo msg : this.teleInfoMsgList) {
            if (msg.getMessageID().equals(messageID)) {
                found = true;
            }
        }

        if (!found) {
            logger.debug("Could not find message : {}", messageID);
        }

        return found;
    }

    public TeleinfoMsgInfo messageIDLookup(String messageID) {
        TeleinfoMsgInfo tim = new TeleinfoMsgInfo();

        for (TeleinfoMsgInfo msg : this.teleInfoMsgList) {
            if (msg.getMessageID().equals(messageID)) {
                tim = msg;
            }
        }

        return tim;
    }

}
