package com.mvnikitin.issuetracker;

import com.mvnikitin.issuetracker.backlog.IssueContainer;
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

    private IssueContainer backlog;
    private Map<String, IssueContainer> sprints = new HashMap<>();

    private BaseRepository issuesRepo;
    private BaseRepository sprintsRepo;

    public Project(ServerContext ctx) {
        this.ctx = ctx;
        backlog = ctx.getFactory().createBacklog();
        issuesRepo = ctx.getRepository("issue");
        sprintsRepo = ctx.getRepository("sprint");
    }

    // Загрузить информацию об issues из базы, наполнить и спринты
    public void reloadContent() {

        // загружаем Issues в бэклог
        reloadContainer(null);

        // загружаем Issues в спринты
        for (IssueContainer s: sprints.values()) {
            reloadContainer(s);
        }
    }

    public void reloadContainer(IssueContainer sprint) {
        ((IssueRepository)issuesRepo).loadIssueContainer(this, sprint);
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
        backlog.add(issue);

        return issue;
    }

    public Issue getIssueById(int id, boolean isloadFromDB) {

        Issue retval;

        if ((retval = backlog.getIssueById(id)) != null) {
            return retval;
        } else {
            for (IssueContainer ic: sprints.values()) {
                if ((retval = ic.getIssueById(id)) != null) {
                    return retval;
                }
            }
        }

        if (isloadFromDB) {
            retval = (Issue) issuesRepo.findById(id).orElse(null);
        }

        return retval;
    }

    public void removeIssue(Issue issue) {

        if (!backlog.remove(issue)) {
            for (IssueContainer ic: sprints.values()) {
                if (ic.remove(issue)) {
                    break;
                }
            }
        }

        issuesRepo.delete(issue);
    }

    public void saveIssue(Issue issue) {
        issuesRepo.save(issue);
    }

    //--------------          Sprints           -----------------------
    //
    public IssueContainer createSprint(LocalDate from, LocalDate to, int capacity) {
        IssueContainer sprint = ctx.getFactory().createSprint(from, to, capacity, this);
        sprints.put(sprint.getName(), sprint);
        return sprint;
    }

    public IssueContainer getSprint(String name) {
        return sprints.get(name);
    }

    public void addSprint(IssueContainer sprint) {
        if (sprint != null) {
            sprints.put(sprint.getName(), sprint);
        }
    }

    public void removeSprint(String name) {
        IssueContainer sprint = sprints.get(name);
        sprints.remove(name);
        sprintsRepo.delete(sprint);
    }

    public void saveSprint(String name) {
        sprintsRepo.save(sprints.get(name));
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
}
