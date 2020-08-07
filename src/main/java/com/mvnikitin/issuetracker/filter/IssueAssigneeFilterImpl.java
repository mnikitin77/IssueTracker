package com.mvnikitin.issuetracker.filter;

import com.mvnikitin.issuetracker.issue.Issue;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("assignee_filter")
public class IssueAssigneeFilterImpl extends AbstractIssueFilter {

    public IssueAssigneeFilterImpl() {
        code = "assignee";
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
