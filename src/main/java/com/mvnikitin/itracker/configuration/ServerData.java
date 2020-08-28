package com.mvnikitin.itracker.configuration;

import com.mvnikitin.itracker.business.Project;
import com.mvnikitin.itracker.dao.repositories.IssueTrackerRepository;
import com.mvnikitin.itracker.business.issuelifecycle.Stateable;
import com.mvnikitin.itracker.business.issuelifecycle.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ServerData {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerData.class);

    private Map<String, Stateable> workflows = new HashMap<>();
    private Map<String, Project> projects = new HashMap<>();

    private IssueTrackerRepository projRepo;
    private IssueTrackerRepository wfRepo;

    @PostConstruct
    private void init() {
        loadWorkflows();
        loadProjects();
    }

    @Autowired
    @Qualifier("proj_repo")
    public void setProjRepo(IssueTrackerRepository projRepo) {
        this.projRepo = projRepo;
    }

    @Autowired
    @Qualifier("wf_repo")
    public void setWfRepo(IssueTrackerRepository wfRepo) {
        this.wfRepo = wfRepo;
    }


    public Stateable getWorkflow(String type) {
        return type != null ? workflows.get(type) : null;
    }

    public void loadWorkflows() {
        workflows.clear();

        List<Workflow> wfsList = (List<Workflow>) wfRepo.findAll();
        for (Workflow w: wfsList) {
            workflows.put(w.getType(), w);
        }

        LOGGER.debug("Workflows are loaded");
    }

    public Project getProject(String name) {
        return name != null ? projects.get(name) : null;
    }

    public void loadProjects() {
        projects.clear();

        List<Project> prjList = (List<Project>) projRepo.findAll();
        for (Project p: prjList) {
            projects.put(p.getName(), p);
        }

        LOGGER.debug("Projects are loaded");
    }
}
