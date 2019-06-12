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

    protected AbstractDao(Connection connection) {
        this.connection = connection;
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public T save(T t) {
        String query = createQuery("save", t);
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
        String query = createQuery("get", id);
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
        String query = createQuery("update", t);
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
        String query = createQuery("delete", id);
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

    private String createQuery(String action, Object object) {
        String table = persistentClass.getAnnotation(Table.class).name();
        Field[] fields;
        String query;
        StringBuilder temp;
        switch (action) {
            case "save":
                temp = new StringBuilder("INSERT INTO " + table + "(");
                StringBuilder valuesQuery = new StringBuilder("VALUES(");
                fields = object.getClass().getDeclaredFields();
                for (Field f : fields) {
                    f.setAccessible(true);
                    Object value = null;
                    try {
                        value = f.get(object);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (value == null) {
                        continue;
                    } else {
                        if (temp.charAt(temp.length() - 1) != '(') {
                            temp.append(", ");
                            valuesQuery.append(", ");
                        }
                        temp.append(f.getName());
                        if (value.getClass().equals(String.class)) {
                            valuesQuery.append('\'');
                            valuesQuery.append(value);
                            valuesQuery.append('\'');
                        } else {
                            valuesQuery.append(value);
                        }
                    }
                }
                temp.append(") ");
                valuesQuery.append(");");
                temp.append(valuesQuery);
                query = temp.toString();
                break;
            case "get":
                query = "SELECT * FROM " + table + " WHERE ID=" + object;
                break;
            case "update":
                fields = persistentClass.getDeclaredFields();
                temp = new StringBuilder("UPDATE " + table + " SET ");
                for (int counter = 1; counter < fields.length; counter++) {
                    fields[counter].setAccessible(true);
                    temp.append(fields[counter].getName());
                    temp.append("=");
                    try {
                        if (fields[counter].getType().equals(String.class)) {
                            temp.append('\'');
                            temp.append(fields[counter].get(object));
                            temp.append('\'');
                        } else {
                            temp.append(fields[counter].get(object));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (counter < fields.length - 1) {
                        temp.append(", ");
                    } else {
                        temp.append(" ");
                    }
                }
                temp.append(" WHERE ID=");
                fields[0].setAccessible(true);
                try {
                    temp.append(fields[0].get(object));
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                query = temp.toString();
                break;
            case "delete":
                query = "DELETE FROM " + table + " WHERE ID=" + object;
                break;
            default:
                query = null;

        }
        return query;
    }
}