package com.mvnikitin.issuetracker.filter;

import com.mvnikitin.issuetracker.issue.Issue;

import java.util.stream.Stream;

public abstract class IssueFilter {

    protected String code;
    protected IssueFilter nextFilter;

    public abstract Stream<Issue> filter(Stream<Issue> issues,
                                         Object borderMin,
                                         Object borderMax,
                                         String filterCode,
                                         Object[] values);

    public void addNextFilter(IssueFilter filter) {
        nextFilter = filter;
    }

    public IssueFilter getNextFilter() {
        return nextFilter;
    }

    public IssueFilter removeNextFilter() {
        IssueFilter removedtFilter = nextFilter;

        if (nextFilter != null) {
            nextFilter = nextFilter.getNextFilter();
        }

        return removedtFilter;
    }

    public String getCriterionCode() {
        return code;
    }
}
