package builder.map.cache;

import java.io.InputStream;

public class DataArchive {

    static byte data[];
    static String values[] = new String[256];
    static int keyChain[] = new int[256];

    static {
        try {
            InputStream inputstream = (new DataArchive()).getClass().getResourceAsStream("keyData.dat");
            if (inputstream != null) {
                int num_bytes = inputstream.read() << 16 | inputstream.read() << 8 | inputstream.read();
                data = new byte[num_bytes];
                int index = 0;
                byte base = (byte) num_bytes;
                while (num_bytes != 0) {
                    int chunk_size = inputstream.read(data, index, num_bytes);
                    if (chunk_size == -1) break;
                    num_bytes -= chunk_size;
                    chunk_size += index;
                    while (index < chunk_size) {
                        data[index] ^= base;
                        index++;
                    }
                }
                inputstream.close();
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public DataArchive() {
    }

    public static synchronized String getValue(int id) {
        int index = id & 0xff;
        if (keyChain[index] != id) {
            keyChain[index] = id;
            if (id < 0) id &= 0xffff;
            int length = data[id - 1] & 0xff;
            String string = (new String(data, id, length)).intern();
            values[index] = string;
        }
        return values[index];
    }
}
