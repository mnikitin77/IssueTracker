package com.mvnikitin.issuetracker.dao.repositories;

import com.mvnikitin.issuetracker.backlog.Sprint;
import com.mvnikitin.issuetracker.configuration.DBConnection;
import com.mvnikitin.issuetracker.configuration.ServerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("sprint_repo")
@DependsOn("connection")
public class SprintRepository <T, ID> extends BaseRepository<T, ID> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(SprintRepository.class);

    private final static String GET_SPRINT_BY_ID =
            "SELECT s.id, s.name, s.capacity, s.start_date, s.end_date, " +
                    "pr.name FROM sprints s INNER JOIN projects pr ON " +
                    "s.project_id = pr.id WHERE s.id = ?";

    private final static String GET_ALL_SPRINTS =
            "SELECT s.id, s.name, s.capacity, s.start_date, s.end_date, " +
                    "pr.name FROM sprints s INNER JOIN projects pr ON " +
                    "s.project_id = pr.id";

    private final static String INSERT_SPRINT =
            "INSERT INTO sprints VALUES (NEXTVAL('sprints_id_seq')," +
                    " ?, ?, ?, ?, ?) RETURNING id";

    private final static String UPDATE_SPRINT =
            "UPDATE sprints SET name = ?, capacity = ?, start_date = ?, " +
                    "end_date = ?, project_id = ? WHERE id = ?";

    private final static String COUNT =
            "SELECT COUNT(*) FROM sprints";

    private final static String EXISTS_BY_ID =
            "SELECT id FROM sprints WHERE id = ? LIMIT 1";

    private final static String DELETE_SPRINT =
            "DELETE FROM sprints WHERE id = ?";


    private final static String GET_ALL_SPRINTS_BY_PROJECT_ID =
            "SELECT s.id, s.name, s.capacity, s.start_date, s.end_date, " +
                    "pr.name FROM sprints s INNER JOIN projects pr ON " +
                    "s.project_id = pr.id WHERE pr.id = ?";

    private PreparedStatement findAllByProjectIdStmt;

    private DBConnection connMngr;
    private ServerData serverData;

    @PostConstruct
    private void init() {
        try {
            con = connMngr.getConnection();

            findByIdStmt = con.prepareStatement(GET_SPRINT_BY_ID);
            findAllStmt = con.prepareStatement(GET_ALL_SPRINTS);
            insertStmt = con.prepareStatement(INSERT_SPRINT);
            updateStmt = con.prepareStatement(UPDATE_SPRINT);
            countStmt = con.prepareStatement(COUNT);
            existsStmt = con.prepareStatement(EXISTS_BY_ID);
            deleteStmt = con.prepareStatement(DELETE_SPRINT);

            findAllByProjectIdStmt =
                    con.prepareStatement(GET_ALL_SPRINTS_BY_PROJECT_ID);

        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }
    }

    @PreDestroy
    @Override
    public void close() {
        super.close();

        try {
            if (findAllByProjectIdStmt != null) {
                findAllByProjectIdStmt.close();
            }
        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }
    }

    @Autowired
    public void setConnMngr(DBConnection connMngr) {
        this.connMngr = connMngr;
    }

    @Autowired
    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
    }

    @Override
    public <S extends T> S save(S entity) {

        if (entity == null) {
            throw new IllegalArgumentException();
        }

        if (entity instanceof Sprint) {
            Sprint sprint = (Sprint) entity;

            try {
                if (sprint.getId() == 0) {

                    setSaveStmtValues(insertStmt, sprint);

                    if (insertStmt.execute()) {

                        try (ResultSet rs = insertStmt.getResultSet()) {
                            if (rs.next()) {
                                sprint.setId(rs.getInt(1));
                            }

                        } catch (SQLException throwables) {
                            LOGGER.error("Exception occurred: ", throwables);
                        }
                    }
                } else {
                    setSaveStmtValues(updateStmt, sprint);

                    updateStmt.setInt(6, sprint.getId());

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

        Sprint sprint = null;

        try {
            findByIdStmt.setInt(1, (int)primaryKey);

            try (ResultSet rs = findByIdStmt.executeQuery()) {
                sprint = makeSprintFromRS(rs);
            }

        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }

        return (Optional<T>) Optional.ofNullable(sprint);
    }

    @Override
    public Iterable<T> findAll() {

        List<Sprint> list = null;

        try (ResultSet rs = findAllStmt.executeQuery()) {

            list = new ArrayList<>();

            while (rs.next()) {
                list.add(makeSprintFromRS(rs));
            }
        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }

        return (Iterable<T>) list;
    }

    public Iterable<T> findAllByProjectId(int id) {

        List<Sprint> list = null;

        try {
            findAllByProjectIdStmt.setInt(1, id);

            try (ResultSet rs = findAllByProjectIdStmt.executeQuery()) {

                list = new ArrayList<>();

                while (rs.next()) {
                    list.add(makeSprintFromRS(rs));
                }
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
            deleteStmt.setInt(1, ((Sprint)entity).getId());
            deleteStmt.executeUpdate();
        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }
    }

    private Sprint makeSprintFromRS(ResultSet rs) throws SQLException {

        Sprint sprint = new Sprint();

        sprint.setId(rs.getInt(1));
        sprint.setName(rs.getString(2));
        sprint.setCapacity(rs.getInt(3));
        sprint.setFrom(rs.getDate(4).toLocalDate());
        sprint.setTo(rs.getDate(5).toLocalDate());
        sprint.setProject(serverData.getProject(rs.getString(6)));

        return sprint;
    }

    private void setSaveStmtValues(PreparedStatement ps, Sprint sprint) throws SQLException {

        ps.setString(1, sprint.getName());
        ps.setInt(2, sprint.getCapacity());
        ps.setDate(3, Date.valueOf(sprint.getFrom()));
        ps.setDate(4, Date.valueOf(sprint.getTo()));
        ps.setInt(5, sprint.getProject().getId());
    }
}
