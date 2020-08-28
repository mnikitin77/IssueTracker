package com.mvnikitin.itracker.business.filter;

public abstract class AbstractIssueFilter implements IssueFilter {

    protected String code;
    protected IssueFilter nextFilter;

    public void addNextFilter(IssueFilter filter) {
        nextFilter = filter;
    }

    public IssueFilter getNextFilter() {
        return nextFilter;
    }

    public IssueFilter removeNextFilter() {
        IssueFilter removedtFilter = nextFilter;

        if (nextFilter != null) {
            nextFilter = nextFilter.getNextFilter();
        }

        return removedtFilter;
    }

    public String getCriterionCode() {
        return code;
    }
}
