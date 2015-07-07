package db.entity;

import db.connection.IConnect;

/**
 * Created by Septron.
 * Date: December 2014
 */
public interface IEntityFactory<T> {

    IConnect connection();

    T get(String column, String search) throws Exception;
    T update(T entity)throws Exception;
    boolean put(T entity) throws Exception;
}