package com.mvnikitin.issuetracker.filter;

import com.mvnikitin.issuetracker.issue.Issue;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("title_filter")
public class IssueTitleFilterImpl extends AbstractIssueFilter {

    public IssueTitleFilterImpl() {
        code = "title";
    }

    @Override
    public Stream<Issue> filter(Stream<Issue> issues,
                                Object borderMin,
                                Object borderMax,
                                String filterCode,
                                Object[] values) {

        if (filterCode.equals(code)) {

            return issues.filter(i ->
                    values == null ? i.getTitle() == null :
                            i.getTitle() != null &&
                            i.getTitle().contains((String)values[0]));

        } else if (nextFilter != null) {
            return nextFilter.filter(issues, borderMin, borderMax, filterCode, values);
        }

        return null;
    }
}
