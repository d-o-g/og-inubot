package me.septron.db.entity.impl;

import me.septron.db.Column;
import me.septron.db.Table;
import me.septron.db.connection.IConnect;
import me.septron.db.connection.impl.SQLConnection;
import me.septron.db.entity.IEntityFactory;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Septron.
 * Date: December 2014
 */
public class SQLFactory<T> implements IEntityFactory<T> {

    private final SQLConnection connection;
    private final Class<T> entity;

    public SQLFactory(SQLConnection connection, Class<T> entity) {
        if (!entity.isAnnotationPresent(Table.class))
            throw new RuntimeException("Not able to find table annotation!");
        this.connection = connection;
        this.entity = entity;
    }

    private String table() {
        return entity.getAnnotation(Table.class).name();
    }

    @Override
    public IConnect connection() {
        return connection;
    }

    @Override
    public T get(String column, String search) throws SQLException {
        return null;
    }

    @Override
    public T update(T entity) throws SQLException {
        return entity;
    }

    @Override
    public boolean put(T entity) throws SQLException, IllegalAccessException {
        Statement statement = this.connection.connection().createStatement();
        String query = "INSERT INTO " + table() + " ";
        String values = " VALUES (";
        String columns = "(";

        int count = 0;
        for (Field field : this.entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                field.setAccessible(true); // TODO: Needed?

                if (!column.increment()) {
                    columns += column.name()  + (count == this.entity.getDeclaredAnnotations().length - 1 ? "" : ", ");
                    values += "'" + field.get(entity)  + "'" + (count == this.entity.getDeclaredFields().length - 1 ? "" : ", ");
                }
            }
            count++;
        }

        columns += ")";
        values += ")";

        statement.execute(query + columns + values);
        return statement.getUpdateCount() > 0;
    }
}