package com.inubot.bundledscripts.mad.modules;

import com.inubot.bundledscripts.mad.util.interfaces.Module;

/**
 * Created by mad on 7/26/15.
 */
public class Done implements Module {
    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void execute() {
        System.out.println("We are done!!!!");

    }
}
