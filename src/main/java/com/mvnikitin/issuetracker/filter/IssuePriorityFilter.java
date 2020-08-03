package com.mvnikitin.issuetracker.filter;

import com.mvnikitin.issuetracker.issue.Issue;

import java.util.stream.Stream;

public class IssuePriorityFilter extends IssueFilter {

    public IssuePriorityFilter(String filterCode) {
        this.code = filterCode;
    }

    @Override
    public Stream<Issue> filter(Stream<Issue> issues,
                                Object borderMin,
                                Object borderMax,
                                String filterCode,
                                Object[] values) {

        if (filterCode.equals(code)) {

            return issues.filter(i -> {
                if (values != null && values.length != 0) {

                    for (String p : (String[]) values) {
                        if (p.equals(i.getPriority())) {
                            return true;
                        }
                    }
                    return false;

                } else {
                    return true;
                }
            });

        } else if (nextFilter != null) {
            return nextFilter.filter(issues, borderMin, borderMax, filterCode, values);
        }

        return null;
    }
}
