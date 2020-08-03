package com.mvnikitin.issuetracker.configuration;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.backlog.Sprint;
import com.mvnikitin.issuetracker.dao.repositories.*;
import com.mvnikitin.issuetracker.dao.repositories.dictionaries.Dictionary;
import com.mvnikitin.issuetracker.dao.repositories.dictionaries.IssuePriorities;
import com.mvnikitin.issuetracker.dao.repositories.dictionaries.IssueStates;
import com.mvnikitin.issuetracker.dao.repositories.dictionaries.IssueTypes;
import com.mvnikitin.issuetracker.factory.BasicIssueTrackingFactory;
import com.mvnikitin.issuetracker.factory.PMSFactory;
import com.mvnikitin.issuetracker.issue.Issue;
import com.mvnikitin.issuetracker.issuelifecycle.Stateable;
import com.mvnikitin.issuetracker.issuelifecycle.Workflow;
import com.mvnikitin.issuetracker.user.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerContext {

    private Map<String, BaseRepository<?, ?>> repos = new HashMap<>();
    private Map<String, Dictionary> dictionaries = new HashMap<>();
    private Map<String, Stateable> workflows = new HashMap<>();
    private Map<String, Project> projects = new HashMap<>();

    private Connection connection;
    private String connectionString;

    public ServerContext(String connectionString) {

        this.connectionString = connectionString;

        try {
            Class.forName( "org.postgresql.Driver" );
            connection = DriverManager.getConnection(connectionString);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        // Initialize dictionaries
        dictionaries.put("issue_states", new IssueStates(connection));
        dictionaries.put("issue_types", new IssueTypes(connection));
        dictionaries.put("issue_priorities", new IssuePriorities(connection));

        // Initialize repositories
        repos.put("user", new UserRepository<User, Integer>(this));
        repos.put("workflow", new WorkflowRepository<Workflow, Integer>(this));
        repos.put("project", new ProjectRepository<Project, Integer>(this));
        repos.put("issue", new IssueRepository<Issue, Integer>(this));
        repos.put("sprint", new SprintRepository<Sprint, Integer>(this));

        // Initialize WFs
        List<Workflow> wfsList = (List<Workflow>) repos.get("workflow").findAll();
        for (Workflow w: wfsList) {
            workflows.put(w.getType(), w);
        }

        // Load projects
        List<Project> prjList = (List<Project>) repos.get("project").findAll();
        for (Project p: prjList) {
            projects.put(p.getName(), p);
        }
    }

    public Connection getConnection() {
        try {
            if (connection.isValid(1)) {
                connection = DriverManager.getConnection(connectionString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void close() {
        try {
            for (BaseRepository<?, ?> rep: repos.values()) {
                rep.close();
            }
            connection.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BaseRepository<?, ?> getRepository(String repoCode) {
        return repos.get(repoCode);
    }

    public Dictionary getDictionary(String dictCode) {
        return  dictCode != null ? dictionaries.get(dictCode) : null;
    }

    public PMSFactory getFactory() {
        return BasicIssueTrackingFactory.getFactory();
    }

    public Stateable getWorkflow(String type) {
        return type != null ? workflows.get(type) : null;
    }

    public Project getProject(String name) {
        return name != null ? projects.get(name) : null;
    }
}
