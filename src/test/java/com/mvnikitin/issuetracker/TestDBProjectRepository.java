package com.mvnikitin.issuetracker;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.configuration.AppConfig;
import com.mvnikitin.issuetracker.configuration.DBConnection;
import com.mvnikitin.issuetracker.dao.repositories.IssueTrackerRepository;
import com.mvnikitin.issuetracker.factory.IssueTrackingFactory;
import com.mvnikitin.issuetracker.user.Team;
import com.mvnikitin.issuetracker.user.User;
import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class TestDBProjectRepository extends TestDBBase {

    @Autowired
    private DBConnection connMngr;

    @Autowired
    private IssueTrackingFactory factory;

    @Autowired
    @Qualifier("proj_repo")
    private IssueTrackerRepository projRepo;

    @Autowired
    @Qualifier("user_repo")
    private IssueTrackerRepository userRepo;

    @Test
    public  void test_project_insert() throws Exception {

        Project project = null;

        User user1 = createUser1();
        User user2 = createUser2();

        try {
            project = factory.createProject();
            project.setName("Тестовый проект");
            project.setDescription("Прикольный проект, очень интересный, инновационный такой");
            project.setBusinessUnit("Розничный блок");

            Team team = factory.createTeam();
            team.setName("Упыри 2");
            team.add(user1);
            team.add(user2);

            project.setOwner(user1);
            project.setAdmin(user2);
            project.setTeam(team);

            projRepo.save(project);

            IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(
                    Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("dbunit/ProjectInsertExpectedDataset.xml"));

            IDataSet actualDataSet = tester.getConnection().createDataSet();

            String[] ignored = {"id", "admin_id", "owner_id"};

            Assertion.assertEqualsIgnoreCols(expectedDataSet, actualDataSet, "projects", ignored);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            projRepo.delete(project);

            userRepo.delete(user1);
            userRepo.delete(user2);
        }
    }

    public User createUser1() {

        User user = new User();

        user.setFirstName("Порфирий");
        user.setMiddleName("Петрович");
        user.setLastName("Прокофьев");
        user.setUsername("champion");
        user.setPassword("quertyquerty".hashCode());
        user.setPhone("+7 910 123 4567");
        user.setEmail("superporfiry@mail.ru");
        user.setEmployeeCode("100001");

        userRepo.save(user);

        return user;
    }

    public User createUser2() {

        User user = new User();

        user.setFirstName("Сидор");
        user.setMiddleName("Сидорович");
        user.setLastName("Иванов");
        user.setUsername("ivanov");
        user.setPassword("1abcDefg#2".hashCode());
        user.setPhone("+7 985 000 0001");
        user.setEmail("ivanov@mail.ru");
        user.setEmployeeCode("000001");

        userRepo.save(user);

        return user;
    }
}
