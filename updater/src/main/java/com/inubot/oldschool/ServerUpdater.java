package com.inubot.oldschool;

import com.inubot.Updater;

import java.io.File;

/**
 * Created by Administrator on 7/23/2015.
 */
public class ServerUpdater extends OSUpdater {

    public ServerUpdater(File file, boolean closeOnOld) throws Exception {
        super(file, closeOnOld);
    }

    @Override
    public String getModscriptLocation() {
        return "C:\\wamp\\www\\modscript.dat";
    }

    public static void main(String[] args) throws Exception {
        Updater updater = new ServerUpdater(null, false);
        updater.print = true;
        updater.run();
    }
}
