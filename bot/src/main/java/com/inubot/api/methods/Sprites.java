/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URL;

/**
 * @author unsigned
 * @since 18-05-2015
 */
public class Sprites {

    private static final String BASE = "http://2007.runescape.wikia.com/wiki/";

    public static Image fromWikia(String itemName) {
        itemName = itemName.replace(" ", "_"); //TODO check what other replacing is needed
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(BASE + itemName).openStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("/" + itemName + ".png") && line.contains("src=\"http")) {
                    int httpIdx = line.indexOf("http"), pngIdx = line.indexOf(".png");
                    if (httpIdx < 0 || pngIdx < 0)
                        continue;
                    String url = line.substring(httpIdx, pngIdx);
                    return ImageIO.read(new URL(url + ".png"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
