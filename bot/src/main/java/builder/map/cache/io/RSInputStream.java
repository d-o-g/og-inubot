package builder.map.cache.io;

import builder.map.util.Class9_Sub1;

public class RSInputStream extends Class9_Sub1 {

    public byte buffer[];
    public int offset;

    public RSInputStream(byte buffer[]) {
        this.buffer = buffer;
        offset = 0;
    }

    public final int readInt() {
        return (readUnsignedByte() << 24) +
               (readUnsignedByte() << 16) +
               (readUnsignedByte() << 8) +
                readUnsignedByte();
    }

    public final int readUnsignedByte() {
        return readByte() & 0xff;
    }

    public final byte readByte() {
        return buffer[offset++];
    }

    public final int readShort() {
        return (readUnsignedByte() << 8) + readUnsignedByte();
    }

    public final String readString() {
        int i = offset;
        while (buffer[offset++] != 10) ;
        return new String(buffer, i, offset - i - 1);
    }

    public final int read24BitInt() {
        return (readUnsignedByte() << 16) +
                (readUnsignedByte() << 8) +
                readUnsignedByte();
    }
}
