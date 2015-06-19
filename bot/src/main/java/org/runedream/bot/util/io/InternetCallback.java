package org.runedream.bot.util.io;

/**
 * @author Tyler Sedlar
 */
public abstract class InternetCallback {

    public int length = -1;

    public abstract void onDownload(int percent);
}
