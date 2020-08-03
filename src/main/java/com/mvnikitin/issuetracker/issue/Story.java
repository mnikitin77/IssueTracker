package com.mvnikitin.issuetracker.issue;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.issuelifecycle.Stateable;

public class Story extends Issue {

    public Story(Stateable workflow, Project project) {
        super("Story", workflow, project);
    }

    @Override
    public String getType() {
        return type;
    }

    //--------------       Decomposable       -------------------------
    //
    @Override
    public boolean isChildAllowed() {
        return true;
    }

    @Override
    public Issue createChild() {

        Issue child = project.getContext().getFactory().createIssue("Task", project);

        // Добавляем дочерний элемент в бэклог
        // (альтернативный вариант: locatedIn.add(child);)
        project.getBacklog().add(child);
        linkChild(child, true);

        return child;
    }
}
