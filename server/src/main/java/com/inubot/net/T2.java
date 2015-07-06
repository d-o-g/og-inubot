package com.inubot.net;

import org.mindrot.jbcrypt.BCrypt;

/**
 * @author Septron
 * @since July 04, 2015
 */
public class T2 {

    public static final String salt = "cpsvpkoiy3Aru3KEXkx9BD";

    public static final String hash = "$2a$13$ySl114KL0U9it6IekfSPKeqMUgtMVEQ01bubLLKDIBHpr7d2ffKfu";

    public static void main(String... args) {
        System.out.println(BCrypt.checkpw("password", hash));
    }
}
