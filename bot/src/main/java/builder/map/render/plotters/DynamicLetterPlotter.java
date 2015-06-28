package builder.map.render.plotters;


import builder.map.cache.DataArchive;
import builder.map.render.RenderModel;
import builder.map.thread.MapScheduler;

import java.awt.*;
import java.awt.image.PixelGrabber;

public class DynamicLetterPlotter extends RenderModel {

    public final void profileCharacter(Font font, FontMetrics fontmetrics, char c, int index, boolean bool, MapScheduler applet_sub1) {
        int char_with = fontmetrics.charWidth(c);
        if (bool)
            try {
                if (c == '/')
                    bool = false;
                if (c == 'f' || c == 't' || c == 'w' || c == 'v' || c == 'k' || c == 'x' || c == 'y' || c == 'A' || c == 'V' || c == 'W')
                    char_with++;
            } catch (Exception ignored) {
            }
        int y = fontmetrics.getMaxAscent();
        int char_height = fontmetrics.getMaxAscent() + fontmetrics.getMaxDescent();
        int font_height = fontmetrics.getHeight();
        Image image = applet_sub1.getDisplayedComponent().createImage(char_with, char_height);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, char_with, char_height);
        graphics.setColor(Color.white);
        graphics.setFont(font);
        graphics.drawString((new StringBuilder()).append(c).append("").toString(), 0, y);
        if (bool)
            graphics.drawString((new StringBuilder()).append(c).append("").toString(), 1, y);
        int raster[] = new int[char_with * char_height];
        PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, char_with, char_height, raster, 0, char_with);
        try {
            pixelgrabber.grabPixels();
        } catch (Exception ignored) {
        }
        image.flush();

        int minX = 0;
        int minY = 0;
        int maxX = char_with;
        int maxY = char_height;

        label0:
        for (int y0 = 0; y0 < char_height; y0++) {
            for (int x0 = 0; x0 < char_with; x0++) {
                int color = raster[x0 + y0 * char_with];
                if ((color & 0xffffff) == 0) continue;
                minY = y0;
                break label0;
            }

        }

        label1:
        for (int x0 = 0; x0 < char_with; x0++) {
            for (int y0 = 0; y0 < char_height; y0++) {
                int color = raster[x0 + y0 * char_with];
                if ((color & 0xffffff) == 0)
                    continue;
                minX = x0;
                break label1;
            }

        }

        label2:
        for (int y0 = char_height - 1; y0 >= 0; y0--) {
            for (int x0 = 0; x0 < char_with; x0++) {
                int color = raster[x0 + y0 * char_with];
                if ((color & 0xffffff) == 0) continue;
                maxY = y0 + 1;
                break label2;
            }

        }

        label3:
        for (int x0 = char_with - 1; x0 >= 0; x0--) {
            for (int y0 = 0; y0 < char_height; y0++) {
                int color = raster[x0 + y0 * char_with];
                if ((color & 0xffffff) == 0) continue;
                maxX = x0 + 1;
                break label3;
            }
        }

        charInfo[index * 9] = (byte) (append / 0x4000);      //renderCluster cords
        charInfo[index * 9 + 1] = (byte) (append / 128 & 0x7F);
        charInfo[index * 9 + 2] = (byte) (append & 0x7F);
        charInfo[index * 9 + 3] = (byte) (maxX - minX);
        charInfo[index * 9 + 4] = (byte) (maxY - minY);
        charInfo[index * 9 + 5] = (byte) minX;
        charInfo[index * 9 + 6] = (byte) (y - minY);
        charInfo[index * 9 + 7] = (byte) char_with;
        charInfo[index * 9 + 8] = (byte) font_height;

        for (int y0 = minY; y0 < maxY; y0++) {
            for (int x0 = minX; x0 < maxX; x0++) {
                int blue = raster[x0 + y0 * char_with] & 0xff;
                if (blue > 30 && blue < 230) implementExisting = true;
                charInfo[append++] = (byte) blue;
            }
        }
    }

    public final void drawString(String string, int x, int y, int rgb, boolean drawShading) {
        try {
            if (implementExisting || rgb == 0) drawShading = false;
            for (int char_index = 0; char_index < string.length(); char_index++) {
                int bytePos = CharacterInfoBytePosition[string.charAt(char_index)];
                if (drawShading) {
                    drawCharacter(bytePos, x + 1, y, 0, charInfo, implementExisting); //Lower Shading, slick..
                    drawCharacter(bytePos, x, y + 1, 0, charInfo, implementExisting); //Upper Shading
                }
                drawCharacter(bytePos, x, y, rgb, charInfo, implementExisting);
                x += charInfo[bytePos + 7];
            }
        } catch (Exception exception) {
            System.out.println((new StringBuilder()).append( DataArchive.getValue( 875 )).append(exception).toString());
            exception.printStackTrace();
        }
    }

    public DynamicLetterPlotter(int font_size, boolean bold, MapScheduler applet_sub1) {
        implementExisting = false;
        append = 0;
        charInfo = new byte[303240];
        append = 855;

        implementExisting = false;
        Font font = new Font(DataArchive.getValue(1241), bold ? 1 : 0, font_size);
        FontMetrics fontmetrics = applet_sub1.getFontMetrics(font);
        String chars = DataArchive.getValue(901);
        for (int i = 0; i < 95; i++)
            profileCharacter(font, fontmetrics, chars.charAt(i), i, false, applet_sub1);
        byte is[] = new byte[append];
        System.arraycopy(charInfo, 0, is, 0, append);
        charInfo = is;
    }

    public final void drawCenterString(String string, int x, int y, int rgb, boolean drawShading) {
        int center_x = getWith(string) / 2;
        int height = getDeltaHeight();
        if (x - center_x <= cluster_with && x + center_x >= map_x
                && y - height <= cluster_height && y >= 0)
            drawString(string, x - center_x, y, rgb, drawShading);
    }

    public final void implementOverlay(int overlay[], byte charCache[],
                                       int rgb, int render_pos, int overlay_pos,
                                       int with, int height,
                                       int overlay_jump, int render_jump) {
        for (int y0 = -height; y0 < 0; y0++) {
            for (int x0 = -with; x0 < 0; x0++) {
                int blue = charCache[render_pos++] & 0xff;
                if (blue > 30) {
                    if (blue >= 230) {
                        overlay[overlay_pos++] = rgb;
                    } else {
                        int exciting = overlay[overlay_pos];
                        overlay[overlay_pos++] =
                                ((rgb & 0xff00ff) * blue +
                                        (exciting & 0xff00ff) * (256 - blue) & 0xff00ff00) +
                                        ((rgb & 0xff00) * blue +
                                                (exciting & 0xff00) * (256 - blue) & 0xff0000) >> 8;
                    }
                } else {
                    overlay_pos++;
                }
            }

            overlay_pos += overlay_jump;
            render_pos += render_jump;
        }

    }

    public final int getFontHeight() {
        return charInfo[8] - 1;
    }

    public final int getWith(String string) {
        int with = 0;
        for (int index = 0; index < string.length(); index++)
            if (string.charAt(index) == '@' && index + 4 < string.length() && string.charAt(index + 4) == '@')
                index += 4;
            else if (string.charAt(index) == '~' && index + 4 < string.length() && string.charAt(index + 4) == '~')
                index += 4;
            else
                with += charInfo[CharacterInfoBytePosition[string.charAt(index)] + 7];
        return with;
    }

    public final void drawCharacter(int bytePos, int x, int y, int rgb, byte data[], boolean useExisting) {
        int real_x = x + data[bytePos + 5]; //x + minX
        int real_y = y - data[bytePos + 6]; //y - (
        int with = data[bytePos + 3];
        int height = data[bytePos + 4];
        int renderPos = data[bytePos] * 16384 + data[bytePos + 1] * 128 + data[bytePos + 2];
        int dest_index = real_x + real_y * map_with;
        int overlay_jump = map_with - with;
        int render_jump = 0;
        if (real_y < map_y) {
            int offY = map_y - real_y;
            height -= offY;
            real_y = map_y;
            renderPos += offY * with;
            dest_index += offY * map_with;
        }
        if (real_y + height >= cluster_height)
            height -= ((real_y + height) - cluster_height) + 1;
        if (real_x < map_x) {
            int offX = map_x - real_x;
            with -= offX;
            real_x = map_x;
            renderPos += offX;
            dest_index += offX;
            render_jump += offX;
            overlay_jump += offX;
        }
        if (real_x + with >= cluster_with) {
            int offX = ((real_x + with) - cluster_with) + 1;
            with -= offX;
            render_jump += offX;
            overlay_jump += offX;
        }
        if (with > 0 && height > 0) {
            if (useExisting) {
                implementOverlay(cluster, data, rgb, renderPos, dest_index, with, height, overlay_jump, render_jump);
            } else {
                forceOverlay(cluster, data, rgb, renderPos, dest_index, with, height, overlay_jump, render_jump);
            }
        }
    }

    public final void forceOverlay(
            int overlay[], byte charCache[], int color,
            int render_pos, int overlay_pos,
            int with, int height,
            int overlay_jump, int render_jump) {
        try {
            int x_start = -(with >> 2);
            with = -(with & 3);

            for (int y0 = -height; y0 < 0; y0++) {

                for (int x0 = x_start; x0 < 0; x0++) {
                    if (charCache[render_pos++] != 0) overlay[overlay_pos++] = color;
                    else overlay_pos++;
                    if (charCache[render_pos++] != 0) overlay[overlay_pos++] = color;
                    else overlay_pos++;
                    if (charCache[render_pos++] != 0) overlay[overlay_pos++] = color;
                    else overlay_pos++;
                    if (charCache[render_pos++] != 0) overlay[overlay_pos++] = color;
                    else overlay_pos++;
                }

                for (int x0 = with; x0 < 0; x0++) {
                    if (charCache[render_pos++] != 0) overlay[overlay_pos++] = color;
                    else overlay_pos++;
                }

                overlay_pos += overlay_jump;
                render_pos += render_jump;
            }
        } catch (Exception exception) {
            System.out.println((new StringBuilder()).append(DataArchive.getValue(888)).append(exception).toString());
            exception.printStackTrace();
        }
    }

    public final int getDeltaHeight() {
        return charInfo[6];
    }

    public boolean implementExisting;
    public int append;
    public byte charInfo[];
    public static int CharacterInfoBytePosition[];

    static {
        CharacterInfoBytePosition = new int[256];
        String char_chain = DataArchive.getValue(901);
        for (int char_id = 0; char_id < 256; char_id++) {
            int char_index = char_chain.indexOf(char_id);
            if (char_index == -1) char_index = 74;
            CharacterInfoBytePosition[char_id] = char_index * 9;
        }
    }
}
