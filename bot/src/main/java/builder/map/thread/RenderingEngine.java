package builder.map.thread;


import builder.map.cache.DataArchive;
import builder.map.render.RenderModel;

import java.awt.*;
import java.awt.image.*;

public class RenderingEngine
        implements ImageProducer, ImageObserver {

    public final void attach() {
        RenderModel.setData(pixelCluster, with, height);
    }

    public RenderingEngine(int with, int height, Component component) {
        this.with = with;
        this.height = height;
        pixelCluster = new int[with * height];
        renderModel = new DirectColorModel(32, 0xff0000, 65280, 255);
        prepareImage = component.createImage(this);
        addConsumer();
        component.prepareImage(prepareImage, this);
        addConsumer();
        component.prepareImage(prepareImage, this);
        addConsumer();
        component.prepareImage(prepareImage, this);
        attach();
    }

    public final boolean imageUpdate(Image image, int infoflags,
                                     int x, int y, int w, int h) {
        return true;
    }

    public final void requestTopDownLeftRightResend(ImageConsumer imageconsumer) {
        System.out.println(DataArchive.getValue(699));
    }

    public final synchronized boolean isConsumer(ImageConsumer imageconsumer) {
        return anImageConsumer55 == imageconsumer;
    }

    public final synchronized void removeConsumer(ImageConsumer imageconsumer) {
        if (anImageConsumer55 == imageconsumer)
            anImageConsumer55 = null;
    }

    public final void applyGraphics(Graphics graphics, int x, int y) {
        addConsumer();
        graphics.drawImage(prepareImage, x, y, this);
    }

    public final void startProduction(ImageConsumer imageconsumer) {
        addConsumer(imageconsumer);
    }

    public final synchronized void addConsumer(ImageConsumer imageconsumer) {
        anImageConsumer55 = imageconsumer;
        imageconsumer.setDimensions(with, height);
        imageconsumer.setProperties(null);
        imageconsumer.setColorModel(renderModel);
        imageconsumer.setHints(14);
    }

    public final synchronized void addConsumer() {
        if (anImageConsumer55 != null) {
            anImageConsumer55.setPixels(0, 0, with, height, renderModel, pixelCluster, 0, with);
            anImageConsumer55.imageComplete(2);
        }
    }

    public int pixelCluster[];
    public int with;
    public int height;
    public ColorModel renderModel;
    public ImageConsumer anImageConsumer55;
    public Image prepareImage;
}
