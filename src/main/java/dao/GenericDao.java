package dao;

import java.util.List;

public interface GenericDao<T, ID> {
    T save(T t);

    T get(ID id);

    T update(T t);

    void delete(ID id);

    List<T> getAll();
}