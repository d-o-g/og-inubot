package com.inubot.bundledscripts.complete;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RSExchange {

    private final String ITEMS_JSON;

    public RSExchange() {
        ITEMS_JSON = getItemsJson().orElse("");
    }

    private Optional<String> getItemsJson() {
        try {
            URL url = new URL("https://rsbuddy.com/exchange/summary.json");
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
            con.setUseCaches(true);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json = br.readLine();
            br.close();
            return Optional.of(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public final Optional<ExchangeItem> getExchangeItem(final String itemName) {
        return getItemID(ITEMS_JSON, itemName).map(id -> new ExchangeItem(itemName, id));
    }

    public final Map<String, ExchangeItem> getExchangeItems(final String... itemNames) {
        Map<String, ExchangeItem> exchangeItems = new HashMap<>();
        for (final String itemName : itemNames) {
            getItemID(ITEMS_JSON, itemName).ifPresent(id -> exchangeItems.put(itemName, new ExchangeItem(itemName, id)));
        }
        return exchangeItems;
    }

    private Optional<Integer> getItemID(final String json, final String itemName) {
        return getItemFromJson(json, itemName).flatMap(this::getItemIDFromItemJson);
    }

    private Optional<String> getItemFromJson(final String json, final String itemName) {
        Matcher matcher = Pattern.compile("(\\{[^}]*\"name\"\\s*:\\s*\"" + Pattern.quote(itemName) + "\"[^}]*})").matcher(json);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }

    private Optional<Integer> getItemIDFromItemJson(final String json) {
        Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*(\\d*)").matcher(json);
        return matcher.find() ? Optional.of(Integer.parseInt(matcher.group(1))) : Optional.empty();
    }
}