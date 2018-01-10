package com.inubot.tools;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class UserNameChecker {

    private static final String CHECK_NAME_URL = "https://secure.runescape.com/m=account-creation/l=3/g=oldscape/check_displayname.ajax";
    private static final String CREATE_ACCOUNT_URL = "https://secure.runescape.com/m=account-creation/l=3/g=oldscape/create_account";

    private static void run(String name) throws IOException {
        if (name.length() > 12) {
            return;
        }
        URLConnection urlConnection = new URL(CHECK_NAME_URL).openConnection();
        urlConnection.addRequestProperty("Referer", CREATE_ACCOUNT_URL);
        urlConnection.setDoOutput(true);

        OutputStream outputStream = urlConnection.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.write(("displayname=" + name).getBytes(StandardCharsets.UTF_8));
        dataOutputStream.flush();

        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8);
        String data;
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            data = reader.lines().collect(Collectors.joining());
        }

        System.out.println(data);
    }

    public static void main(String[] args) throws IOException {
        run("warmpuss3");
    }
}

