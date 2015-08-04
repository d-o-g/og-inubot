/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.api.methods.Projection;
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

    public Polygon[] getPolygons() {
        if (referent == null) {
            return new Polygon[0];
        }
        if (referent instanceof Character) {
            setOrientation(((Character) referent).getOrientation());
            rotate();
        }
        return new Polygon[0]; //TODO
    }

    public void render(Graphics g) {
        for (Polygon p : getPolygons()) {
            g.drawPolygon(p);
        }
    }
}
