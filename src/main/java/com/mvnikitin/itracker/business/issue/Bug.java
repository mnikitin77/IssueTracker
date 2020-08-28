package com.mvnikitin.itracker.business.issue;

import com.mvnikitin.itracker.business.Project;
import com.mvnikitin.itracker.business.issuelifecycle.Stateable;

public class Bug extends Issue {

    public Bug(Stateable workflow, Project project) {
    super("Bug", workflow, project);
}

    @Override
    public String getType() {
        return type;
    }

    //--------------       Decomposable       -------------------------
    //
    @Override
    public boolean isChildAllowed() {
        return false;
    }

    @Override
    public Issue createChild() {
        throw new UnsupportedOperationException(
                "Child Issues are not allowed for Bug");
    }
}
