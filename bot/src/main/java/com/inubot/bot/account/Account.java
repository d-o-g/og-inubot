package com.inubot.bot.account;

import com.inubot.RuneDream;
import com.inubot.RuneDream;

/**
 * Created by luckruns0ut on 30/04/15.
 */
public class Account {
    private final String username;
    private final String password;

    public Account(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // puts this account's stuff into the login/pass fields
    public void enterCredentials() {
        RuneDream.getInstance().getClient().setUsername(username);
        RuneDream.getInstance().getClient().setPassword(password);
    }
}
