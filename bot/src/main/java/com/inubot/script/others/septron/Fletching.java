package com.inubot.script.others.septron;

/**
 * @author Septron
 * @since July 04, 2015
 */
public class Fletching {


    private enum Log {
        LOGS(1511);

        public final int id;

        Log(int id) {
            this.id = id;
        }
    }

    private enum Outputs {
        ARROW_SHAFT(52, Log.LOGS);

        public final int id;

        public final Log type;

        Outputs(int id, Log type) {
            this.id = id;
            this.type = type;
        }
    }

    public static void main(String... args) {

    }
}
