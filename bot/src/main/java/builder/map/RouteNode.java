package builder.map;

import builder.map.render.RenderModel;

import java.awt.*;

public class RouteNode {

    public final MapNode A, B;
    public double weight;

    public RouteNode(MapNode A, MapNode B, double weight) {
        this.A = A;
        this.B = B;
        this.weight = weight;
    }

    public void render() {
        if (!A.render || !B.render) return;

        RenderModel.drawLine(
                A.getCenterX(), A.getCenterY(),
                B.getCenterX(), B.getCenterY(),
                Color.ORANGE.getRGB()
        );

    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
