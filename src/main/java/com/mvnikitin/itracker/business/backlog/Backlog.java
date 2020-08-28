package com.mvnikitin.itracker.business.backlog;

public class Backlog extends IssueContainer {

    public Backlog() {
        name = "Backlog";
    }

    @Override
    public String toString() {
        return name;
    }
}
