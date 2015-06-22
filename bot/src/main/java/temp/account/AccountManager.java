package temp.account;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

/**
 * Created by luckruns0ut on 30/04/15.
 */
public class AccountManager {
    private static Account curAccount;

    public static void setCurrentAccount(Account account) {
        curAccount = account;
    }

    public static Account getCurrentAccount() {
        return curAccount;
    }

    public static Account generateRandomAccount() {
        String username = randomString();
        String email = randomString() + "@gmail.com";
        String password = randomString();

        try {
            if (createAccount(email, password, username)) {
                return new Account(email, password);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static String randomString() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase().substring(25);
    }

    private static boolean createAccount(String email, String password, String name) throws IOException {
        // Storing the post values
        String[][] postVars = {
                {"onlyOneEmail", "1"},
                {"age", "42"},
                {"displayname", name},
                {"email1", email},
                {"password1", password},
                {"password2", password},
                {"onyOneEmail", "1"},
                {"agree_pp_and_tac", "1"},
                {"submit", "register"}};
        URL postURL = new URL("https://secure.runescape.com/m=account-creation/g=oldscape/");
        URLConnection postURLConnect = postURL.openConnection();
        postURLConnect.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(postURLConnect.getOutputStream());
        for (String[] postVar : postVars) {
            out.write(postVar[0] + "=" + postVar[1] + "&");
        }
        out.flush();
        // Get the response
        BufferedReader br = new BufferedReader(new InputStreamReader(postURLConnect.getInputStream()));
        String line;
        boolean failed = false;
        while ((line = br.readLine()) != null) {
            //System.out.println(line);
            if (line.contains("Sorry, you are not eligible to play.") || line.contains("Sorry, you have entered an invalid age.") || line.contains("Please fill out this field.")) {
                failed = true;
            }
            if(line.contains("blocked from creating too many")) {
                System.out.println("Lol blocked from creating too many accounts.");
            }
        }
        if (failed) {
            System.out.println("Failed to create account : " + email + " / " + name);
            return false;
        } else {
            System.out.println("Created account : " + email + " / " + name + " / " + password + "!");
        }
        out.close();
        return true;
    }
}
