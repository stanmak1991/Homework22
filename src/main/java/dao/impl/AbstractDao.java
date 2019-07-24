package dao.impl;

import dao.GenericDao;

import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T, ID> implements GenericDao<T, ID> {

    protected final Connection connection;
    private final Class<T> persistentClass;
    private final QueryEditor queryEditor;

    protected AbstractDao(Connection connection) {
        this.connection = connection;
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        this.queryEditor = new QueryEditor(persistentClass);
    }

    @Override
    public T save(T t) {
        String query = queryEditor.createQuery("save", t);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public T get(ID id) {
        T entity = null;
        String query = queryEditor.createQuery("get", id);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                entity = persistentClass.newInstance();
                Field[] fields = persistentClass.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    field.set(entity, resultSet.getObject(field.getName()));
                }
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public T update(T t) {
        String query = queryEditor.createQuery("update", t);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public void delete(ID id) {
        String query = queryEditor.createQuery("delete", id);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<T> getAll() {
        String table = persistentClass.getAnnotation(Table.class).name();
        List<T> allEntities = new ArrayList<>();
        String query = "SELECT * FROM " + table;
        Field[] fields = persistentClass.getDeclaredFields();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                T entity = persistentClass.newInstance();
                for (Field field : fields) {
                    field.setAccessible(true);
                    field.set(entity, resultSet.getObject(field.getName()));
                }
                allEntities.add(entity);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return allEntities;
    }
}