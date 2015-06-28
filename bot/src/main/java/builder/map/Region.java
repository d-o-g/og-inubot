package builder.map;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Region implements Comparable<Region>{

    public final int regionId;
    public int setCollision = 0;

    public static final int REGION_LENGTH = 64;
    public static final int GAME_REGION_LENGTH = 104;
    public static final int MAX_PLANES = 4;
    public static final int PLANE_SIZE = REGION_LENGTH * REGION_LENGTH;
    public static final int MAX_SET_ELEMENTS = MAX_PLANES * PLANE_SIZE;
    public static final int REGION_BYTE_CAPACITY = MAX_SET_ELEMENTS * 4 + 8;
    public static final int UNSETTLED_VALUE = -1;

    private final RandomAccessFile file;
    private final MappedByteBuffer buff;

    public static void createRegion(int regionID, File location) throws IOException {
        RandomAccessFile file = new RandomAccessFile(location, "rw");
        MappedByteBuffer buff = file.getChannel().map(
                FileChannel.MapMode.READ_WRITE, 0, REGION_BYTE_CAPACITY);
        buff.putInt(regionID);
        buff.putInt(0);
        for (int i = 8; i < REGION_BYTE_CAPACITY; i += 4)
            buff.putInt(i, UNSETTLED_VALUE);
        file.close();
    }

    public Region(File location) throws IOException {
        file = new RandomAccessFile(location, "rw");
        buff = file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, REGION_BYTE_CAPACITY);
        this.regionId = buff.getInt();
        this.setCollision = buff.getInt();
    }

    public static int makeRegionID(int rx, int ry) {
        return (rx << 8) + ry;
    }

    public int getGridX() {
        return regionId >> 8;
    }

    public int getGridY() {
        return regionId & 0xFF;
    }

    public int getX() {
        return getGridX() * 64;
    }

    public int getY() {
        return getGridY() * 64;
    }

    public static int get2DIndex(int x, int y) {
        return REGION_LENGTH * y + x;
    }

    public static int get3DIndex(int x, int y, int z) {
        return PLANE_SIZE * z + get2DIndex(x, y);
    }

    public int getValue(int gx, int gy, int gz) {
        int index = get3DIndex(gx, gy, gz);
        return getValue(index);
    }

    private int getBytePosition(int index) {
        return index * 4 + 8;
    }

    public int getValue(int index) {
        int position = getBytePosition(index);
        return buff.getInt(position);
    }

    public int settle(int index, int flag) throws IOException {
        int position = getBytePosition(index);
        int exciting = buff.getInt(position);
        if(exciting == UNSETTLED_VALUE)
            buff.putInt(4, setCollision++);
        buff.putInt(position, flag);

        return exciting;
    }

    public int[][] toMatrix(int plane) throws IOException {
        int[][] ret = new int[REGION_LENGTH][REGION_LENGTH];
        for(int y = 0; y < REGION_LENGTH; y++) {
            for(int x = 0; x < REGION_LENGTH; x++) {
                ret[x][y] = getValue(x,y, plane);
            }
        }
        return ret;
    }

    public int settle(int gx, int gy, int gz, int flag) throws IOException {
        int index = get3DIndex(gx, gy, gz);
        return settle(index, flag);
    }

    public void close() throws IOException {
        file.close();
    }

    @Override
    public int compareTo(Region o) {
        return Integer.compare(regionId,o.regionId);
    }
}
