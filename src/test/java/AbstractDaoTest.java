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
        User user = new User("test", "test@test.com");
        daoUser.save(user);
        Good good = new Good("test", "description", 5.00);
        daoGood.save(good);
    }

    @Test
    public void getTest() {
        User user = daoUser.get(2L);
        Good good = daoGood.get(1L);
    }

    @Test
    public void UpdateTest() {
        User user = daoUser.get(6L);
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
        daoUser.delete(1L);
        daoGood.delete(5L);
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
