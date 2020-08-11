package com.mvnikitin.issuetracker.dao.repositories;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.backlog.IssueContainer;
import com.mvnikitin.issuetracker.backlog.Sprint;
import com.mvnikitin.issuetracker.configuration.DBConnection;
import com.mvnikitin.issuetracker.configuration.ServerData;
import com.mvnikitin.issuetracker.dao.repositories.dictionaries.Dictionary;
import com.mvnikitin.issuetracker.factory.IssueTrackingFactory;
import com.mvnikitin.issuetracker.filter.Filters;
import com.mvnikitin.issuetracker.issue.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("issue_repo")
@DependsOn("connection")
public class IssueRepository<T, ID> extends BaseRepository<T, ID> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(IssueRepository.class);

    private final static String GET_ISSUE_BY_ID =
            "SELECT i.id, i.issue_type_id, prj.name, i.issue_state_id, " +
                    "i.issue_priority_id, i.sprint_id, i.parent_id, " +
                    "i.assignee_id, i.reporter_id, i.code, i.story_points, " +
                    "i.title, i.description, i.created, i.modified " +
                    "FROM issues i INNER JOIN projects prj " +
                    "ON prj.id = i.project_id WHERE i.id = ?";

    private final static String GET_ALL_ISSUES =
            "SELECT i.id, i.issue_type_id, prj.name, i.issue_state_id, " +
                    "i.issue_priority_id, i.sprint_id, i.parent_id, " +
                    "i.assignee_id, i.reporter_id, i.code, i.story_points, " +
                    "i.title, i.description, i.created, i.modified " +
                    "FROM issues i INNER JOIN projects prj " +
                    "ON prj.id = i.project_id";

    private final static String INSERT_ISSUE =
            "INSERT INTO issues VALUES (NEXTVAL('issues_id_seq'), " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    private final static String UPDATE_ISSUE =
            "UPDATE issues SET issue_type_id = ?, project_id = ?, " +
                    "issue_state_id = ?, issue_priority_id = ?, sprint_id = ?, " +
                    "parent_id = ?, assignee_id = ?, reporter_id = ?, " +
                    "code = ?, story_points = ?, title = ?, description = ?, " +
                    "modified = ? WHERE id = ?";

    private final static String COUNT =
            "SELECT COUNT(*) FROM issues";

    private final static String EXISTS_BY_ID =
            "SELECT id FROM issues WHERE id = ? LIMIT 1";

    private final static String DELETE_ISSUE =
            "DELETE FROM issues WHERE id = ?";


    private final static String GET_PROJECT_ISSUES =
            "SELECT id, issue_type_id, project_id, issue_state_id, " +
                    "issue_priority_id, sprint_id, parent_id, " +
                    "assignee_id, reporter_id, code, story_points, " +
                    "title, description, created, modified "+
                    "FROM issues WHERE project_id = ?";

    private PreparedStatement findAllByProjectStmt;

    private DBConnection connMngr;

    private ServerData serverData;
    private IssueTrackingFactory factory;

    private Dictionary issueTypes;
    private Dictionary issuePriorities;
    private Dictionary issueStates;

    @PostConstruct
    private void init() {
        try {
            con = connMngr.getConnection();

            findByIdStmt = con.prepareStatement(GET_ISSUE_BY_ID);
            findAllStmt = con.prepareStatement(GET_ALL_ISSUES);
            insertStmt = con.prepareStatement(INSERT_ISSUE);
            updateStmt = con.prepareStatement(UPDATE_ISSUE);
            countStmt = con.prepareStatement(COUNT);
            existsStmt = con.prepareStatement(EXISTS_BY_ID);
            deleteStmt = con.prepareStatement(DELETE_ISSUE);

            findAllByProjectStmt = con.prepareStatement(GET_PROJECT_ISSUES);

        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }
    }

    @PreDestroy
    @Override
    public void close() {
        super.close();

        try {
            if (findAllByProjectStmt != null) {
                findAllByProjectStmt.close();
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

    @Autowired
    @Qualifier("types")
    public void setIssueTypes(Dictionary issueTypes) {
        this.issueTypes = issueTypes;
    }

    @Autowired
    @Qualifier("priorities")
    public void setIssuePriorities(Dictionary issuePriorities) {
        this.issuePriorities = issuePriorities;
    }

    @Autowired
    @Qualifier("states")
    public void setIssueStates(Dictionary issueStates) {
        this.issueStates = issueStates;
    }

    @Autowired
    public void setFactory(IssueTrackingFactory factory) {
        this.factory = factory;
    }

    @Override
    public <S extends T> S save(S entity) {

        if (entity == null) {
            throw new IllegalArgumentException();
        }

        if (entity instanceof Issue) {
            Issue issue = (Issue) entity;

            try {
                if (issue.getId() == 0) {

                    setSaveStmtValues(insertStmt, issue);

                    if (insertStmt.execute()) {

                        try (ResultSet rs = insertStmt.getResultSet()) {
                            if (rs.next()) {
                                issue.setId(rs.getInt(1));
                            }
                        }
                    }
                } else {
                    setSaveStmtValues(updateStmt, issue);

                    LocalDateTime modified = LocalDateTime.now();
                    issue.setModified(modified);
                    updateStmt.setTimestamp(13, Timestamp.valueOf(modified));
                    updateStmt.setInt(14, issue.getId());

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

        Issue issue = null;

        try {
            findByIdStmt.setInt(1, (int)primaryKey);

            try (ResultSet rs = findByIdStmt.executeQuery()) {
                while (rs.next()) {

                    Project project = serverData.getProject(rs.getString(3));
                    issue = makeIssueFromRS(rs, project);
                    project.placeAndlinkIssue(issue);
                }
            }

        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }

        return (Optional<T>) Optional.ofNullable(issue);
    }

    @Override
    public Iterable<T> findAll() {
        throw new UnsupportedOperationException(
                "findAll is not supported by IssueRepository class");
    }

    @Override
    public void delete(T entity) {

        if (entity == null) {
            throw new IllegalArgumentException();
        }

        try {
            deleteStmt.setInt(1, ((Issue)entity).getId());
            deleteStmt.executeUpdate();
        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }
    }

    public Iterable<T> findAllByProject(Project project) {

        List<Issue> list = null;

        try{
            findAllByProjectStmt.setInt(1, project.getId());

            try (ResultSet rs = findAllByProjectStmt.executeQuery()) {

                list = new ArrayList<>();

                while (rs.next()) {
                    list.add(makeIssueFromRS(rs, project));
                }
            }
        } catch (SQLException throwables) {
            LOGGER.error("Exception occurred: ", throwables);
        }

        return (Iterable<T>) list;
    }

    private Issue makeIssueFromRS(ResultSet rs, Project project) throws SQLException {

        Issue issue = factory.createIssue(
                issueTypes.getNameById(rs.getInt(2)),
                project);

        issue.setId(rs.getInt(1));
        issue.setState(issueStates.getNameById(rs.getInt(4)));
        issue.setPriority(issuePriorities.getNameById(rs.getInt(5)));
        issue.setSprintId(rs.getInt(6));
        issue.setParentId(rs.getInt(7));
        issue.setAssignee(project.getTeam().getUserById(rs.getInt(8)));
        issue.setReporter(project.getTeam().getUserById(rs.getInt(9)));
        issue.setCode(rs.getString(10));
        issue.setStoryPoints(rs.getInt(11));
        issue.setTitle(rs.getString(12));
        issue.setDescription(rs.getString(13));
        issue.setCreated(rs.getTimestamp(14).toLocalDateTime());
        issue.setModified(rs.getTimestamp(15).toLocalDateTime());

        return issue;
    }

    private void setSaveStmtValues(PreparedStatement ps, Issue issue) throws SQLException {

        ps.setInt(1, issueTypes.getIdByName(issue.getType()));
        ps.setInt(2, issue.getProject().getId());
        ps.setInt(3, issueStates.getIdByName(issue.getState()));
        ps.setInt(4, issuePriorities.getIdByName(issue.getPriority()));

        IssueContainer locatedIn = issue.getLocatedIn();
        if (locatedIn instanceof Sprint) {
            ps.setInt(5, ((Sprint) locatedIn).getId());
        } else {
            // Issue в бэклоге, в спринт не назначена
            ps.setNull(5, Types.INTEGER);
        }

        if (issue.getParent() == null) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, issue.getParent().getId());
        }

        if (issue.getAssignee() == null) {
            ps.setNull(7, Types.INTEGER);
        } else {
            ps.setInt(7, issue.getAssignee().getId());
        }

        if (issue.getReporter() == null) {
            ps.setNull(8, Types.INTEGER);
        } else {
            ps.setInt(8, issue.getReporter().getId());
        }

        ps.setString(9, issue.getCode());
        ps.setInt(10, issue.getStoryPoints());
        ps.setString(11, issue.getTitle());
        ps.setString(12, issue.getDescription());
    }
}
