package com.inubot.api.oldschool;

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

    private static String data(int item) throws IOException {
        URLConnection connection = new URL(BASE_URL + item).openConnection();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line = reader.readLine();
            if (line != null)
                return line;
        }
        return null;
    }

    public static int price(int item) {
        try {
            String line = data(item);
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
