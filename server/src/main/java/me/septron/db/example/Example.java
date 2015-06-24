package me.septron.db.example;

import me.septron.db.Column;
import me.septron.db.Table;
import me.septron.db.connection.impl.SQLConnection;
import me.septron.db.entity.IEntity;
import me.septron.db.entity.IEntityFactory;
import me.septron.db.entity.impl.SQLFactory;

public class Example {

    @Table(name = "user")
    private static class User implements IEntity {

        @Column(name = "username")
        private String username;

        @Column(name = "password")
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String username() { return username; }
        public String password() { return password; }

        public void username(String username) { this.username = username; }
        public void password(String password) { this.password = password; }
    }

    public static void main(String... arguments) throws Exception {
        SQLConnection connection = new SQLConnection("website", "root", "password");
        try {
            connection.init();
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        IEntityFactory<User> factory = new SQLFactory<>(connection, User.class);

        User penis = new User("penis", "password");
        if (!factory.put(penis))
            throw new RuntimeException("Failed to put user into database!");

        User dick = factory.get("dick", "user-b");
        if (dick == null)
            throw new RuntimeException("Failed to find user-b.");

        penis.password("hello");
        factory.update(penis);
    }
}