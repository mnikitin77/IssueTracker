package com.mvnikitin.issuetracker.issue;

public interface Decomposable {

    boolean isChildAllowed();

    Issue createChild();

    default Issue createChild(String type) {
        return null;
    };
}
