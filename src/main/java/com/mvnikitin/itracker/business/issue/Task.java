package com.mvnikitin.itracker.business.issue;

import com.mvnikitin.itracker.business.Project;
import com.mvnikitin.itracker.business.issuelifecycle.Stateable;

public class Task extends Issue {

    public Task(Stateable workflow, Project project) {
        super("Task", workflow, project);
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

        Issue child = project.getFactory().createIssue("Task", project);

        // Добавляем дочерний элемент в бэклог
        // (альтернативный вариант: locatedIn.add(child);)
        project.getBacklog().add(child);
        linkChild(child, true);

        return child;
    }
}
