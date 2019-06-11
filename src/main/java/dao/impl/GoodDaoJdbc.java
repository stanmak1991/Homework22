package dao.impl;

import dao.GoodDao;
import model.Good;

import java.sql.Connection;

public class GoodDaoJdbc extends AbstractDao<Good, Long> implements GoodDao {
    public GoodDaoJdbc(Connection connection) {
        super(connection);
    }
}
