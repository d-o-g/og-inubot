package com.inubot.client.natives.oldschool;

import com.inubot.client.natives.ClientNative;

/**
 * Created by Asus on 01/06/2017.
 */
public interface RSReferenceTable extends ClientNative {

    Object[][] getBuffer();

    RSIdentityTable[] getChildren();

    RSIdentityTable getEntry();

    byte[] unpack(int index, int file, int[] xteas);

    default byte[] unpack(int index, int file) {
        return unpack(index, file, null);
    }

    default int getFileCount() {
        Object[][] buffer = getBuffer();
        if (buffer == null) {
            return 0;
        }
        return buffer.length;
    }
}
