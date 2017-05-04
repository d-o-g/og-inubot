/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.api.methods.Projection;
import com.inubot.api.util.Random;
import com.inubot.api.util.filter.Filter;
import com.inubot.client.ClientInvoked;
import com.inubot.client.natives.oldschool.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Blitz
 */
public class Model extends Wrapper<RSModel> implements RSModel {

    private final RSRenderable parent;
    private final int[] xVertices, yVertices, zVertices;
    private final int[] xTriangles, yTriangles, zTriangles;
    private final int[] initialXVertices, initialZVertices;
    private int orientation;
    private Locatable referent;

    @ClientInvoked
    public Model(RSRenderable parent, RSModel raw) {
        super(raw);
        this.parent = parent;
        int count = raw.getXVertices().length;
        this.xTriangles = Arrays.copyOf(raw.getXTriangles(), count);
        this.yTriangles = Arrays.copyOf(raw.getYTriangles(), count);
        this.zTriangles = Arrays.copyOf(raw.getZTriangles(), count);
        count = raw.getXTriangles().length;
        this.xVertices = Arrays.copyOf(raw.getXVertices(), count);
        this.yVertices = Arrays.copyOf(raw.getYVertices(), count);
        this.zVertices = Arrays.copyOf(raw.getZVertices(), count);
        this.initialXVertices = xVertices;
        this.initialZVertices = zVertices;
        this.rotate();
    }

    public int getVertexCount() {
        return xVertices.length;
    }

    public int getTriangleCount() {
        return xTriangles.length;
    }

    public RSRenderable getParent() {
        return parent;
    }

    @Override
    public int[] getXVertices() {
        return xVertices;
    }

    @Override
    public int[] getYVertices() {
        return yVertices;
    }

    @Override
    public int[] getZVertices() {
        return zVertices;
    }

    @Override
    public int[] getXTriangles() {
        return xTriangles;
    }

    @Override
    public int[] getYTriangles() {
        return yTriangles;
    }

    @Override
    public int[] getZTriangles() {
        return zTriangles;
    }

    @Override
    public Model getModel() {
        return raw.getModel();
    }

    @Override
    public void setModel(Model model) {
        raw.setModel(model);
    }

    @Override
    public RSNode getNext() {
        return raw.getNext();
    }

    @Override
    public RSNode getPrevious() {
        return raw.getPrevious();
    }

    @Override
    public long getUid() {
        return raw.getUid();
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public Locatable getReferent() {
        return referent;
    }

    public void setReferent(Locatable referent) {
        this.referent = referent;
    }

    public void rotate() {
        int theta = orientation & 0x3fff;
        int sin = Projection.SIN_TABLE[theta];
        int cos = Projection.COS_TABLE[theta];
        for (int i = 0; i < initialXVertices.length; ++i) {
            // Note that the second row of the matrix would result
            // in no change, as the y coordinates are always unchanged
            // by rotation about the y axis.
            xVertices[i] = (initialXVertices[i] * cos + initialZVertices[i] * sin >> 15) >> 1;
            zVertices[i] = (initialZVertices[i] * cos - initialXVertices[i] * sin >> 15) >> 1;
        }
    }

    public Point[] getPoints() {
        if (referent == null) {
            return new Point[0];
        }
        if (referent instanceof Character) {
            setOrientation(((Character) referent).getOrientation());
            rotate();
        }
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < getTriangleCount(); i++) {
            if (xTriangles[i] >= xVertices.length || yTriangles[i] >= xVertices.length || zTriangles[i] >= xVertices.length) {
                break;
            }
            Point x = Projection.groundToViewport(referent.getLocation().getStrictX() + 64 + xVertices[xTriangles[i]],
                    referent.getLocation().getStrictY() + 64 + zVertices[xTriangles[i]], -yVertices[xTriangles[i]]);
            Point y = Projection.groundToViewport(referent.getLocation().getStrictX() + 64 + xVertices[yTriangles[i]],
                    referent.getLocation().getStrictY() + 64 + zVertices[yTriangles[i]], -yVertices[yTriangles[i]]);
            Point z = Projection.groundToViewport(referent.getLocation().getStrictX() + 64 + xVertices[zTriangles[i]],
                    referent.getLocation().getStrictY() + 64 + zVertices[zTriangles[i]], -yVertices[zTriangles[i]]);
            if (x != null && y != null && z != null
                    && x.x > 0 && x.y > 0
                    && y.x > 0 && y.y > 0
                    && z.x > 0 && z.y > 0) {
                y.x += 4;
                y.y += 4;
                points.add(x);
                points.add(y);
                points.add(z);
            }
        }
        return points.toArray(new Point[points.size()]);
    }

    /**
     * @return A random {@link java.awt.Point} within the Model.
     * null if no point is available
     */
    public Point getRandomPoint() {
        Point[] points = getPoints();
        return points.length > 0 ? points[Random.nextInt(points.length - 1)] : null;
    }

    /**
     * @return A random {@link java.awt.Point} within the Model
     * accepted by the filter.
     * null if no point is available
     */
    public Point getRandomPoint(Filter<Point> filter) {
        Point[] points = getPoints();
        if (points.length > 0) {
            List<Point> list = Arrays.asList(points);
            Collections.shuffle(list);
            return list.stream().filter(filter::accept).findFirst().orElse(null);
        }
        return null;
    }

    public void render(Graphics g) {
        for (Point p : getPoints()) {
            g.drawOval(p.x, p.y, 3, 3);
        }
    }
}
