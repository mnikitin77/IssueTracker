package com.mvnikitin.issuetracker.filter;

import com.mvnikitin.issuetracker.issue.Issue;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("reporter_filter")
public class IssueReporterFilterImpl extends AbstractIssueFilter {

    public IssueReporterFilterImpl() {
        code = "reporter";
    }

    @Override
    public Stream<Issue> filter(Stream<Issue> issues,
                                Object borderMin,
                                Object borderMax,
                                String filterCode,
                                Object[] values) {

        if (filterCode.equals(code)) {

            return issues.filter(i ->
                    values == null ? i.getReporter() == null :
                            i.getReporter() != null &&
                            i.getReporter().getUsername() != null &&
                            i.getReporter().getUsername().equals(values[0]));

        } else if (nextFilter != null) {
            return nextFilter.filter(issues, borderMin, borderMax, filterCode, values);
        }

        return null;
    }
}