package db.connection;

/**
 * @author Septron
 * @since June 24, 2015
 */
public interface IConnect<T> {

    boolean connect() throws Exception;
    boolean destroy() throws Exception;

    void init() throws Exception;

    T connection();
}