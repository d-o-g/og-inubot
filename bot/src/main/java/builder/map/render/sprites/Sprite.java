package builder.map.render.sprites;


import builder.map.cache.DataArchive;
import builder.map.cache.MapDataStore;
import builder.map.cache.io.RSInputStream;
import builder.map.render.RenderModel;

public class Sprite extends RenderModel {

    public final void attach() {
        setData(colorModel, model_with, model_height);
    }

    public final void render(int x, int y) {
        x += unknown_x;
        y += unknown_y;
        int dest_pos = x + y * map_with;
        int model_pos = 0;
        int height0 = model_height;
        int with0 = model_with;
        int dest_jump = map_with - with0;
        int model_jump = 0;
        if (y < map_y) {
            int y_off = map_y - y;
            height0 -= y_off;
            y = map_y;
            model_pos += y_off * with0;
            dest_pos += y_off * map_with;
        }
        if (y + height0 > cluster_height)
            height0 -= (y + height0) - cluster_height;
        if (x < map_x) {
            int x_off = map_x - x;
            with0 -= x_off;
            x = map_x;
            model_pos += x_off;
            dest_pos += x_off;
            model_jump += x_off;
            dest_jump += x_off;
        }
        if (x + with0 > cluster_with) {
            int x_off = (x + with0) - cluster_with;
            with0 -= x_off;
            model_jump += x_off;
            dest_jump += x_off;
        }
        if (with0 > 0 && height0 > 0)
            apply(cluster, colorModel, model_pos, dest_pos, with0, height0, dest_jump, model_jump);
    }

    public final void apply(int dest[], int colorModel[],
                            int model_pos, int dest_pos,
                            int with, int height,
                            int dest_jump, int model_jump) {
        int run = -(with >> 2);
        with = -(with & 3);
        for (int y = -height; y < 0; y++) {
            for (int x = run; x < 0; x++) {
                int i = colorModel[model_pos++];
                if (i != 0)
                    dest[dest_pos++] = i;
                else
                    dest_pos++;
                i = colorModel[model_pos++];
                if (i != 0)
                    dest[dest_pos++] = i;
                else
                    dest_pos++;
                i = colorModel[model_pos++];
                if (i != 0)
                    dest[dest_pos++] = i;
                else
                    dest_pos++;
                i = colorModel[model_pos++];
                if (i != 0)
                    dest[dest_pos++] = i;
                else
                    dest_pos++;
            }

            for (int i_20_ = with; i_20_ < 0; i_20_++) {
                int i = colorModel[model_pos++];
                if (i != 0)
                    dest[dest_pos++] = i;
                else
                    dest_pos++;
            }

            dest_pos += dest_jump;
            model_pos += model_jump;
        }

    }

    public Sprite(int with, int height) {
        colorModel = new int[with * height];
        this.model_with = pixel_with = with;
        this.model_height = pixel_height = height;
        unknown_x = unknown_y = 0;
    }

    public Sprite(MapDataStore data, String functionKey, int id) {
        RSInputStream functionData = new RSInputStream(data.getEntry((new StringBuilder()).append(functionKey).append(DataArchive.getValue(1251)).toString(), null));
        RSInputStream indexData = new RSInputStream(data.getEntry( DataArchive.getValue( 1256 ), null));
        indexData.offset = functionData.readShort();
        pixel_with = indexData.readShort();
        pixel_height = indexData.readShort();
        int num_bytes = indexData.readUnsignedByte();
        int rafter[] = new int[num_bytes];
        for (int model = 0; model < num_bytes - 1; model++) {
            rafter[model + 1] = indexData.read24BitInt();
            if (rafter[model + 1] == 0)
                rafter[model + 1] = 1;
        }

        for (int i = 0; i < id; i++) {
            indexData.offset += 2;
            functionData.offset += indexData.readShort() * indexData.readShort();
            indexData.offset++;
        }

        unknown_x = indexData.readUnsignedByte();
        unknown_y = indexData.readUnsignedByte();
        model_with = indexData.readShort();
        model_height = indexData.readShort();
        int protocol = indexData.readUnsignedByte();
        int num_pixels = model_with * model_height;
        colorModel = new int[num_pixels];
        if (protocol == 0) {
            for (int i = 0; i < num_pixels; i++)
                colorModel[i] = rafter[functionData.readUnsignedByte()];
        } else if (protocol == 1) {
            for (int x0 = 0; x0 < model_with; x0++) {
                for (int y0 = 0; y0 < model_height; y0++)
                    colorModel[x0 + y0 * model_with] = rafter[functionData.readUnsignedByte()];
            }
        }
    }

    public final void render0(int x, int y) {
        x += unknown_x;
        y += unknown_y;
        int i_32_ = x + y * map_with;
        int i_33_ = 0;
        int i_34_ = model_height;
        int i_35_ = model_with;
        int i_36_ = map_with - i_35_;
        int i_37_ = 0;
        if (y < map_y) {
            int i_38_ = map_y - y;
            i_34_ -= i_38_;
            y = map_y;
            i_33_ += i_38_ * i_35_;
            i_32_ += i_38_ * map_with;
        }
        if (y + i_34_ > cluster_height)
            i_34_ -= (y + i_34_) - cluster_height;
        if (x < map_x) {
            int i_39_ = map_x - x;
            i_35_ -= i_39_;
            x = map_x;
            i_33_ += i_39_;
            i_32_ += i_39_;
            i_37_ += i_39_;
            i_36_ += i_39_;
        }
        if (x + i_35_ > cluster_with) {
            int i_40_ = (x + i_35_) - cluster_with;
            i_35_ -= i_40_;
            i_37_ += i_40_;
            i_36_ += i_40_;
        }
        if (i_35_ > 0 && i_34_ > 0)
            toString(cluster, colorModel, i_33_, i_32_, i_35_, i_34_, i_36_, i_37_);
    }

    public final void toString(int is[], int is_41_[], int i, int i_42_, int i_43_, int i_44_, int i_45_,
                               int i_46_) {
        int i_47_ = -(i_43_ >> 2);
        i_43_ = -(i_43_ & 3);
        for (int i_48_ = -i_44_; i_48_ < 0; i_48_++) {
            for (int i_49_ = i_47_; i_49_ < 0; i_49_++) {
                is[i_42_++] = is_41_[i++];
                is[i_42_++] = is_41_[i++];
                is[i_42_++] = is_41_[i++];
                is[i_42_++] = is_41_[i++];
            }

            for (int i_50_ = i_43_; i_50_ < 0; i_50_++)
                is[i_42_++] = is_41_[i++];

            i_42_ += i_45_;
            i += i_46_;
        }

    }

    public int colorModel[];
    public int model_with;
    public int model_height;
    public int unknown_x;
    public int unknown_y;
    public int pixel_with;
    public int pixel_height;
}
