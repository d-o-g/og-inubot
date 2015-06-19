package builder.map.cache;


import builder.map.cache.io.RSInputStream;
import builder.map.util.Class8;

public class MapDataStore {

    public MapDataStore(byte data[]) {
        unpack(data);
    }

    public final void unpack(byte data[]) {
        RSInputStream RSInputStream = new RSInputStream(data);
        int expected_length = RSInputStream.read24BitInt();
        int i_0_ = RSInputStream.read24BitInt();
        if (i_0_ != expected_length) {
            System.out.println("Using algo");
            byte dest[] = new byte[expected_length];
            Class8.method11(dest, expected_length, data, i_0_, 6);
            this.data = dest;
            RSInputStream = new RSInputStream(this.data);
            protocol = true;
        } else {
            this.data = data;
            protocol = false;
        }

        nut_entries = RSInputStream.readShort();
        entryNameHashes = new int[nut_entries];
        entrySizes = new int[nut_entries];
        anIntArray2 = new int[nut_entries];
        bytePositions = new int[nut_entries];
        int entry_size = 4 + 3 + 3;
        int i_2_ = RSInputStream.offset + nut_entries * entry_size;
        for (int entryIndex = 0; entryIndex < nut_entries; entryIndex++) {
            entryNameHashes[entryIndex] = RSInputStream.readInt();
            entrySizes[entryIndex] = RSInputStream.read24BitInt();
            anIntArray2[entryIndex] = RSInputStream.read24BitInt();
            bytePositions[entryIndex] = i_2_;
            i_2_ += anIntArray2[entryIndex];
        }

    }

    public final byte[] getEntry(String key, byte[] dest) {
        int hash = 0;
        key = key.toUpperCase();
        for (int char_index = 0; char_index < key.length(); char_index++)
            hash = (hash * 61 + key.charAt(char_index)) - 32;

        for (int i = 0; i < nut_entries; i++)
            if (entryNameHashes[i] == hash) {
                if (dest == null) dest = new byte[entrySizes[i]];
                if (!protocol) {
                    Class8.method11(dest, entrySizes[i], data, anIntArray2[i], bytePositions[i]);
                } else {
                    System.arraycopy(data, bytePositions[i], dest, 0, entrySizes[i]);
                }
                return dest;
            }

        return null;
    }

    public byte data[];
    public int nut_entries;
    public int entryNameHashes[];
    public int entrySizes[];
    public int anIntArray2[];
    public int bytePositions[];
    public boolean protocol;
}
