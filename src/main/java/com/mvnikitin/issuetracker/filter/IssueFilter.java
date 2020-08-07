package com.mvnikitin.issuetracker.filter;

import com.mvnikitin.issuetracker.issue.Issue;

import java.util.stream.Stream;

public interface IssueFilter {

    Stream<Issue> filter(Stream<Issue> issues,
                                         Object borderMin,
                                         Object borderMax,
                                         String filterCode,
                                         Object[] values);

    void addNextFilter(IssueFilter filter);

    IssueFilter getNextFilter();

    IssueFilter removeNextFilter();

    String getCriterionCode();
}
