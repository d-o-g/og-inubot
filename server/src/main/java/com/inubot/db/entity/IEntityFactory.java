package com.inubot.db.entity;

import com.inubot.db.connection.IConnect;

/**
 * Created by Septron.
 * Date: December 2014
 */
public interface IEntityFactory<T> {

    public IConnect connection();

    public T get(String column, String search) throws Exception;
    public T update(T entity)throws Exception;
    public boolean put(T entity) throws Exception;
}