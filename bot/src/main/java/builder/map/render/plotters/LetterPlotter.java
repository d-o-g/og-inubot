package builder.map.render.plotters;


import builder.map.cache.DataArchive;
import builder.map.cache.MapDataStore;
import builder.map.cache.io.RSInputStream;
import builder.map.render.RenderModel;

import java.util.Random;

public class LetterPlotter extends RenderModel {

    public final int getStringWith(String string) {
        if (string == null) return 0;
        int with = 0;
        for (int i = 0; i < string.length(); i++)
            with += absulteWith[string.charAt(i)];
        return with;
    }

    public final void drawString(String string, int x, int y, int color) {
        if (string != null) {
            y -= anInt87;
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (c != ' ')
                    drawCharacter(renderFlags[c], x + xOffsets[c], y + yOffsets[c], charWiths[c], charHeights[c], color);
                x += absulteWith[c];
            }
        }
    }

    public LetterPlotter(MapDataStore store, String string, boolean bool) {
        renderFlags = new byte[256][];
        charWiths = new int[256];
        charHeights = new int[256];
        xOffsets = new int[256];
        yOffsets = new int[256];
        absulteWith = new int[256];
        anInt87 = 0;
        aRandom88 = new Random();
        aBool89 = false;
        System.out.println("Data: " + string + ","+ ( DataArchive.getValue( 1256 )));
        RSInputStream stream = new RSInputStream(store.getEntry((new StringBuilder()).append(string).append(DataArchive.getValue(1251)).toString(), null));
        RSInputStream indexData = new RSInputStream(store.getEntry(DataArchive.getValue(1256), null));
        indexData.offset = stream.readShort() + 4;
        int i = indexData.readUnsignedByte();
        if (i > 0) indexData.offset += 3 * (i - 1);
        for (int i_5_ = 0; i_5_ < 256; i_5_++) {
            xOffsets[i_5_] = indexData.readUnsignedByte();
            yOffsets[i_5_] = indexData.readUnsignedByte();
            int i_6_ = charWiths[i_5_] = indexData.readShort();
            int i_7_ = charHeights[i_5_] = indexData.readShort();
            int i_8_ = indexData.readUnsignedByte();
            int i_9_ = i_6_ * i_7_;
            renderFlags[i_5_] = new byte[i_9_];
            if (i_8_ == 0) {
                for (int i_10_ = 0; i_10_ < i_9_; i_10_++)
                    renderFlags[i_5_][i_10_] = stream.readByte();

            } else if (i_8_ == 1) {
                for (int i_11_ = 0; i_11_ < i_6_; i_11_++) {
                    for (int i_12_ = 0; i_12_ < i_7_; i_12_++)
                        renderFlags[i_5_][i_11_ + i_12_ * i_6_] = stream.readByte();

                }

            }
            if (i_7_ > anInt87 && i_5_ < 128)
                anInt87 = i_7_;
            xOffsets[i_5_] = 1;
            absulteWith[i_5_] = i_6_ + 2;
            int i_13_ = 0;
            for (int i_14_ = i_7_ / 7; i_14_ < i_7_; i_14_++)
                i_13_ += renderFlags[i_5_][i_14_ * i_6_];

            if (i_13_ <= i_7_ / 7) {
                absulteWith[i_5_]--;
                xOffsets[i_5_] = 0;
            }
            i_13_ = 0;
            for (int i_15_ = i_7_ / 7; i_15_ < i_7_; i_15_++)
                i_13_ += renderFlags[i_5_][(i_6_ - 1) + i_15_ * i_6_];

            if (i_13_ <= i_7_ / 7)
                absulteWith[i_5_]--;
        }

        if (bool)
            absulteWith[32] = absulteWith[73];
        else
            absulteWith[32] = absulteWith[105];
    }

    public final void drawTailingString(String string, int x, int y, int color) {
        drawString(string, x - getStringWith(string), y, color);
    }

    public final void drawCenterString(String text, int centerX, int centerY, int textShading) {
        drawString(text, centerX - getStringWith(text) / 2, centerY, textShading);
    }

    public final void drawCharacter(byte flags[], int x, int y, int with, int height, int color) {
        int i_24_ = x + y * map_with;
        int i_25_ = map_with - with;
        int i_26_ = 0;
        int i_27_ = 0;
        if (y < map_y) {
            int i_28_ = map_y - y;
            height -= i_28_;
            y = map_y;
            i_27_ += i_28_ * with;
            i_24_ += i_28_ * map_with;
        }
        if (y + height >= cluster_height)
            height -= ((y + height) - cluster_height) + 1;
        if (x < map_x) {
            int i_29_ = map_x - x;
            with -= i_29_;
            x = map_x;
            i_27_ += i_29_;
            i_24_ += i_29_;
            i_26_ += i_29_;
            i_25_ += i_29_;
        }
        if (x + with >= cluster_with) {
            int i_30_ = ((x + with) - cluster_with) + 1;
            with -= i_30_;
            i_26_ += i_30_;
            i_25_ += i_30_;
        }
        if (with > 0 && height > 0)
            applyRendering(cluster, flags, color, i_27_, i_24_, with, height, i_25_, i_26_);
    }

    public final void applyRendering(int dest[], byte flags[], int color, int flag_pos, int data_pos, int with, int height,
                                     int data_jump, int flag_jump) {

        int i_38_ = -(with >> 2);
        with = -(with & 3);
        for (int i_39_ = -height; i_39_ < 0; i_39_++) {
            for (int i_40_ = i_38_; i_40_ < 0; i_40_++) {
                if (flags[flag_pos++] != 0)
                    dest[data_pos++] = color;
                else
                    data_pos++;
                if (flags[flag_pos++] != 0)
                    dest[data_pos++] = color;
                else
                    data_pos++;
                if (flags[flag_pos++] != 0)
                    dest[data_pos++] = color;
                else
                    data_pos++;
                if (flags[flag_pos++] != 0)
                    dest[data_pos++] = color;
                else
                    data_pos++;
            }

            for (int i_41_ = with; i_41_ < 0; i_41_++)
                if (flags[flag_pos++] != 0)
                    dest[data_pos++] = color;
                else
                    data_pos++;

            data_pos += data_jump;
            flag_pos += flag_jump;
        }

    }

    public byte renderFlags[][];
    public int charWiths[];
    public int charHeights[];
    public int xOffsets[];
    public int yOffsets[];
    public int absulteWith[];
    public int anInt87;
    public Random aRandom88;
    public boolean aBool89;
}
