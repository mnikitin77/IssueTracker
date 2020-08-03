package com.mvnikitin.issuetracker;

import com.mvnikitin.issuetracker.configuration.ServerContext;
import com.mvnikitin.issuetracker.dao.repositories.BaseRepository;
import com.mvnikitin.issuetracker.factory.BasicIssueTrackingFactory;
import com.mvnikitin.issuetracker.factory.PMSFactory;
import com.mvnikitin.issuetracker.filter.IssueFilter;
import com.mvnikitin.issuetracker.issue.Issue;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class SomeMain {

    public static void main(String[] args) {

        ServerContext ctx = null;
//        BaseRepository userRep = null;
        BaseRepository wfRep = null;
        BaseRepository prjRep = null;
        BaseRepository issueRep = null;

        try {
            ctx = new ServerContext("jdbc:postgresql://localhost:5432" +
                    "/issue_tracker?user=postgres&password=Qwerty123");

//            userRep = new UserRepository(cfg.getConnection());
//            userRep = cfg.getRepository("user");
            wfRep = ctx.getRepository("workflow");

//            Workflow wf = (Workflow) wfRep.findById(2).orElse(null);
//
//            List<Workflow> workflows = (List<Workflow>) wfRep.findAll();


            PMSFactory factory = BasicIssueTrackingFactory.getFactory();
//            Workflow taskWF = (lesson15.hw.issuelifecycle.Workflow)factory.createWorkflow("Bug", "OPEN");
//            taskWF.setName("Bug Workflow");
//            taskWF.setDescription("WF для ошибок");
//            taskWF.addNextState("OPEN", "CLOSED");
//            taskWF.addNextState("OPEN", "IN PROGRESS");
//            taskWF.addNextState("IN PROGRESS", "REVIEW");
//            taskWF.addNextState("REVIEW", "TEST");
//            taskWF.addNextState("TEST", "RESOLVED");
//            taskWF.addNextState("RESOLVED", "CLOSED");
//            taskWF.addNextState("CLOSED", "REOPENED");
//            taskWF.addNextState("REOPENED", "IN PROGRESS");
//            taskWF.addNextState("REOPENED", "CLOSED");
//            // Insert
//            wfRep.save(taskWF);


//            Workflow taskWF = (Workflow) wfRep.findById(5).orElse(null);

//            taskWF.addNextState("OPEN", "IN PROGRESS");
//            taskWF.addNextState("IN PROGRESS", "OPEN");
//            // Update
//            wfRep.save(taskWF);

            // Update
//            wfRep.delete(taskWF);

            prjRep = ctx.getRepository("project");
            Project proj = ctx.getProject("Тестовый проект");
//            Project proj = (Project) prjRep.findById(41).orElse(null);

//            UserEntity newUser = new UserEntity();
//            newUser.setFirstName("Порфирий");
//            newUser.setMiddleName("Петрович");
//            newUser.setLastName("Прокофьев");
//            newUser.setUsername("champion");
//            newUser.setPassword("quertyquerty".hashCode());
//            newUser.setPhone("+7 910 123 4567");
//            newUser.setEmail("superporfity@mail.ru");
//            newUser.setEmployeeCode("100001");
//
//            // CREATE
//            UserEntity inserted = (UserEntity)userRep.save(newUser);
//
//            // READ
//            Optional<UserEntity> user1 = (Optional<UserEntity>)userRep.findById(1);
//            List<UserEntity> userList = (List<UserEntity>)userRep.findAll();
//            long count = userRep.count();
//            boolean exist= userRep.existsById(inserted.getId());
//
//            // UPDATE
//            newUser.setPassword("StrongPWD10".hashCode());
//            UserEntity updated = (UserEntity)userRep.save(inserted);
//
//            // DELETE
//            userRep.delete(updated);

            // Issue
            proj.reloadContent();

//            issueRep = ctx.getRepository("issue");

//            Issue parent = (Issue) issueRep.findById(2).orElse(null);
//            Issue child = (Issue) issueRep.findById(3).orElse(null);

//            Issue child = parent.createChild();
//            child.setState("REVIEW");
//            child.setPriority("Low");
//            child.setTitle("Реализовать стильную кнопочку \"Отмена\"");
//            child.setDescription("Тарам-парам, трам-пам-пам...");
//            child.setCode("CDE123");
//            child.setStoryPoints(3);
//            child.setReporter(parent.getAssignee());
//
//            proj.saveIssue(child);

            IssueFilter filter = factory.createFilter();
            Stream<Issue> backlog = proj.getBacklog().getAllIssues().stream();

            // 1) по приоритету
            Stream<Issue> filteredIssues = filter.filter(
                    backlog,
                    null,
                    null,
                    "priority",
                    new String[]{"High", "Low"});

            Stream<Issue> backlog2 = proj.getBacklog().getAllIssues().stream();
            filteredIssues = filter.filter(
                    backlog2,
//                LocalDateTime.of(2020, 6, 1, 0, 0, 0),
//                LocalDateTime.of(2020, 7, 15, 0, 0, 0),
                    null,
                    LocalDateTime.of(2020, 6, 1, 0, 0, 0),
                    "created",
                    null);

            Stream<Issue> backlog3 = proj.getBacklog().getAllIssues().stream();
            filteredIssues = filter.filter(
                    backlog3,
                    null,
                    null,
                    "title",
                    new String[]{"как"});

            Stream<Issue> backlog4 = proj.getBacklog().getAllIssues().stream();
            filteredIssues = filter.filter(
                    backlog4,
                    null,
                    null,
                    "assignee",
                    new String[]{"ivanov"});

            Stream<Issue> backlog5 = proj.getBacklog().getAllIssues().stream();
            filteredIssues = filter.filter(
                    backlog5,
                    null,
                    null,
                    "reporter",
                    new String[]{"champion"});

            int i = 0;

        } finally {
            ctx.close();
        }
    }
}
