package com.mvnikitin.itracker.business.filter;

import com.mvnikitin.itracker.business.issue.Issue;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component("created_filter")
public class IssueCreatedFilterImpl extends AbstractIssueFilter {

    public IssueCreatedFilterImpl() {
        code = "created";
    }

    @Override
    public Stream<Issue> filter(Stream<Issue> issues,
                                Object borderMin,
                                Object borderMax,
                                String filterCode,
                                Object[] values) {

        if (filterCode.equals(code)) {

            if (borderMin == null && borderMax == null) {

                return issues;

            } else if (borderMin != null && borderMax != null) {

                return issues.filter(i ->
                        ((LocalDateTime) borderMin).isBefore(i.getCreated()) &&
                        ((LocalDateTime) borderMax).isAfter(i.getCreated()));
            } else if (borderMin != null) {
                return issues.filter(i ->
                        ((LocalDateTime) borderMin).isBefore(i.getCreated()));
            }

            return issues.filter(i ->
                            ((LocalDateTime) borderMax).isAfter(i.getCreated()));

        } else if (nextFilter != null) {
            return nextFilter.filter(issues, borderMin, borderMax, filterCode, values);
        }

        return null;
    }
}
