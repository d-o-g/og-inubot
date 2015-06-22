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
}
