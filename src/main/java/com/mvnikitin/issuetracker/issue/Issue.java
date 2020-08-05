package com.mvnikitin.issuetracker.issue;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.backlog.IssueContainer;
import com.mvnikitin.issuetracker.issuelifecycle.Stateable;
import com.mvnikitin.issuetracker.user.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Issue implements Linkable, Decomposable {

    protected int id;
    protected Project project;
    protected String type;
    protected Stateable workflow;
    protected String state;

    protected String priority;
    protected LocalDateTime created;
    protected LocalDateTime modified;
    protected String code;
    protected String title;
    protected String description;
    protected User assignee;
    protected User reporter;

    protected IssueContainer locatedIn;
    protected int sprintId;

    protected int storyPoints;

    protected Issue parent;
    protected int parentId;
    protected Map<Integer, Issue> children;


    public Issue(String type, Stateable workflow, Project project) {

        this.type = type;
        this.project = project;
        this.workflow = workflow;
        state = workflow.getInitialState();

        priority = "Normal";
        created = LocalDateTime.now();
    }

    //--------------         Linkable         -------------------------
    //
    @Override
    public void linkChild(Issue child, boolean linkParent) {

        if (child != null) {
            if (children == null) {
                children = new HashMap<>();
            }

            children.put(child.getId(), child);
            if (linkParent) {
                child.linkParent(this, false);
            }
        }
    }

    @Override
    public void unlinkChild(Issue child, boolean unlinkParent) {

        if (child != null && children != null &&
//                children.remove(child)) {
                children.remove(child.getId()) != null) {

            if (children.isEmpty()) {
                children = null;
            }

            if (unlinkParent) {
                child.unlinkParent(this, false);
            }
        }
    }

    @Override
    public void linkParent(Issue parent, boolean linkChild) {

        this.parent = parent;

        if (parent != null && linkChild) {
            parent.linkChild(this, false);
        }
    }

    @Override
    public void unlinkParent(Issue parent, boolean unlinkChild) {
        this.parent = null;

        if (unlinkChild) {
            parent.unlinkChild(this, false);
        }
    }

    // --

    public Issue getParent() {
        return parent;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<Issue> getChildren() {
        return children.values().stream().collect(Collectors.toList());
    }

    public void changeState(String newState) {
        state = workflow.changeState(state, newState);
    }

    public abstract String getType();

    public void setType(String type) {
        this.type = type;
    }

    public IssueContainer getLocatedIn() {
        return locatedIn;
    }

    public void setLocatedIn(IssueContainer locatedIn) {
        this.locatedIn = locatedIn;
    }

    public int getSprintId() {
        return sprintId;
    }

    public void setSprintId(int sprintId) {
        this.sprintId = sprintId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getAssignee() {
    return assignee;
}

    public void setAssignee(User assignee) {
    this.assignee = assignee;
}

    public User getReporter() {
    return reporter;
}

    public void setReporter(User reporter) {
    this.reporter = reporter;
}

    public Stateable getWorkflow() {
        return workflow;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = storyPoints;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void clear() {

        for (Issue i: children.values()) {
            i.unlinkParent(null, false);
        }

        parent = null;
        locatedIn = null;
        workflow = null;
        assignee = null;
        reporter = null;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", project=" + project +
                ", type=" + type +
                ", state=" + state +
                ", priority=" + priority +
                ", created=" + created +
                ", title='" + title + '\'' +
                ", assignee=" + assignee +
                ", reporter=" + reporter +
                ", locatedIn=" + locatedIn +
                ", storyPoints=" + storyPoints +
                ", parentId=" + (parent != null ? parent.getId() : parent) +
                ", children=" + (children != null ? children.size() : children) +
                "\n, description=" + description +
                '}';
    }
}
