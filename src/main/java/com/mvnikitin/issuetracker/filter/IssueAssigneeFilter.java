package com.mvnikitin.issuetracker.filter;

import com.mvnikitin.issuetracker.issue.Issue;

import java.util.stream.Stream;

public class IssueAssigneeFilter extends IssueFilter {

    public IssueAssigneeFilter(String filterCode) {
        this.code = filterCode;
    }

    @Override
    public Stream<Issue> filter(Stream<Issue> issues,
                                Object borderMin,
                                Object borderMax,
                                String filterCode,
                                Object[] values) {

        if (filterCode.equals(code)) {

            return issues.filter(i ->
                    values == null ? i.getAssignee() == null :
                            i.getAssignee() != null &&
                            i.getAssignee().getUsername() != null &&
                            i.getAssignee().getUsername().equals(values[0]));

        } else if (nextFilter != null) {
            return nextFilter.filter(issues, borderMin, borderMax, filterCode, values);
        }

        return null;
    }
}
