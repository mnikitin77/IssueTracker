package com.mvnikitin.itracker.business.filter;

import com.mvnikitin.itracker.business.issue.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Stream;

@Component("issue_filters")
public class Filters {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(Filters.class);

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

        LOGGER.debug("Issue filters are created");
    }

    public Stream<Issue> filter(Stream<Issue> issues,
                                Object borderMin,
                                Object borderMax,
                                String filterCode,
                                Object[] values) {

        return filtersChain.filter(issues, borderMin, borderMax, filterCode, values);
    }
}
