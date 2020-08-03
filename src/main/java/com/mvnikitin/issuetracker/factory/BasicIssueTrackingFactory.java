package com.mvnikitin.issuetracker.factory;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.user.Team;
import com.mvnikitin.issuetracker.backlog.Backlog;
import com.mvnikitin.issuetracker.backlog.IssueContainer;
import com.mvnikitin.issuetracker.backlog.Sprint;
import com.mvnikitin.issuetracker.configuration.ServerContext;
import com.mvnikitin.issuetracker.exception.NotDefinedWorkflowException;
import com.mvnikitin.issuetracker.filter.*;
import com.mvnikitin.issuetracker.issue.*;
import com.mvnikitin.issuetracker.issuelifecycle.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class BasicIssueTrackingFactory extends PMSFactory {

    private final static Map<String, BiFunction<Stateable, Project, Issue>>
        issueFactories = new HashMap<>();

    static {
        issueFactories.put("Epic", (wf, p) -> new Epic(wf, p));
        issueFactories.put("Bug", (wf, p) -> new Bug(wf, p));
        issueFactories.put("Task", (wf, p) -> new Task(wf, p));
        issueFactories.put("Story", (wf, p) -> new Story(wf, p));
    }

    private final static PMSFactory instance = new BasicIssueTrackingFactory();

    private BasicIssueTrackingFactory() { };

    public static PMSFactory getFactory() {
        return instance;
    }

    @Override
    public WorkflowManager createWorkflow(String type, String first) {
        return new Workflow(type, first);
    }

    @Override
    public Issue createIssue(String type, Project project) {
        Objects.requireNonNull(project, "Project must be provided");

        Issue newIssue = issueFactories.get(type)
                .apply(getWorkflow(type, project), project);

        return newIssue;
    }

    @Override
    public IssueContainer createBacklog() {
        return new Backlog();
    }

    @Override
    public IssueContainer createSprint(LocalDate from, LocalDate to,
                                       int capacity, Project project) {
        return new Sprint(from, to, capacity, project);
    }

    @Override
    public IssueFilter createFilter() {

        IssueFilter filter = new IssueAssigneeFilter("assignee");
        IssueFilter retval = filter;

        filter.addNextFilter(new IssueCreatedFilter("created"));
        filter = filter.getNextFilter();

        filter.addNextFilter(new IssuePriorityFilter("priority"));
        filter = filter.getNextFilter();

        filter.addNextFilter(new IssueReporterFilter("reporter"));
        filter = filter.getNextFilter();

        filter.addNextFilter(new IssueTitleFilter("title"));

        return retval;
    }

    private Stateable getWorkflow(String type, Project project) {
        Stateable wf;

        if (project == null ||
                (wf = project.getContext().getWorkflow(type)) == null) {

            throw new NotDefinedWorkflowException("No workflow defined for " +
                    "the issue type " + type);
        }

        return wf;
    }

    @Override
    public Project createProject(ServerContext ctx) {
        return new Project(ctx);
    }

    @Override
    public Team createTeam() {
        return new Team();
    }
}
