package com.mvnikitin.issuetracker.backlog;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.issue.Issue;

import java.time.LocalDate;

public class Sprint extends IssueContainer {

    private int id;
    private LocalDate from;
    private LocalDate to;
    private int capacity;
    private int storyPointsAllocated;
    Project project;

    public Sprint(){};

    public Sprint(LocalDate from, LocalDate to, int capacity, Project project) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.project = project;
    }

    public void empty() {

        Object[] array = issues.values().toArray();
        for (int i = 0; i < array.length; i++) {
            move((Issue)array[i], ((Issue)array[i]).getProject().getBacklog());
        }

        storyPointsAllocated = 0;
    }

    @Override
    public boolean add(Issue issue) {

        if (issue != null && isIssueCanAdd(issue) && super.add(issue)) {

            storyPointsAllocated += issue.getStoryPoints();
            return true;
        }

        return false;
    }

    @Override
    public boolean remove(Issue issue) {

        if (issue != null &&
                super.move(issue, issue.getProject().getBacklog())) {

            storyPointsAllocated -= issue.getStoryPoints();
            return true;
        }

        return false;
    }

    @Override
    public boolean move(Issue issue, IssueContainer destination) {
        if (issue != null && destination != null &&
                destination.isIssueCanAdd(issue) &&
                super.move(issue, destination)) {

            storyPointsAllocated -= issue.getStoryPoints();
            // в destination storyPoints добавятся в методе add().

            return true;
        }

        return false;
    }

    @Override
    public boolean isIssueCanAdd(Issue issue) {
        return issue.getStoryPoints() + storyPointsAllocated <= capacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getStoryPointsAllocated() {
        return storyPointsAllocated;
    }
}
