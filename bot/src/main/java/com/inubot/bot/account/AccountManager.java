package com.inubot.bot.account;

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
