package com.mvnikitin.itracker.factory;

import com.mvnikitin.itracker.business.Project;
import com.mvnikitin.itracker.business.backlog.Backlog;
import com.mvnikitin.itracker.business.backlog.IssueContainer;
import com.mvnikitin.itracker.business.backlog.Sprint;
import com.mvnikitin.itracker.configuration.ServerData;
import com.mvnikitin.itracker.business.exception.NotDefinedWorkflowException;
import com.mvnikitin.itracker.business.issue.*;
import com.mvnikitin.itracker.business.issuelifecycle.Stateable;
import com.mvnikitin.itracker.business.issuelifecycle.Workflow;
import com.mvnikitin.itracker.business.issuelifecycle.WorkflowManager;
import com.mvnikitin.itracker.business.user.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

@Component("factory")
public class IssueTrackingFactoryImpl implements IssueTrackingFactory {

    private final static Map<String, BiFunction<Stateable, Project, Issue>>
        issueFactories = new HashMap<>();

    static {
        issueFactories.put("Epic", (wf, p) -> new Epic(wf, p));
        issueFactories.put("Bug", (wf, p) -> new Bug(wf, p));
        issueFactories.put("Task", (wf, p) -> new Task(wf, p));
        issueFactories.put("Story", (wf, p) -> new Story(wf, p));
    }

    private ServerData serverData;

    @Autowired
    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
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

    private Stateable getWorkflow(String type, Project project) {

        Stateable wf;

        if (project == null || (wf = serverData.getWorkflow(type)) == null) {

            throw new NotDefinedWorkflowException("No workflow defined for " +
                    "the issue type " + type);
        }

        return wf;
    }

    @Lookup
    @Override
    public Project createProject() {
        return null;
    }

    @Override
    public Team createTeam() {
        return new Team();
    }
}
