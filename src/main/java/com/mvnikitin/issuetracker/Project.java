package com.mvnikitin.issuetracker;

import com.mvnikitin.issuetracker.backlog.IssueContainer;
import com.mvnikitin.issuetracker.backlog.Sprint;
import com.mvnikitin.issuetracker.configuration.ServerContext;
import com.mvnikitin.issuetracker.dao.repositories.BaseRepository;
import com.mvnikitin.issuetracker.dao.repositories.IssueRepository;
import com.mvnikitin.issuetracker.issue.Issue;
import com.mvnikitin.issuetracker.user.Team;
import com.mvnikitin.issuetracker.user.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Project {

    private int id;
    private Team team;
    private ServerContext ctx;

    private String name;
    private String description;
    private String businessUnit;
    private User owner;
    private User admin;

    // All project issues
    private Map<Integer, Issue> issueBag = new HashMap<>();
    // Backlog
    private IssueContainer backlog;
    // Sprints
    private Map<Integer, IssueContainer> sprints = new HashMap<>();

    private BaseRepository issuesRepo;
    private BaseRepository sprintsRepo;

    public Project(ServerContext ctx) {
        this.ctx = ctx;
        backlog = ctx.getFactory().createBacklog();
        issuesRepo = ctx.getRepository("issue");
        sprintsRepo = ctx.getRepository("sprint");
    }

    // Очистить конейнеры, загрузить issues из базы,
    // связать и добавить в контейнеры
    public void reloadIssues() {

        resetContainers();

        List<Issue> issues = (List<Issue>)((IssueRepository)issuesRepo)
                .findAllByProject(this);

        for (Issue i: issues) {
            placeAndlinkIssue(i);
        }
    }

    public void placeAndlinkIssue(Issue i) {
        // 1. Put all project's issues into the "issue bag".
        issueBag.put(i.getId(), i);

        // 2. Make parent-child links (searching in the memory only).
        if (i.getParentId() > 0) {
            i.linkParent(issueBag.get(i.getParentId()) , true);
        }

        // 3. Add the issue to the container
        if (i.getSprintId() == 0) {
            backlog.add(i);
        } else {
            IssueContainer sprint = sprints.get(i.getSprintId());
            if (sprint != null) {
                sprint.add(i);
            }
        }
    }

    //--------------           Team           -------------------------
    //
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    //--------------          Issue           -------------------------
    //
    public Issue createIssue(String type) {

        Issue issue = ctx.getFactory().createIssue(type, this);

        // Сохраняем в базе, получаем ID и добавляем в бэклог
        issue = (Issue)issuesRepo.save(issue);

        issueBag.put(issue.getId(), issue);
        backlog.add(issue);

        return issue;
    }

    public Issue getIssueById(int id, boolean isloadFromDB) {

        Issue retval;

        if ((retval = issueBag.get(id)) != null) {
            return retval;
        }

        if (isloadFromDB) {
            retval = (Issue) issuesRepo.findById(id).orElse(null);
        }

        return retval;
    }

    public void removeIssue(Issue issue) {

        if (issue.getSprintId() == 0) {
            backlog.remove(issue);
        } else {
            sprints.get(issue.getSprintId()).remove(issue);
        }

        issueBag.remove(issue.getId());

        issuesRepo.delete(issue);
    }

    public void saveIssue(Issue issue) {
        issuesRepo.save(issue);
    }

    //--------------          Sprints           -----------------------
    //
    public IssueContainer createSprint(LocalDate from, LocalDate to, int capacity) {

        IssueContainer sprint = ctx.getFactory()
                .createSprint(from, to, capacity, this);
        addSprint(sprint);
        return sprint;
    }

    public IssueContainer getSprint(int id) {
        return sprints.get(id);
    }

    public List<IssueContainer> getAllSprints() {
        return sprints.values().stream().collect(Collectors.toList());
    }

    public void addSprint(IssueContainer sprint) {
        if (sprint != null) {
            sprints.put(((Sprint)sprint).getId(), sprint);
        }
    }

    public void removeSprint(Integer id) {
        sprints.remove(id);
        IssueContainer sprint = sprints.get(id);

        if (sprint != null) {
            sprintsRepo.delete(sprint);
        }
    }

    public void saveSprint(String name) {
        IssueContainer sprint = sprints.get(id);

        if (sprint != null) {
            sprintsRepo.save(sprint);
        }
    }

    //-----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public IssueContainer getBacklog() {
        return backlog;
    }

    public void setBacklog(IssueContainer backlog) {
        this.backlog = backlog;
    }

    public List<IssueContainer> getSprints() {
        return sprints.values().stream().collect(Collectors.toList());
    }

    public ServerContext getContext() {
        return ctx;
    }

    @Override
    public String toString() {
        return name;
    }


    //---------------------------

    private void resetContainers() {
        backlog.removeAll();
        for (IssueContainer s: sprints.values()) {
            s.removeAll();
        }

        issueBag.clear();
    }
}
