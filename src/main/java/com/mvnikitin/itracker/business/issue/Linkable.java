package com.mvnikitin.itracker.business.issue;

public interface Linkable {

    void linkChild(Issue child, boolean linkParent);

    void unlinkChild(Issue child, boolean unlinkParent);

    void linkParent(Issue parent, boolean linkChild);

    void unlinkParent(Issue parent, boolean unlinkChild);
}
