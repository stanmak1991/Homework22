package dao.impl;

import javax.persistence.Table;
import java.lang.reflect.Field;

public class QueryEditor<T> {
    private final Class<T> persistentClass;

    public QueryEditor(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public String createQuery(String action, Object object) {
        switch (action) {
            case "save":
                return saveQuery(object);
            case "get":
                return getQuery(object);
            case "update":
                return updateQuery(object);
            case "delete":
                return deleteQuery(object);
        }
        return null;
    }

    private String saveQuery(Object object) {
        String table = persistentClass.getAnnotation(Table.class).name();
        StringBuilder temp = new StringBuilder("INSERT INTO " + table + "(");
        StringBuilder valuesQuery = new StringBuilder("VALUES(");
        Field[] fields = object.getClass().getDeclaredFields();
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
        return temp.toString();
    }

    private String getQuery(Object object) {
        String table = persistentClass.getAnnotation(Table.class).name();
        return "SELECT * FROM " + table + " WHERE ID=" + object;
    }

    private String updateQuery(Object object) {
        String table = persistentClass.getAnnotation(Table.class).name();
        Field[] fields = persistentClass.getDeclaredFields();
        StringBuilder temp = new StringBuilder("UPDATE " + table + " SET ");
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
        return temp.toString();
    }

    private String deleteQuery(Object object) {
        String table = persistentClass.getAnnotation(Table.class).name();
        return "DELETE FROM " + table + " WHERE ID=" + object;
    }
}
