import dao.DbConnector;
import dao.impl.AbstractDao;
import dao.impl.GoodDaoJdbc;
import dao.impl.UserDaoJdbc;
import model.Good;
import model.User;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class AbstractDaoTest {
    AbstractDao<User, Long> daoUser;
    AbstractDao<Good, Long> daoGood;

    @Before
    public void initializeTest() {
        Connection connection = DbConnector.connect();
        daoUser = new UserDaoJdbc(connection);
        daoGood = new GoodDaoJdbc(connection);
    }

    @Test
    public void saveTest() {
        User user = new User("test1", "test1@test.com");
        daoUser.save(user);
        Good good = new Good("test1", "description", 10.00);
        daoGood.save(good);
    }

    @Test
    public void getTest() {
        User user = daoUser.get(12L);
        System.out.println(user);
        Good good = daoGood.get(1L);
        System.out.println(good);
    }

    @Test
    public void UpdateTest() {
        User user = daoUser.get(12L);
        System.out.println(user);
        user.setName("t");
        user = daoUser.update(user);
        System.out.println(user);
        Good good = daoGood.get(6L);
        System.out.println(good);
        good.setName("t");
        daoGood.update(good);
        System.out.println(good);

    }

    @Test
    public void deleteTest() {
        daoUser.delete(12L);
        daoGood.delete(12L);
    }

    @Test
    public void getAllTest() {
        List<User> users = daoUser.getAll();
        List<Good> goods = daoGood.getAll();
        for (User user : users) {
            System.out.println(user);
        }

        for (Good good : goods) {
            System.out.println(good);
        }
    }
}
