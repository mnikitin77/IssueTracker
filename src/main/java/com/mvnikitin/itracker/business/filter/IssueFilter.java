package com.mvnikitin.itracker.business.filter;

import com.mvnikitin.itracker.business.issue.Issue;

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
