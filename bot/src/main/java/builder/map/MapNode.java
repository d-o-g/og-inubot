package builder.map;

import builder.map.render.RenderModel;

import java.awt.*;

public class MapNode {

    public final int x,y,z,hash;
    public boolean render = false;

    public Rectangle bounds;

    public MapNode(int x, int y, int z, int hash) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = hash;
        bounds = new Rectangle(0,0,0,0);
    }

 /*   public MapNode( TileNode node ) {
        this(node.getX(), node.getY(), node.getFloorLevel(), node.hashCode());
    }*/

    public void setBounds(int x, int y, int w, int h) {
        bounds.setBounds(x,y,w,h);
        render = x >= 0 && y >= 0 && w > 0 && h > 0 &&
                x < RenderModel.map_with - 37 && y < RenderModel.map_height - 38;
    }

    public void render() {
        if(!render) return;
        RenderModel.fillRectangle(
                bounds.x, bounds.y,
                bounds.width, bounds.height,
                Color.GREEN.getRGB()
        );
    }

    public int getCenterX() {
        return bounds.x + (bounds.width / 2);
    }

    public int getCenterY() {
        return bounds.y + (bounds.height / 2);
    }


    public boolean equals(Object o) {
        if(!(o instanceof MapNode)) return false;
        MapNode node = (MapNode) o;
        return node.x == x &&
                node.y == y&&node.z == z;
    }
}
