package com.inubot.api.methods;

import com.inubot.api.util.CacheLoader;
import com.inubot.client.natives.RSItemDefinition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Septron
 * @since June 26, 2015
 */
public class Exchange {

    private static final String BASE_URL = "https://api.rsbuddy.com/grandExchange?a=guidePrice&i=";

    private static String getData(int itemId) throws IOException {
        URLConnection connection = new URL(BASE_URL + itemId).openConnection();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line = reader.readLine();
            if (line != null)
                return line;
        }
        return null;
    }

    /**
     * Looks up an item on the OSBuddy Exchange
     * @param itemId the id of the item to lookup
     * @return the price of the item, 0 if not found
     */
    public static int getPrice(int itemId) {
        try {
            String line = getData(itemId);
            if (line != null) {
                Matcher matcher = Pattern.compile("\"selling\":(\\d+)").matcher(line);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
