package builder.map.cache.io;


import builder.map.cache.DataArchive;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class FileReader {

    public FileReader() {
    }

    public static byte[] readFile(String filePath) {
        byte[] data;
        try {
            File file = new File(filePath);
            int length = (int) file.length();
            data = new byte[length];
            DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
            datainputstream.readFully(data, 0, length);
            datainputstream.close();
            files_loaded++;
        } catch (Exception exception) {
            System.out.println((new StringBuilder()).append( DataArchive.getValue( 1385 )).append(filePath).toString());
            return null;
        }
        return data;
    }

    public static int files_loaded = 0;
    public static int close = 0;
    public static int length = 0;

}
