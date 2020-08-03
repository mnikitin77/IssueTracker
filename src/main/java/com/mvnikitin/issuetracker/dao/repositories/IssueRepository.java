package com.mvnikitin.issuetracker.dao.repositories;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.backlog.IssueContainer;
import com.mvnikitin.issuetracker.backlog.Sprint;
import com.mvnikitin.issuetracker.configuration.ServerContext;
import com.mvnikitin.issuetracker.issue.Issue;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class IssueRepository<T, ID> extends BaseRepository<T, ID> {

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
                    "modified = DEFAULT WHERE id = ?";

    private final static String COUNT =
            "SELECT COUNT(*) FROM issues";

    private final static String EXISTS_BY_ID =
            "SELECT id FROM issues WHERE id = ? LIMIT 1";

    private final static String DELETE_USER =
            "DELETE FROM issues WHERE id = ?";


    private final static String GET_SPRINT_NAME =
            "SELECT name FROM sprints WHERE id = ?";

    private final static String GET_CHILDREN_IDS =
            "SELECT id FROM issues WHERE parent_id = ?";

    private final static String GET_SPRINT_ISSUES =
            "SELECT id, issue_type_id, project_id, issue_state_id, " +
            "issue_priority_id, sprint_id, parent_id, " +
            "assignee_id, reporter_id, code, story_points, " +
            "title, description, created, modified "+
                    "FROM issues WHERE project_id = ? AND sprint_id = ?";

    private final static String GET_BACKLOG_ISSUES =
            "SELECT id, issue_type_id, project_id, issue_state_id, " +
                    "issue_priority_id, sprint_id, parent_id, " +
                    "assignee_id, reporter_id, code, story_points, " +
                    "title, description, created, modified "+
                    "FROM issues WHERE project_id = ? AND sprint_id IS NULL";


    private PreparedStatement getSprintNameStmt;
    private PreparedStatement getChildrenIdsStmt;
    private PreparedStatement getSprintIssuesStmt;
    private PreparedStatement getBacklogIssuesStmt;

    public IssueRepository(ServerContext ctx) {
        super(ctx);

        try {
            findByIdStmt = con.prepareStatement(GET_ISSUE_BY_ID);
            findAllStmt = con.prepareStatement(GET_ALL_ISSUES);
            insertStmt = con.prepareStatement(INSERT_ISSUE);
            updateStmt = con.prepareStatement(UPDATE_ISSUE);
            countStmt = con.prepareStatement(COUNT);
            existsStmt = con.prepareStatement(EXISTS_BY_ID);
            deleteStmt = con.prepareStatement(DELETE_USER);

            getSprintNameStmt = con.prepareStatement(GET_SPRINT_NAME);
            getChildrenIdsStmt = con.prepareStatement(GET_CHILDREN_IDS);
            getSprintIssuesStmt = con.prepareStatement(GET_SPRINT_ISSUES);
            getBacklogIssuesStmt = con.prepareStatement(GET_BACKLOG_ISSUES);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
                throwables.printStackTrace();
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
        Project project = null;

        try {
            findByIdStmt.setInt(1, (int)primaryKey);

            try (ResultSet rs = findByIdStmt.executeQuery()) {
                while (rs.next()) {

                    project = ctx.getProject(rs.getString(3));

                    issue = makeIssueFromRS(rs, project);

                    int sprintId = rs.getInt(6);

                    // Если issue связана со спринтом, добавляем в спринт,
                    // иначе - в бэклог
                    if (sprintId != 0) {
                        getSprintNameStmt.setInt(1, sprintId);

                        try (ResultSet sprintRS = getSprintNameStmt.executeQuery()) {
                            while (sprintRS.next()) {

                                IssueContainer ic = project.getSprint(
                                        sprintRS.getString(1));

                                if (ic != null) {
                                    ic.add(issue);
                                    issue.setLocatedIn(ic);
                                    break;
                                }
                            }
                        }
                    } else {
                        project.getBacklog().add(issue);
                        issue.setLocatedIn(project.getBacklog());
                    }

                    int parentId = rs.getInt(7);

                    // Проверяем Parent (ищем в памяти и в базе)
                    if (parentId != 0) {
                        issue.linkParent(project.getIssueById(parentId, true), true);
                    }
                }
            }

            // Загружаем дочерние Issues (при привязывании
            //        дочерних к ним будет привязана родительская)
            getChildrenIdsStmt.setInt(1, issue.getId());

            try (ResultSet childrenRS =
                         getChildrenIdsStmt.executeQuery()) {

                linkChildrenIssues(childrenRS, issue, project);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
            throwables.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();

        try {
            if (getSprintNameStmt != null) {
                getSprintNameStmt.close();
            }
            if (getChildrenIdsStmt != null) {
                getChildrenIdsStmt.close();
            }
            if (getSprintIssuesStmt != null) {
                getSprintIssuesStmt.close();
            }
            if (getBacklogIssuesStmt != null) {
                getBacklogIssuesStmt.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadIssueContainer(Project project, IssueContainer sprint) {

        // Очищаем содержимое контейнера
        if (sprint != null) {
            sprint.removeAll();
        } else {
            project.getBacklog().removeAll();
        }

        // Загружаем
        try {
            int sprintId = (sprint == null) ? 0 : ((Sprint)sprint).getId();

            if (sprintId == 0) {
                getBacklogIssuesStmt.setInt(1, project.getId());
                loadContainer(project, getBacklogIssuesStmt, null);
            } else {
                getSprintIssuesStmt.setInt(1, project.getId());
                getSprintIssuesStmt.setInt(2, sprintId);
                loadContainer(project, getSprintIssuesStmt, (Sprint) sprint);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadContainer(Project project, PreparedStatement ps, Sprint sprint) throws SQLException {

        try (ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {

                Issue issue = makeIssueFromRS(rs, project);

                // Добавляем в спринт или в бэклог
                if (sprint != null) {
                    sprint.add(issue);
                    issue.setLocatedIn(sprint);
                } else {
                    project.getBacklog().add(issue);
                    issue.setLocatedIn(project.getBacklog());
                }
            }
        }

        // Загружаем и привязываем дочерние Issues (при привязывании
        // дочерних к ним будет привязана родительская)
        List<Issue> issues = project.getBacklog().getAllIssues();

        for (Issue i: issues) {
            getChildrenIdsStmt.setInt(1, i.getId());
            try (ResultSet childrenRS =
                             getChildrenIdsStmt.executeQuery()) {
                linkChildrenIssues(childrenRS, i, project);
            }
        }
    }

    private Issue makeIssueFromRS(ResultSet rs, Project project) throws SQLException {

        // При создании Issue добавляется автоматически в бэклог
        Issue issue = ctx.getFactory().createIssue(
                ctx.getDictionary("issue_types").getNameById(rs.getInt(2)),
                project);

        issue.setId(rs.getInt(1));
        issue.setState(ctx.getDictionary("issue_states")
                .getNameById(rs.getInt(4)));
        issue.setPriority(ctx.getDictionary("issue_priorities")
                .getNameById(rs.getInt(5)));
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

        ps.setInt(1, ctx.getDictionary("issue_types")
                .getIdByName(issue.getType()));
        ps.setInt(2, issue.getProject().getId());
        ps.setInt(3, ctx.getDictionary("issue_states")
                .getIdByName(issue.getState()));
        ps.setInt(4, ctx.getDictionary("issue_priorities")
                .getIdByName(issue.getPriority()));

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

    private void linkChildrenIssues(ResultSet rs, Issue issue, Project project) throws SQLException {

        while (rs.next()) {
            // TODO проверять на возможное рекурсивное зацикливание
            Issue child = project.getIssueById(rs.getInt(1), true);
            if (child != null) {
                issue.linkChild(child, true);
            }
        }
    }
}
