package dao.impl;

import model.User;

import java.sql.Connection;

public class UserDaoJdbc extends AbstractDao<User, Long> {

    public UserDaoJdbc(Connection connection) {
        super(connection);
    }
}
