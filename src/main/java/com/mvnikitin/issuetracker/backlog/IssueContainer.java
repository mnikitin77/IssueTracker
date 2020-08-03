package com.mvnikitin.issuetracker.backlog;

import com.mvnikitin.issuetracker.issue.Issue;

import java.util.*;
import java.util.stream.Collectors;

public class IssueContainer {

    protected String name;
    protected Map<Integer, Issue> issues = new HashMap<>();

    public boolean add(Issue issue) {

        if (issue != null) {
            issues.put(issue.getId(), issue);
            issue.setLocatedIn(this);

            return true;
        }

        return false;
    }

    public boolean remove(Issue issue) {

        if (issue != null && issues.remove(issue.getId()) != null) {
            issue.setLocatedIn(null);
            return true;
        }

        return false;
    }

    public boolean move(Issue issue, IssueContainer destination) {

        // Важен порядок!!! destination сначала, так как задача может не влезть !!!
        return issue != null && destination.add(issue) &&
                issues.remove(issue.getId()) != null;
    }

    public boolean isIssueCanAdd(Issue issue) {
        return true;
    }

    public List<Issue> getAllIssues() {
        return issues.values().stream().collect(Collectors.toList());
    }

    public Issue getIssueById(int id) {
        return issues.get(id);
    }

    public int count() {
        return issues.size();
    }

    public void removeAll() {
        issues.clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
