package com.inubot.model;

/**
 * @author Septron
 * @since July 07, 2015
 */
public class Account {

    private String username;
    private String password;
    private String salt;
    private int id;
    private int group;

    public Account(String username, String password, String salt) {
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

    public Account() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public UserGroup getUserGroup() {
        try {
            return UserGroup.values()[getGroup()];
        } catch (Exception e) {
            return UserGroup.BANNED;
        }
    }

    public enum UserGroup {

        GUEST(false), REGISTERED(false), SUPER_MOD(true, 1), ADMIN(true, 1337), AWAITING_ACTIVATION(false),
        MOD(true, 10), BANNED(false), MEMBER(true, 2), VIP(true, 5), SPONSOR(true, 50);

        private final boolean canBot;
        private final int maximumInstances;

        UserGroup(boolean canBot, int maximumInstances) {
            this.canBot = canBot;
            this.maximumInstances = maximumInstances;
        }

        UserGroup(boolean canBot) {
            this(canBot, 0);
        }

        public int getId() {
            return super.ordinal() + 1;
        }

        public boolean canBot() {
            return canBot;
        }

        public int getMaximumInstances() {
            return maximumInstances;
        }
    }
}
