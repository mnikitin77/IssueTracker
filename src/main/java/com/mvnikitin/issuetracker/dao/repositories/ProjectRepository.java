package com.mvnikitin.issuetracker.dao.repositories;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.user.Team;
import com.mvnikitin.issuetracker.backlog.IssueContainer;
import com.mvnikitin.issuetracker.backlog.Sprint;
import com.mvnikitin.issuetracker.configuration.ServerContext;
import com.mvnikitin.issuetracker.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectRepository<T, ID> extends BaseRepository<T, ID> {

    private final static String GET_PROJECT_BY_ID =
            "SELECT * FROM projects WHERE id = ?";

    private final static String GET_ALL_PROJECTS =
            "SELECT * FROM projects";

    private final static String INSERT_PROJECT =
            "INSERT INTO projects VALUES (NEXTVAL('projects_id_seq')," +
                    " ?, ?, ?, ?, ?) RETURNING id";

    private final static String UPDATE_PROJECT =
            "UPDATE projects SET name = ?, description = ?, biz_unit = ?, " +
                    "admin_id = ?, owner_id = ? WHERE id = ?";

    private final static String COUNT =
            "SELECT COUNT(*) FROM projects";

    private final static String EXISTS_BY_ID =
            "SELECT id FROM projects WHERE id = ? LIMIT 1";

    private final static String DELETE_PROJECT =
            "DELETE FROM projects WHERE id = ?";


    private final static String GET_PROJECT_USERS =
            "SELECT user_id FROM projects_team_members ptm " +
            "INNER JOIN project_teams pt " +
            "ON ptm.projects_team_id = pt.id " +
            "INNER JOIN projects prj " +
            "ON pt.project_id = prj.id " +
            "WHERE prj.id = ?";

    private final static String ADD_PROJECT_USERS =
            "INSERT INTO projects_team_members VALUES (?, ?)";

    private final static String GET_TEAM_INFO =
            "SELECT id, name FROM project_teams WHERE project_id = ?";

    private PreparedStatement getProjectUsers;
    private PreparedStatement addProjectUsers;
    private PreparedStatement getProjectTeamInfo;

    public ProjectRepository(ServerContext ctx) {
        super(ctx);

        try {
            findByIdStmt = con.prepareStatement(GET_PROJECT_BY_ID);
            findAllStmt = con.prepareStatement(GET_ALL_PROJECTS);
            insertStmt = con.prepareStatement(INSERT_PROJECT);
            updateStmt = con.prepareStatement(UPDATE_PROJECT);
            countStmt = con.prepareStatement(COUNT);
            existsStmt = con.prepareStatement(EXISTS_BY_ID);
            deleteStmt = con.prepareStatement(DELETE_PROJECT);

            getProjectUsers = con.prepareStatement(GET_PROJECT_USERS);
            addProjectUsers = con.prepareStatement(ADD_PROJECT_USERS);
            getProjectTeamInfo = con.prepareStatement(GET_TEAM_INFO);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public <S extends T> S save(S entity) {

        if (entity == null) {
            throw new IllegalArgumentException();
        }

        if (entity instanceof Project) {
            Project project = (Project) entity;

            try {

                con.setAutoCommit(false);

                setSaveStmtValues(insertStmt, project);

                if (project.getId() == 0) {

                    insertStmt.execute();

                    try (ResultSet rs = insertStmt.getResultSet()) {
                        if (rs.next()) {
                            project.setId(rs.getInt(1));
                        }
                    }

                    // Добавить команду в базу
                    try (Statement st = con.createStatement()) {
                        if (st.execute("INSERT INTO project_teams " +
                                "VALUES (NEXTVAL('project_teams_id_seq'), " +
                                project.getId() + ", '" +
                                project.getTeam().getName() +
                                "') RETURNING id")) {

                            try (ResultSet rsTeamId = st.getResultSet()) {
                                if (rsTeamId.next()) {
                                    project.getTeam().setId(rsTeamId.getInt(1));
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    }
                } else {

                    updateStmt.setInt(9, project.getId());
                    updateStmt.executeUpdate();

                    // Меняем атрибуты команды
                    try (Statement st = con.createStatement()) {
                        st.executeUpdate("UPDATE project_teams SET name = '" +
                                project.getTeam().getName() + "' WHERE id = " +
                                project.getTeam().getId());
                    }

                    // Удаляем всю команду
                    try (Statement st = con.createStatement()) {
                        st.executeUpdate("DELETE * FROM " +
                                "projects_team_members WHERE " +
                                "projects_team_id = " +
                                project.getTeam().getId());
                    }
                }

                // Добавить участников команды в базу
                insertTeam(project.getTeam());

                // Сохраняем спринты
                BaseRepository sprintsRepo = ctx.getRepository("sprint");

                for (IssueContainer s: project.getSprints()) {
                    sprintsRepo.save(s);
                }

                con.commit();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
                try {
                    con.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return entity;
    }

    @Override
    public Optional<T> findById(ID primaryKey) {

        if (primaryKey == null) {
            throw new IllegalArgumentException();
        }

        Project project = null;

        try {
            findByIdStmt.setInt(1, (int)primaryKey);

            try (ResultSet rs = findByIdStmt.executeQuery()) {
                while (rs.next()) {
                    project = makeProjectFromRS(rs);
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return (Optional<T>) Optional.ofNullable(project);
    }

    @Override
    public Iterable<T> findAll() {

        List<Project> list = null;

        try (ResultSet rs = findAllStmt.executeQuery()) {

            list = new ArrayList<>();

            while (rs.next()) {
                list.add(makeProjectFromRS(rs));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return (Iterable<T>) list;
    }

    @Override
    public void delete(T entity) {

        if (entity == null) {
            throw new IllegalArgumentException();
        }

        try {
            deleteStmt.setInt(1, ((Project)entity).getId());
            deleteStmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();

        try {
            if (getProjectUsers != null) {
                getProjectUsers.close();
            }
            if (addProjectUsers != null) {
                addProjectUsers.close();
            }
            if (getProjectTeamInfo != null) {
                getProjectTeamInfo.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private Project makeProjectFromRS(ResultSet rs) throws SQLException {
        Project project = new Project(ctx);

        project.setId(rs.getInt(1));
        project.setName(rs.getString(2));
        project.setDescription(rs.getString(3));
        project.setBusinessUnit(rs.getString(4));

        project.setAdmin(getUser(rs.getInt(5)));
        project.setOwner(getUser(rs.getInt(6)));

        // Загружаем связанные спринты
        loadSprints(project);

        // Наполняем команду
        populateTeam(project);

        return project;
    }

    private void loadSprints(Project project) {

        SprintRepository sprintsRepo =
                (SprintRepository)ctx.getRepository("sprint");
        List<Sprint> sprints =
                (List<Sprint>)sprintsRepo.findAllByProjectId(project.getId());

        for (Sprint s: sprints) {
            project.addSprint(s);
        }
    }

    private void populateTeam(Project project) throws SQLException {

        Team team = null;
        getProjectUsers.setInt(1, project.getId());
        getProjectTeamInfo.setInt(1, project.getId());

        try (ResultSet rs = getProjectUsers.executeQuery();
             ResultSet teamRs = getProjectTeamInfo.executeQuery()) {

            team = new Team();

            while (rs.next()) {
                team.add(getUser(rs.getInt(1)));
            }

            while (teamRs.next()) {
                team.setName(teamRs.getString(2));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        project.setTeam(team);
    }

    private User getUser(int id) {
        UserRepository userRep = (UserRepository)ctx.getRepository("user");
        return  (User)(userRep.findById(id).orElse(null));
    }

    private void insertTeam(Team team) throws SQLException {
        for (User u: team.getUsers()) {
            addProjectUsers.setInt(1, team.getId());
            addProjectUsers.setInt(2, u.getId());
            addProjectUsers.addBatch();
        }

        addProjectUsers.executeBatch();
    }

    private void setSaveStmtValues(PreparedStatement ps, Project project) throws SQLException {

        ps.setString(1, project.getName());
        ps.setString(2, project.getDescription());
        ps.setString(3, project.getBusinessUnit());
        ps.setInt(4, project.getAdmin().getId());
        ps.setInt(5, project.getOwner().getId());
    }
}
