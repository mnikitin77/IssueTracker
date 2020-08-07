package com.mvnikitin.issuetracker.factory;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.backlog.IssueContainer;
import com.mvnikitin.issuetracker.issue.Issue;
import com.mvnikitin.issuetracker.issuelifecycle.WorkflowManager;
import com.mvnikitin.issuetracker.user.Team;

import java.time.LocalDate;

public interface IssueTrackingFactory {

    WorkflowManager createWorkflow(String type, String first);

    Issue createIssue(String type, Project project);

    IssueContainer createBacklog();

    IssueContainer createSprint(LocalDate from, LocalDate to,
                                                int capacity, Project project);

    Project createProject();

    Team createTeam();
}
