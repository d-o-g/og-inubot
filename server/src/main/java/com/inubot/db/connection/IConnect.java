package com.inubot.db.connection;

/**
 * @author Septron
 * @since June 24, 2015
 */
public interface IConnect<T> {

    public boolean connect() throws Exception;
    public boolean destroy() throws Exception;

    public void init() throws Exception;

    public T connection();
}