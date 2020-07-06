package me.noaz.testplugin.dao;

import java.util.List;

public interface Dao<T> {

    /**
     * Updates the given object in the database
     *
     * @param t Object to update
     */
    void update(T t/*, String param?*/);

    /**
     * Adds a new object to the Dao.
     * @param t Object to add
     */
    void add(T t);

    /**
     *
     * @param id Id of the object to get
     * @return The object.
     */
    T get(int id);

    //void safeDelete(T t)

    /**
     * Deletes the object from this dao, does not save it.
     * @param t The object to delete
     */
    void delete(T t);

}
