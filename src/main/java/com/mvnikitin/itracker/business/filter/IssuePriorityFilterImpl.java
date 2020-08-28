package com.mvnikitin.itracker.business.filter;

import com.mvnikitin.itracker.business.issue.Issue;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("priority_filter")
public class IssuePriorityFilterImpl extends AbstractIssueFilter {

    public IssuePriorityFilterImpl() {
        code = "priority";
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
