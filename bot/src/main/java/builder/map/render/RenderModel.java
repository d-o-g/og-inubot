package builder.map.render;


import builder.map.util.Class9_Sub1;

public class RenderModel extends Class9_Sub1 {

    public RenderModel() {
    }


    /**
     * @return the greatest common denominator
     */
    public static int gcm(int a, int b) {
        return b == 0 ? a : gcm(b, a % b); // Not bad for one line of code :)
    }

    public static void drawLine(int x1, int y1, int x2, int y2, int color) {
        int maxx = Math.max(x1, x2);
        int minx = Math.min(x1, x2);
        int maxy = Math.max(y1, y2);
        int miny = Math.min(y1, y2);
        if (minx < map_x || maxx > cluster_with_minus_one || miny < map_y || maxy > cluster_height)
            return;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;

        int err = dx - dy;



        while (true) {
            int data_pos = x1 + y1 * map_with;

            if(data_pos >= 0 && data_pos < cluster.length)
                cluster[data_pos] = color;



            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }

            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
        }
    }

    public static void drawHorizontalLine(int x, int y, int length, int color) {
        if (y >= map_y && y < cluster_height) {
            if (x < map_x) {
                length -= map_x - x;
                x = map_x;
            }
            if (x + length > cluster_with)
                length = cluster_with - x;
            int data_pos = x + y * map_with;
            for (int i = 0; i < length; i++)
                cluster[data_pos + i] = color;

        }
    }

    public static void drawVerticalLine(int x, int y, int length, int color) {
        if (x >= map_x && x < cluster_with) {
            if (y < map_y) {
                length -= map_y - y;
                y = map_y;
            }
            if (y + length > cluster_height)
                length = cluster_height - y;
            int data_pos = x + y * map_with;
            for (int i = 0; i < length; i++)
                cluster[data_pos + i * map_with] = color;
        }
    }

    public static void setBounds(int x, int y, int with, int height) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (with > map_with) with = map_with;
        if (height > map_height) height = map_height;
        map_x = x;
        map_y = y;
        cluster_with = with;
        cluster_height = height;
        cluster_with_minus_one = cluster_with - 1;
        mapCenterX = cluster_with / 2;
        mapCenterY = cluster_height / 2;
    }

    public static void setData(int data[], int with, int height) {
        cluster = data;
        map_with = with;
        map_height = height;
        setBounds(0, 0, with, height);
    }

    public static void fillRectangle(int x, int y, int with, int height, int color, int alpha) {
        if (x < map_x) {
            with -= map_x - x;
            x = map_x;
        }
        if (y < map_y) {
            height -= map_y - y;
            y = map_y;
        }
        if (x + with > cluster_with)
            with = cluster_with - x;
        if (y + height > cluster_height)
            height = cluster_height - y;
        int lightness = 256 - alpha;
        int red = (color >> 16 & 0xff) * alpha;
        int green = (color >> 8 & 0xff) * alpha;
        int blue = (color & 0xff) * alpha;
        int jump = map_with - with;
        int index = x + y * map_with;
        for (int i_24_ = 0; i_24_ < height; i_24_++) {
            for (int i_25_ = -with; i_25_ < 0; i_25_++) {
                int existing_red = (cluster[index] >> 16 & 0xff) * lightness;
                int exciting_green = (cluster[index] >> 8 & 0xff) * lightness;
                int exciting_blue = (cluster[index] & 0xff) * lightness;
                int new_rgb = ((red + existing_red >> 8) << 16) + ((green + exciting_green >> 8) << 8) + (blue + exciting_blue >> 8);
                cluster[index++] = new_rgb;
            }
            index += jump;
        }

    }

    public static void drawRectangle(int x, int y, int with, int height, int color) {
        drawHorizontalLine(x, y, with, color);
        drawHorizontalLine(x, (y + height) - 1, with, color);
        drawVerticalLine(x, y, height, color);
        drawVerticalLine((x + with) - 1, y, height, color);
    }

    public static void fillRectangle(int x, int y, int with, int height, int color) {
        if (x < map_x) {
            with -= map_x - x;
            x = map_x;
        }
        if (y < map_y) {
            height -= map_y - y;
            y = map_y;
        }
        if (x + with > cluster_with)
            with = cluster_with - x;
        if (y + height > cluster_height)
            height = cluster_height - y;
        int leap = map_with - with;
        int data_pos = x + y * map_with;
        for (int y0 = -height; y0 < 0; y0++) {
            for (int x0 = -with; x0 < 0; x0++)
                cluster[data_pos++] = color;
            data_pos += leap;
        }
    }

    public static void clear() {
        int num_pixels = map_with * map_height;
        for (int i = 0; i < num_pixels; i++)
            cluster[i] = 0;
    }

    public static void fillCircle(int x, int y, int radius, int color, int alpha) {
        int lightness = 256 - alpha;
        int red = (color >> 16 & 0xff) * alpha;
        int green = (color >> 8 & 0xff) * alpha;
        int blue = (color & 0xff) * alpha;
        int y_min = y - radius;
        if (y_min < 0)
            y_min = 0;
        int y_max = y + radius;
        if (y_max >= map_height)
            y_max = map_height - 1;
        for (int y0 = y_min; y0 <= y_max; y0++) {
            int deltaY = y0 - y;
            int x_length = (int) Math.sqrt(radius * radius - deltaY * deltaY); //C^2 - Y^2 = X^2
            int min_x = x - x_length;
            if (min_x < 0) min_x = 0;
            int max_x = x + x_length;
            if (max_x >= map_with)
                max_x = map_with - 1;
            int index = min_x + y0 * map_with; // i = y * w + x
            for (int x0 = min_x; x0 <= max_x; x0++) {
                int existing_red = (cluster[index] >> 16 & 0xff) * lightness;
                int existing_green = (cluster[index] >> 8 & 0xff) * lightness;
                int existing_blue = (cluster[index] & 0xff) * lightness;
                int new_rgb = ((red + existing_red >> 8) << 16) + ((green + existing_green >> 8) << 8) + (blue + existing_blue >> 8);
                cluster[index++] = new_rgb;
            }
        }
    }

    public static int cluster[];
    public static int map_with;
    public static int map_height;
    public static int map_y = 0;
    public static int cluster_height = 0;
    public static int map_x = 0;
    public static int cluster_with = 0;
    public static int cluster_with_minus_one;
    public static int mapCenterX;
    public static int mapCenterY;

}
