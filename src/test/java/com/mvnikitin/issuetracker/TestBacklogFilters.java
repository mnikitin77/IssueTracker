package com.mvnikitin.issuetracker;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.configuration.ServerContext;
import com.mvnikitin.issuetracker.dao.repositories.BaseRepository;
import com.mvnikitin.issuetracker.factory.BasicIssueTrackingFactory;
import com.mvnikitin.issuetracker.factory.PMSFactory;
import com.mvnikitin.issuetracker.filter.IssueFilter;
import com.mvnikitin.issuetracker.issue.Issue;
import com.mvnikitin.issuetracker.user.Team;
import com.mvnikitin.issuetracker.user.User;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestBacklogFilters {

    private static ServerContext ctx = new ServerContext("jdbc:postgresql://localhost:5432" +
            "/issue_tracker?user=postgres&password=Qwerty123");

    private static PMSFactory factory = BasicIssueTrackingFactory.getFactory();
    private static Project project;
    private static IssueFilter filter = factory.createFilter();
    private static BaseRepository projRepo = ctx.getRepository("project");
    private static BaseRepository userRepo = ctx.getRepository("user");

    private static Issue issue1, issue2, issue3, issue4, issue5;
    private static User user1, user2;

    @BeforeClass
    public static void prepareTests() {

        initProject();
        createIssues();
    }

    @AfterClass
    public static void clearTests() {

        projRepo.delete(project);

        for (User u: project.getTeam().getUsers()) {
            userRepo.delete(u);
        }

        if (ctx != null) {
            ctx.close();
        }
    }

    @Test
    public void test_priorityFilter() {
        Stream<Issue> backlog = project.getBacklog().getAllIssues().stream();

        Stream<Issue> filteredIssues = filter.filter(
                backlog,
                null,
                null,
                "priority",
                new String[]{"High", "Low"});

        List<Issue> expected = new ArrayList<>();
        expected.add(issue1);
        expected.add(issue2);
        expected.add(issue5);

        Assert.assertTrue(expected.containsAll(
                filteredIssues.collect(Collectors.toList())));
    }

    @Test
    public void test_createdFilter() {

        Issue issue = project.getBacklog().getAllIssues().get(0);
        issue.setCreated(LocalDateTime.now().minusDays(1));

        Stream<Issue> backlog = project.getBacklog().getAllIssues().stream();

        Stream<Issue> filteredIssues = filter.filter(
                backlog,
                null,
                LocalDateTime.now().minusMinutes(10),
                "created",
                null);

        List<Issue> expected = new ArrayList<>();
        expected.add(issue);

        Assert.assertTrue(expected.containsAll(
                filteredIssues.collect(Collectors.toList())));
    }

    @Test
    public void test_titleFilter() {
        Stream<Issue> backlog = project.getBacklog().getAllIssues().stream();

        Stream<Issue> filteredIssues = filter.filter(
                backlog,
                null,
                null,
                "title",
                new String[]{"как"});

        List<Issue> expected = new ArrayList<>();
        expected.add(issue2);
        expected.add(issue4);

        Assert.assertTrue(expected.containsAll(
                filteredIssues.collect(Collectors.toList())));
    }

    @Test
    public void test_assigneeFilter() {
        Stream<Issue> backlog = project.getBacklog().getAllIssues().stream();

        Stream<Issue> filteredIssues = filter.filter(
                backlog,
                null,
                null,
                "assignee",
                new String[]{"ivanov"});

        List<Issue> expected = new ArrayList<>();
        expected.add(issue1);
        expected.add(issue4);

        Assert.assertTrue(expected.containsAll(
                filteredIssues.collect(Collectors.toList())));
    }

    @Test
    public void test_reporterFilter() {
        Stream<Issue> backlog = project.getBacklog().getAllIssues().stream();

        Stream<Issue> filteredIssues = filter.filter(
                backlog,
                null,
                null,
                "reporter",
                new String[]{"champion"});

        List<Issue> expected = new ArrayList<>();
        expected.add(issue1);
        expected.add(issue4);

        Assert.assertTrue(expected.containsAll(
                filteredIssues.collect(Collectors.toList())));
    }

    private static void initProject() {
        project = factory.createProject(ctx);
        project.setName("MEGA Project");
        project.setDescription("It's an extra important project. " +
                "Unfortunately it is top secret. No piece of information is to share.");
        project.setBusinessUnit("The Corporate Board");

        Team team = factory.createTeam();

        user1 = new User();

        user1.setFirstName("Порфирий");
        user1.setMiddleName("Петрович");
        user1.setLastName("Прокофьев");
        user1.setUsername("champion");
        user1.setPassword("quertyquerty".hashCode());
        user1.setPhone("+7 910 123 4567");
        user1.setEmail("superporfity@mail.ru");
        user1.setEmployeeCode("100001");

        userRepo.save(user1);

        team.add(user1);
        project.setOwner(user1);

        user2 = new User();

        user2.setFirstName("Сидор");
        user2.setMiddleName("Сидорович");
        user2.setLastName("Иванов");
        user2.setUsername("ivanov");
        user2.setPassword("1abcDefg#2".hashCode());
        user2.setPhone("+7 985 000 0001");
        user2.setEmail("ivanov@mail.ru");
        user2.setEmployeeCode("000001");

        userRepo.save(user2);

        team.add(user2);

        project.setAdmin(user2);

        team.setName("Огонь");
        project.setTeam(team);

        projRepo.save(project);
    }

    private static void createIssues() {

        issue1 = project.createIssue("Epic");
        issue1.setState("REVIEW");
        issue1.setPriority("Low");
        issue1.setTitle("Реализовать стильную кнопочку \"Отмена\"");
        issue1.setDescription("Тарам-парам, трам-пам-пам...");
        issue1.setCode("CDE123");
        issue1.setStoryPoints(3);
        issue1.setReporter(user1);
        issue1.setAssignee(user2);

        issue2 = project.createIssue("Epic");
        issue2.setState("OPEN");
        issue2.setPriority("High");
        issue2.setTitle("Я как клиент хочу удалить все issue проекта");
        issue2.setDescription("При трёхкратном нажатии на кнопку DEL отображается запрос подтверждения удаления. В случае подтверждения все фичи проекта удаляются как с экрана, так и из базы данных.");
        issue2.setCode("ABC2");
        issue2.setStoryPoints(4);
        issue2.setReporter(user2);

        issue3 = issue2.createChild();
        issue3.setState("IN PROGRESS");
        issue3.setTitle("Панель управленческих показателей");
        issue3.setDescription("Должна быть панель управленческих показателей, отображающая в близком к реальному времени всякую инфу.");
        issue3.setCode("ABC123");
        issue3.setStoryPoints(15);
        issue3.setReporter(user2);
        issue3.setAssignee(user1);

        issue4 = issue2.createChild();
        issue4.setState("OPEN");
        issue4.setTitle("Я как клиент хочу увидеть мессаджбокс.");
        issue4.setDescription("При нажатии на кнопку клиенту отображается информативный мессадбокс.");
        issue4.setCode("ABC1");
        issue4.setStoryPoints(1);
        issue4.setReporter(user1);
        issue4.setAssignee(user2);

        issue5 = project.createIssue("Bug");
        issue5.setState("OPEN");
        issue5.setPriority("High");
        issue5.setTitle("Синий экран с белыми буквами.");
        issue5.setDescription("При нажатии на кнопку Enter получаю большой синий экран с белыми буквами.");
        issue5.setCode("ABC26");
        issue5.setStoryPoints(4);
        issue5.setReporter(user2);
    }
}
