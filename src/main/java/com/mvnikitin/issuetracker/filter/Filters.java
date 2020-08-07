package com.mvnikitin.issuetracker.filter;

import com.mvnikitin.issuetracker.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Stream;

@Component("issue_filters")
public class Filters {

    private IssueFilter filtersChain;

    @Autowired
    @Qualifier("assignee_filter")
    private IssueFilter assigneeFilter;

    @Autowired
    @Qualifier("created_filter")
    private IssueFilter createdFilter;

    @Autowired
    @Qualifier("priority_filter")
    private IssueFilter priorityFilter;

    @Autowired
    @Qualifier("reporter_filter")
    private IssueFilter reporterFilter;

    @Autowired
    @Qualifier("title_filter")
    private IssueFilter titleFilter;

    @PostConstruct
    private void init() {

        assigneeFilter.addNextFilter(createdFilter);
        createdFilter.addNextFilter(priorityFilter);
        priorityFilter.addNextFilter(reporterFilter);
        reporterFilter.addNextFilter(titleFilter);

        filtersChain = assigneeFilter;
    }

    public Stream<Issue> filter(Stream<Issue> issues,
                                Object borderMin,
                                Object borderMax,
                                String filterCode,
                                Object[] values) {

        return filtersChain.filter(issues, borderMin, borderMax, filterCode, values);
    }
}
