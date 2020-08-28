package com.mvnikitin.itracker.factory;

import com.mvnikitin.itracker.business.Project;
import com.mvnikitin.itracker.business.backlog.IssueContainer;
import com.mvnikitin.itracker.business.issue.Issue;
import com.mvnikitin.itracker.business.issuelifecycle.WorkflowManager;
import com.mvnikitin.itracker.business.user.Team;

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
