package com.mvnikitin.itracker.dao.repositories;

import com.mvnikitin.itracker.configuration.DBConnection;
import com.mvnikitin.itracker.dao.repositories.dictionaries.Dictionary;
import com.mvnikitin.itracker.business.issuelifecycle.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.*;

@Component("wf_repo")
@DependsOn("connection")
public class WorkflowRepository<T, ID> extends BaseRepository<T, ID> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(WorkflowRepository.class);

    private final static String GET_WF_TRANSITIONS_BY_ID =
            "SELECT f.name, t.name FROM workflow_transitions wt " +
                    "INNER JOIN issue_states f " +
                    "ON wt.from_state_id = f.id " +
                    "INNER JOIN issue_states t " +
                    "ON wt.to_state_id = t.id " +
                    "WHERE workflow_id = ?";

    private final static String GET_ALL_WF =
            "SELECT id FROM workflows";

    private final static String INSERT_WF =
            "INSERT INTO workflows VALUES " +
                    "(NEXTVAL('workflow_id_seq'), ?, ?, ?, ?) RETURNING id";

    private final static String COUNT =
            "SELECT COUNT(*) FROM workflows";

    private final static String EXISTS_BY_ID =
            "SELECT id FROM workflows WHERE id = ? LIMIT 1";

    // Cascade delete workflow_transitions by FK constraints
    private final static String DELETE_WF =
            "DELETE FROM workflows WHERE id = ?";


    private DBConnection connMngr;

    private Dictionary issueTypes;
    private Dictionary issueStates;

    @PostConstruct
    private void init() {
        try {
            con = connMngr.getConnection();

            findByIdStmt = con.prepareStatement(GET_WF_TRANSITIONS_BY_ID);
            findAllStmt = con.prepareStatement(GET_ALL_WF);
            insertStmt = con.prepareStatement(INSERT_WF);
            countStmt = con.prepareStatement(COUNT);
            existsStmt = con.prepareStatement(EXISTS_BY_ID);
            deleteStmt = con.prepareStatement(DELETE_WF);
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

    @Autowired
    @Qualifier("types")
    public void setIssueTypes(Dictionary issueTypes) {
        this.issueTypes = issueTypes;
    }

    @Autowired
    @Qualifier("states")
    public void setIssueStates(Dictionary issueStates) {
        this.issueStates = issueStates;
    }

    @Override
    public <S extends T> S save(S entity) {

        if (entity == null) {
            throw new IllegalArgumentException();
        }

        if (entity instanceof Workflow) {
            Workflow wf = (Workflow) entity;

            try {
                if (wf.getId() == 0) {

                    insertStmt.setString(1, wf.getName());
                    insertStmt.setString(2, wf.getDescription());
                    insertStmt.setInt(3, issueTypes.getIdByName(wf.getType()));
                    insertStmt.setInt(4, issueStates.getIdByName(wf.getFirst()));

                    if (insertStmt.execute()) {

                        try (ResultSet rs = insertStmt.getResultSet()) {
                            if (rs.next()) {
                                wf.setId(rs.getInt(1));
                            }

                        } catch (SQLException throwables) {
                            LOGGER.error("Exception occurred: ", throwables);
                        }
                    }
                } else {

                    // UPDATE
                    // Проще удалить переходы и записать новые
                    try (Statement stmt = con.createStatement()) {

                        stmt.executeUpdate("DELETE FROM workflow_transitions " +
                                "WHERE workflow_id = " + wf.getId());
                    }
                }

                // Наполняем таблицу переходов workflow_transitions
                // делаем это как для Insert, так и для delete
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO workflow_transitions VALUES (?, ?, ?)")) {

                    ps.setInt(1, wf.getId());

                    Map<String, Set<String>> transitions = wf.getStateTransitions();

                    for (String fromState: transitions.keySet()) {

                        ps.setInt(2, issueStates.getIdByName(fromState));

                        for (String toState: transitions.get(fromState)) {

                            ps.setInt(3, issueStates.getIdByName(toState));
                            ps.executeUpdate();
                        }
                    }
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

        Workflow wf = null;

        try {
            findByIdStmt.setInt(1, (int)primaryKey);

            try (ResultSet rs = findByIdStmt.executeQuery()) {
                wf = makeWFFromRSFromRS(rs, primaryKey);
            }

        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }

        return (Optional<T>) Optional.ofNullable(wf);
    }

    @Override
    public Iterable<T> findAll() {

        List<Workflow> list = null;

        try (ResultSet rs = findAllStmt.executeQuery()) {

            list = new ArrayList<>();

            while (rs.next()) {
                list.add((Workflow) findById((ID)((Integer)rs.getInt(1))).get());
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
            deleteStmt.setInt(1, ((Workflow)entity).getId());
            deleteStmt.executeUpdate();
        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }
    }

    private Workflow makeWFFromRSFromRS(ResultSet transitionsRS, ID primaryKey)
            throws SQLException {

        Workflow wf = null;

        try (Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT w.name, w.description, it.name, iss.name " +
                                "FROM workflows w INNER JOIN issue_types it " +
                                "ON w.issue_type_id = it.id INNER JOIN issue_states iss " +
                                "ON w.first_state_id = iss.id " +
                            "WHERE w.id = " + primaryKey)) {

            while (rs.next()) {
                wf = new Workflow(rs.getString(3), rs.getString(4));
                wf.setName(rs.getString(1));
                wf.setDescription(rs.getString(2));
            }

            wf.setId((int)primaryKey);

            while (transitionsRS.next()) {
                wf.addNextState(transitionsRS.getString(1),
                        transitionsRS.getString(2));
            }

        }

        return wf;
    }
}
