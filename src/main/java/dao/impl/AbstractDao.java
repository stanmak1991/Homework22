package dao.impl;

import dao.GenericDao;

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

    protected AbstractDao(Connection connection) {
        this.connection = connection;
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public T save(T t) {
        Field[] fields = t.getClass().getDeclaredFields();
        StringBuilder query = new StringBuilder("INSERT INTO " + t.getClass().getSimpleName() + "(");
        StringBuilder valuesQuery = new StringBuilder("VALUES(");
        try {
            for (Field f : fields) {
                f.setAccessible(true);
                Object value = f.get(t);
                if (value == null) {
                    continue;
                } else {
                    if (query.charAt(query.length() - 1) != '(') {
                        query.append(", ");
                        valuesQuery.append(", ");
                    }
                    query.append(f.getName());
                    if (value.getClass().equals(String.class)) {
                        valuesQuery.append('\'');
                        valuesQuery.append(value);
                        valuesQuery.append('\'');
                    } else {
                        valuesQuery.append(value);
                    }
                }
            }
            query.append(") ");
            valuesQuery.append(");");
            query.append(valuesQuery);
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public T get(ID id) {
        T entity = null;
        String query = "SELECT * FROM " + persistentClass.getSimpleName() + " WHERE ID=" + id;
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
        StringBuilder query = new StringBuilder("UPDATE " + t.getClass().getSimpleName() + " SET ");
        Field[] fields = persistentClass.getDeclaredFields();
        try {
            for (int counter = 1; counter < fields.length; counter++) {
                fields[counter].setAccessible(true);
                query.append(fields[counter].getName());
                query.append("=");
                if (fields[counter].getType().equals(String.class)) {
                    query.append('\'');
                    query.append(fields[counter].get(t));
                    query.append('\'');
                } else {
                    query.append(fields[counter].get(t));
                }
                if (counter < fields.length - 1) {
                    query.append(", ");
                } else {
                    query.append(" ");
                }
            }
            query.append(" WHERE ID=");
            fields[0].setAccessible(true);
            query.append(fields[0].get(t));
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public void delete(ID id) {
        String query = "DELETE FROM " + persistentClass.getSimpleName() + " WHERE ID=" + id;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<T> getAll() {
        List<T> allEntities = new ArrayList<>();
        String query = "SELECT * FROM " + persistentClass.getSimpleName();
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