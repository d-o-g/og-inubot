package builder.map.render.sprites;


import builder.map.cache.ArchiveKeys;
import builder.map.cache.DataArchive;
import builder.map.cache.MapDataStore;
import builder.map.cache.io.RSInputStream;
import builder.map.render.RenderModel;

public class LandscapeEntity extends RenderModel {

    public LandscapeEntity(MapDataStore store, String sceneDataFile, int id) {
        String data_extension = DataArchive.getValue( ArchiveKeys.DATA_FILE_EXTENSION );
        String file_name = sceneDataFile + data_extension;
        String index_file = DataArchive.getValue(ArchiveKeys.INDEX_FILE_PATH);
        RSInputStream sceneData = new RSInputStream(store.getEntry(file_name, null));
        RSInputStream indexes = new RSInputStream(store.getEntry(index_file, null));
        indexes.offset = sceneData.readShort();

        /* Finalized cache for all landscape entities */
        pixel_with = indexes.readShort();
        pixel_height = indexes.readShort();
        int num_colors = indexes.readUnsignedByte();
        colorModel = new int[num_colors];
        for (int i_2_ = 0; i_2_ < num_colors - 1; i_2_++)
            colorModel[i_2_ + 1] = indexes.read24BitInt();

        for (int s = 0; s < id; s++) {
            indexes.offset += (1 + 1); //x,y
            sceneData.offset += indexes.readShort() * indexes.readShort(); //model_with x model_height
            indexes.offset++;  //type
        }

        /* Unique render rules */
        x_unknown = indexes.readUnsignedByte();
        y_unknown = indexes.readUnsignedByte();
        model_with = indexes.readShort();
        model_height = indexes.readShort();
        int model_type = indexes.readUnsignedByte();
        int num_pixels = model_with * model_height;
        renderRules = new byte[num_pixels];
        if (model_type == 0) {
            for (int i = 0; i < num_pixels; i++)
                renderRules[i] = sceneData.readByte();

        } else if (model_type == 1) {
            for (int y0 = 0; y0 < model_height; y0++) {
                for (int x0 = 0; x0 < model_with; x0++)
                    renderRules[y0 + x0 * model_height] = sceneData.readByte();
            }
        }
    }

    public final void render(int x, int y, int dest_with, int dest_height) {
        try {
            int pixel_height = this.model_height;
            int x_data_pos = 0;
            int y_data_pos = 0;
            int my_with = pixel_with;
            int my_height = this.pixel_height;
            int with_scale = (my_with << 16) / dest_with;
            int height_scale = (my_height << 16) / dest_height;
            x += ((x_unknown * dest_with + my_with) - 1) / my_with;
            y += ((y_unknown * dest_height + my_height) - 1) / my_height;
            if ((x_unknown * dest_with) % my_with != 0)
                x_data_pos = (my_with - (x_unknown * dest_with) % my_with << 16) / dest_with;
            if ((y_unknown * dest_height) % my_height != 0)
                y_data_pos = (my_height - (y_unknown * dest_height) % my_height << 16) / dest_height;
            dest_with = (dest_with * (this.model_height - (x_data_pos >> 16))) / my_with;
            dest_height = (dest_height * (this.model_with - (y_data_pos >> 16))) / my_height;
            int data_pos = x + y * map_with;
            int jump = map_with - dest_with;
            if (y < map_y) {
                int y_off = map_y - y;
                dest_height -= y_off;
                y = 0;
                data_pos += y_off * map_with;
                y_data_pos += height_scale * y_off;
            }
            if (y + dest_height > cluster_height)
                dest_height -= (y + dest_height) - cluster_height;
            if (x < map_x) {
                int x_off = map_x - x;
                dest_with -= x_off;
                x = 0;
                data_pos += x_off;
                x_data_pos += with_scale * x_off;
                jump += x_off;
            }
            if (x + dest_with > cluster_with) {
                int x_off = (x + dest_with) - cluster_with;
                dest_with -= x_off;
                jump += x_off;
            }
            apply(cluster, renderRules, colorModel, x_data_pos, y_data_pos, data_pos, jump, dest_with, dest_height, with_scale, height_scale, pixel_height);
        } catch (Exception exception) {
            System.out.println(DataArchive.getValue(997));
        }
    }

    public final void apply(int[] dest, byte[] renderRules, int[] colorModel,
                            int x_data_pos, int y_data_pos, int data_pos,
                            int jump, int with, int height,
                            int with_scale, int height_scale,
                            int pixel_height) {
        try {
            int i_35_ = x_data_pos;
            for (int y = -height; y < 0; y++) {
                int i_37_ = (y_data_pos >> 16) * pixel_height;
                for (int x = -with; x < 0; x++) {
                    int color_type = renderRules[(x_data_pos >> 16) + i_37_];
                    if (color_type != 0)
                        dest[data_pos++] = colorModel[color_type & 0xff];
                    else data_pos++;
                    x_data_pos += with_scale;
                }
                y_data_pos += height_scale;
                x_data_pos = i_35_;
                data_pos += jump;
            }

        } catch (Exception exception) {
            System.out.println(DataArchive.getValue(1030));
        }
    }

    public byte renderRules[];
    public int colorModel[];
    public int model_height;
    public int model_with;
    public int x_unknown;
    public int y_unknown;
    public int pixel_with;
    public int pixel_height;
}
