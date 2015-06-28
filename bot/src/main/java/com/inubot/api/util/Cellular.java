package com.inubot.api.util;

/**
 * Created by bytehound on 6/23/2015.
 */
public class Cellular {

    public final static String VIRGIN_MOBILE = "vmobl.com";
    public final static String T_MOBILE = "tmomail.net";
    public final static String CINGULAR = "cingularme.com";
    public final static String SPRINT = "messaging.sprintpcs.com";
    public final static String VERIZON = "vtext.com";
    public final static String NEXTEL = "messaging.nextel.com";

    public static boolean send(Phone phone, String message) {
        return false;
    }

    public static class Phone {

        private String number, provider;

        public Phone(String number, String provider) {
            this.number = number;
            this.provider = provider;
        }

        public String getNumber() {
            return number;
        }

        public String getProvider() {
            return provider;
        }

        public String getPhone() {
            return getNumber().concat("@").concat(getProvider());
        }
    }
}
