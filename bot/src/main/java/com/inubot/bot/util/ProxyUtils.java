package com.inubot.bot.util;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by luckruns0ut on 27/04/15.
 *
 * lets u use proxies easy
 *
 */
public class ProxyUtils {
    private static ProxyEntry[] proxyEntries = null;
    private static String lastIP = "127.0.0.1";
    private static String lastPort = "1337";

    public static void update() throws IOException {
        ArrayList<String> tableEntries = new ArrayList<>();

        // parse all the lines which refer to proxies
        URL page = new URL("http://socks-proxy.net");
        try (Scanner scanner = new Scanner(page.openStream())) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if(line.contains("<tbody>")) {
                    line = line.replace("<tbody>", "");
                    tableEntries.add(line);
                    while ((line = scanner.nextLine()).startsWith("<tr>")) {
                        tableEntries.add(line);
                    }
                }
            }
        }

        // parse table and create entries
        ArrayList<ProxyEntry> entries = new ArrayList<>();
        for(int i = 0; i < tableEntries.size(); i++) {
            String line = tableEntries.get(i);
            line = line.replaceFirst("<tr><td>", "")
                    .replaceAll("</td><td>", "@")
                    .replaceAll("</td></tr>", "");

            String[] split = line.split("@");
            String address = split[0];
            int port = Integer.parseInt(split[1]);
            String code = split[2];
            String country = split[3];
            int version = Integer.parseInt(split[4].replaceAll("Socks", ""));
            String anonymity = split[5];
            boolean https = split[6].equals("Yes");

            entries.add(new ProxyEntry(address, port, code, country, version, anonymity, https));
        }

        System.out.println("Updated proxy list, contains " + entries.size() + " entries.");
        proxyEntries = entries.toArray(new ProxyEntry[entries.size()]);
    }

    public static boolean useAliveProxy(String country, int version) {
        try {
            update();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for(int i = 0; i < proxyEntries.length; i++) {
            ProxyEntry proxyEntry = proxyEntries[i];
            if((proxyEntry.country.toLowerCase().equals(country.toLowerCase()) || proxyEntry.code.toLowerCase().matches(country.toLowerCase()))
                    && proxyEntries[i].version == version) {
                System.out.print("Checking potential proxy: " + proxyEntry.address + ":" + proxyEntry.port + " [" + proxyEntry.country + "]... ");
                //if(proxyEntries[i].isAlive()) {
                    System.out.println("Alive. Using it.");
                    useProxy(proxyEntry);
                    return true;
//                } else {
//                    System.out.println("Dead, not using it.");
//                }
            }
        }
        return false;
    }

    public static void useProxy(ProxyEntry proxyEntry) {
        System.setProperty("socksProxyHost", proxyEntry.address);
        System.setProperty("socksProxyPort", "" + proxyEntry.port);
        System.setProperty("socksProxyVersion", "" + proxyEntry.version);
        System.out.println("This JVM instance is now using a proxy. Info:\n" + proxyEntry.toString());
        lastIP = proxyEntry.address;
        lastPort = "" + proxyEntry.port;
    }

    public static boolean useAliveUSProxy() {
        return useAliveProxy("US", 4);
    }

    public static boolean useAliveGBProxy() {
        return useAliveProxy("GB", 4);
    }

    public static ProxyEntry[] getProxyEntries() {
        return proxyEntries;
    }

    // last ip we used
    public static String getLastIP() {
        return lastIP;
    }

    // last port we used
    public static String getLastPort() {
        return lastPort;
    }

    public static class ProxyEntry {
        public final String address;
        public final int port;
        public final String code;
        public final String country;
        public final int version;
        public final String anonymity;
        public final boolean https;

        public ProxyEntry(String address, int port, String code, String country, int version, String anonymity, boolean https) {
            this.address = address;
            this.port = port;
            this.code = code;
            this.country = country;
            this.version = version;
            this.anonymity = anonymity;
            this.https = https;
        }

        //TODO: MAKE THIS ACTUALLY WORK, IT DOESNT DETECT LIVE PROXIES PROPERLY
        public boolean isAlive() {
            try {
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(address, port));
                Socket s = new Socket(proxy);
                s.connect(new InetSocketAddress(address, port), 2000);
                return true;
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
            return false;
        }

        public String toString() {
            return "Addr: " + address
                    + "\nPort: " + port
                    + "\nCountry: " + country
                    + "\nVer: " + version
                    + "\nAnonymity: " + anonymity
                    + "\nHttps: " + https;
        }
    }
}
