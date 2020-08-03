package com.mvnikitin.issuetracker.factory;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.user.Team;
import com.mvnikitin.issuetracker.backlog.IssueContainer;
import com.mvnikitin.issuetracker.configuration.ServerContext;
import com.mvnikitin.issuetracker.filter.IssueFilter;
import com.mvnikitin.issuetracker.issue.Issue;
import com.mvnikitin.issuetracker.issuelifecycle.WorkflowManager;

import java.time.LocalDate;

public abstract class PMSFactory {

    public abstract WorkflowManager createWorkflow(String type, String first);

    public abstract Issue createIssue(String type, Project project);

    public abstract IssueContainer createBacklog();

    public abstract IssueContainer createSprint(LocalDate from, LocalDate to,
                                                int capacity, Project project);

    public abstract IssueFilter createFilter();

    public abstract Project createProject(ServerContext ctx);

    public abstract Team createTeam();
}
