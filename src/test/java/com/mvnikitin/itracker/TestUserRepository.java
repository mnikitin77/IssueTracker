package com.mvnikitin.itracker;

import com.mvnikitin.itracker.configuration.AppConfig;
import com.mvnikitin.itracker.configuration.DBConnection;
import com.mvnikitin.itracker.dao.repositories.IssueTrackerRepository;
import com.mvnikitin.itracker.business.user.User;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestUserRepository {

    private static User user = new User();

    @Autowired
    private DBConnection connMngr;

    @Autowired
    @Qualifier("user_repo")
    private IssueTrackerRepository userRepo;


    @BeforeClass
    public static void init() {

        user.setFirstName("Порфирий");
        user.setMiddleName("Петрович");
        user.setLastName("Прокофьев");
        user.setUsername("champion");
        user.setPassword("quertyquerty".hashCode());
        user.setPhone("+7 910 123 4567");
        user.setEmail("superporfiry@mail.ru");
        user.setEmployeeCode("100001");
    }

    @Test
    public void test_1_user_insert() {

        boolean testResult = false;

        User inserted = (User)userRepo.save(user);

        inserted.getId();

        try (Statement stmt = connMngr.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users " +
                     "WHERE id=" + inserted.getId())) {
            while (rs.next()) {
                testResult = rs.getInt("id") == inserted.getId() &&
                        rs.getString("first_name").equals(inserted.getFirstName()) &&
                        rs.getString("middle_name").equals(inserted.getMiddleName()) &&
                        rs.getString("last_name").equals(inserted.getLastName()) &&
                        rs.getString("username").equals(inserted.getUsername()) &&
                        rs.getInt("password") == inserted.getPassword() &&
                        rs.getString("phone").equals(inserted.getPhone()) &&
                        rs.getString("email").equals(inserted.getEmail()) &&
                        rs.getString("employee_code").equals(inserted.getEmployeeCode());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(testResult);
    }

    @Test
    public void test_user_findById() {

        int expectedId = -1;

        try (Statement stmt = connMngr.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM users LIMIT 1")) {
            while (rs.next()) {
                expectedId = rs.getInt("id");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        Optional<User> user = (Optional<User>)userRepo.findById(expectedId);

        assertEquals(expectedId, user.isPresent() ? user.get().getId() : -1);
    }

    @Test
    public void test_user_findAll() {

        int expected = -1;

        try (Statement stmt = connMngr.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            while (rs.next()) {
                expected = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        List<User> userList = (List<User>)userRepo.findAll();

        assertEquals(expected, userList.size());
    }

    @Test
    public void test_2_user_update() {

        int expected = 0;

        User toUpdate = getTestUser();

        toUpdate.setPassword("StrongPWD10".hashCode());
        User updated = (User)userRepo.save(toUpdate);

        try (Statement stmt = connMngr.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT password FROM users " +
                     "WHERE id = " + updated.getId())) {
            while (rs.next()) {
                expected = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assertEquals(expected, updated.getPassword());
    }

    @Test
    public void test_3_user_delete() {

        int expected = -1;

        User user = getTestUser();
        int deletedId = user.getId();

        userRepo.delete(getTestUser());

        try (Statement stmt = connMngr.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users " +
                     "WHERE id = " + deletedId)) {
            while (rs.next()) {
                expected = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assertEquals(expected, 0);
    }

    private User getTestUser() {
        User user = new User();

        String queryString = "SELECT * FROM users WHERE " +
                "first_name = '" + TestUserRepository.user.getFirstName() +
                "' AND middle_name = '" +
                TestUserRepository.user.getMiddleName() + "' AND last_name = '" +
                TestUserRepository.user.getLastName() + "' AND username = '" +
                TestUserRepository.user.getUsername() +
                "' AND phone = '" + TestUserRepository.user.getPhone() + "' AND email = '" +
                TestUserRepository.user.getEmail() + "' AND employee_code = '" +
                TestUserRepository.user.getEmployeeCode() + "' LIMIT 1";

        try (Statement stmt = connMngr.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(queryString)) {
            while (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setLastName(rs.getString("last_name"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getInt("password"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setEmployeeCode(rs.getString("employee_code"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return user;
    }
}
