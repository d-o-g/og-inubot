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

        //for now anyone can use
        EMPTY(0), GUEST(0), REGISTERED(1), SUPER_MOD(10), ADMIN(1337), AWAITING_ACTIVATION(0),
        MOD(10), BANNED(0), MEMBER(5), VIP(10), SCRIPTER(10), SPONSOR(50);

        private final int maximumInstances;

        UserGroup(int maximumInstances) {
            this.maximumInstances = maximumInstances;
        }

        public int getId() {
            return super.ordinal() + 1;
        }

        public int getMaximumInstances() {
            return maximumInstances;
        }
    }
}
