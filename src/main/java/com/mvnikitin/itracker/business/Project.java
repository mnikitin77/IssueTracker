package com.mvnikitin.itracker.business;

import com.mvnikitin.itracker.business.backlog.IssueContainer;
import com.mvnikitin.itracker.business.backlog.Sprint;
import com.mvnikitin.itracker.dao.repositories.IssueRepository;
import com.mvnikitin.itracker.dao.repositories.IssueTrackerRepository;
import com.mvnikitin.itracker.factory.IssueTrackingFactory;
import com.mvnikitin.itracker.business.issue.Issue;
import com.mvnikitin.itracker.business.user.Team;
import com.mvnikitin.itracker.business.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("project")
@Scope("prototype")
public class Project {

    private static final Logger LOGGER = LoggerFactory.getLogger(Project.class);

    private int id;
    private Team team;

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

    private IssueTrackerRepository issuesRepo;
    private IssueTrackerRepository sprintsRepo;

    private IssueTrackingFactory factory;

    @PostConstruct
    private void init() {
        backlog = factory.createBacklog();
    }

    @Autowired
    @Qualifier("issue_repo")
    public void setIssuesRepo(IssueTrackerRepository issuesRepo) {
        this.issuesRepo = issuesRepo;
    }

    @Autowired
    @Qualifier("sprint_repo")
    public void setSprintsRepo(IssueTrackerRepository sprintsRepo) {
        this.sprintsRepo = sprintsRepo;
    }

    @Autowired
    public void setFactory(IssueTrackingFactory factory) {
        this.factory = factory;
    }

    // Очистить конейнеры, загрузить issues из базы,
    // связать между собой и добавить в контейнеры
    public void reloadIssues() {

        resetContainers();

        List<Issue> issues = (List<Issue>)((IssueRepository)issuesRepo)
                .findAllByProject(this);

        for (Issue i: issues) {
            placeAndlinkIssue(i);
        }

        LOGGER.debug("Project [" + this.name + "] is reloaded issues");
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

        Issue issue = factory.createIssue(type, this);

        // Сохраняем в базе, получаем ID и добавляем в бэклог
        issue = (Issue)issuesRepo.save(issue);

        issueBag.put(issue.getId(), issue);
        backlog.add(issue);

        LOGGER.debug("Project [" + this.name + "] created issue [id: " +
                issue.getId() + ", title: " + issue.getTitle() + "]");

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

        LOGGER.debug("Project [" + this.name + "] deleted issue [id: " +
            issue.getId() + ", title: " + issue.getTitle() + "]");
    }

    public void saveIssue(Issue issue) {
        issuesRepo.save(issue);
    }

    //--------------          Sprints           -----------------------
    //
    public IssueContainer createSprint(LocalDate from, LocalDate to, int capacity) {

        IssueContainer sprint = factory.createSprint(from, to, capacity, this);
        addSprint(sprint);

        LOGGER.debug("Project [" + this.name + "] created sprint [" +
                sprint.getName() + "]");

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

        LOGGER.debug("Project [" + this.name + "] deleted sprint [" +
                sprint.getName() + "]");
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

    public IssueTrackingFactory getFactory() {
        return factory;
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
