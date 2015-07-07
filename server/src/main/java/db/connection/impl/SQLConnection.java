package db.connection.impl;

import db.connection.IConnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Septron
 * @since June 24, 2015
 */
public class SQLConnection implements IConnect<Connection> {

    private final String database;
    private final String username;
    private final String password;

    private Connection connection;

    public SQLConnection(String database, String username, String password) {
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean connect() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:mysql://" + database, username, password);
        return !this.connection.isClosed();
    }

    @Override
    public boolean destroy() throws SQLException {
        if (!connection.isClosed())
            connection.close();
        return connection.isClosed();
    }

    @Override
    public void init() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
    }

    public Connection connection() { return connection; }
}