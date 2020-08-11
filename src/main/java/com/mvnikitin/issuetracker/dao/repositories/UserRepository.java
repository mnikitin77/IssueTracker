package com.mvnikitin.issuetracker.dao.repositories;

import com.mvnikitin.issuetracker.configuration.DBConnection;
import com.mvnikitin.issuetracker.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("user_repo")
@DependsOn("connection")
public class UserRepository <T, ID> extends BaseRepository<T, ID> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(UserRepository.class);

    private final static String GET_USER_BY_ID =
            "SELECT * FROM users WHERE id = ?";

    private final static String GET_ALL_USERS =
            "SELECT * FROM users";

    private final static String INSERT_USER =
            "INSERT INTO users VALUES(NEXTVAL('users_id_seq')," +
                    " ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    private final static String UPDATE_USER =
            "UPDATE users SET first_name = ?, middle_name = ?, last_name = ?, " +
                    "username = ?, password = ?, phone = ?, email = ?, " +
                    "employee_code = ? WHERE id = ?";

    private final static String COUNT =
            "SELECT COUNT(*) FROM users";

    private final static String EXISTS_BY_ID =
            "SELECT id FROM users WHERE id = ? LIMIT 1";

    private final static String DELETE_USER =
            "DELETE FROM users WHERE id = ?";

    private DBConnection connMngr;

    @PostConstruct
    private void init() {
        try {
            con = connMngr.getConnection();

            findByIdStmt = con.prepareStatement(GET_USER_BY_ID);
            findAllStmt = con.prepareStatement(GET_ALL_USERS);
            insertStmt = con.prepareStatement(INSERT_USER);
            updateStmt = con.prepareStatement(UPDATE_USER);
            countStmt = con.prepareStatement(COUNT);
            existsStmt = con.prepareStatement(EXISTS_BY_ID);
            deleteStmt = con.prepareStatement(DELETE_USER);
        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }
    }

    @PreDestroy
    @Override
    public void close() {
        super.close();
    }

    @Autowired
    public void setConnMngr(DBConnection connMngr) {
        this.connMngr = connMngr;
    }

    @Override
    public <S extends T> S save(S entity) {

        if (entity == null) {
            throw new IllegalArgumentException();
        }

        if (entity instanceof User) {
            User user = (User) entity;

            try {
                if (user.getId() == 0) {

                    setSaveStmtValues(insertStmt, user);

                    if (insertStmt.execute()) {

                        try (ResultSet rs = insertStmt.getResultSet()) {
                            if (rs.next()) {
                                user.setId(rs.getInt(1));
                            }

                        } catch (SQLException throwables) {
                            LOGGER.error("Exception occurred: ", throwables);
                        }
                    }
                } else {

                    setSaveStmtValues(updateStmt, user);

                    updateStmt.setInt(9, user.getId());

                    updateStmt.executeUpdate();
                }
            } catch (SQLException throwables) {
                LOGGER.error("Exception occurred: ", throwables);
            }
        }

        return entity;
    }

    @Override
    public Optional<T> findById(ID primaryKey) {

        if (primaryKey == null) {
            throw new IllegalArgumentException();
        }

        User user = null;

        try {
            findByIdStmt.setInt(1, (int)primaryKey);

            try (ResultSet rs = findByIdStmt.executeQuery()) {
                while (rs.next()) {
                    user = makeUserFromRS(rs);
                }
            }

        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }

        return (Optional<T>) Optional.ofNullable(user);
    }

    @Override
    public Iterable<T> findAll() {

        List<User> list = null;

        try (ResultSet rs = findAllStmt.executeQuery()) {

            list = new ArrayList<>();

            while (rs.next()) {
                list.add(makeUserFromRS(rs));
            }
        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }

        return (Iterable<T>) list;
    }

    @Override
    public void delete(T entity) {

        if (entity == null) {
            throw new IllegalArgumentException();
        }

        try {
            deleteStmt.setInt(1, ((User)entity).getId());
            deleteStmt.executeUpdate();
        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }
    }

    private User makeUserFromRS(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getInt(1));
        user.setFirstName(rs.getString(2));
        user.setMiddleName(rs.getString(3));
        user.setLastName(rs.getString(4));
        user.setUsername(rs.getString(5));
        user.setPassword(rs.getInt(6));
        user.setPhone(rs.getString(7));
        user.setEmail(rs.getString(8));
        user.setEmployeeCode(rs.getString(9));

        return user;
    }

    private void setSaveStmtValues(PreparedStatement ps, User user) throws SQLException {

        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getMiddleName());
        ps.setString(3, user.getLastName());
        ps.setString(4, user.getUsername());
        ps.setInt(5, user.getPassword());
        ps.setString(6, user.getPhone());
        ps.setString(7, user.getEmail());
        ps.setString(8, user.getEmployeeCode());
    }
}
