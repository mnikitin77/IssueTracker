package com.mvnikitin.issuetracker;

import com.mvnikitin.issuetracker.Project;
import com.mvnikitin.issuetracker.user.Team;
import com.mvnikitin.issuetracker.configuration.ServerContext;
import com.mvnikitin.issuetracker.dao.repositories.BaseRepository;
import com.mvnikitin.issuetracker.factory.BasicIssueTrackingFactory;
import com.mvnikitin.issuetracker.factory.PMSFactory;
import com.mvnikitin.issuetracker.user.User;
import com.mvnikitin.issuetracker.utils.*;
import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Test;

public class TestDBProjectRepository extends TestDBBase {

    private final static String connString = "jdbc:postgresql://localhost:" +
            "5432/issue_tracker?user=postgres&password=Qwerty123";

    private static PMSFactory factory = BasicIssueTrackingFactory.getFactory();

    public TestDBProjectRepository(String name) {
        super(name);
    }

    @Test
    public  void test_project_insert() throws Exception {

        ServerContext ctx = null;
        BaseRepository prjRep = null;
        Project project = null;

        User user1 = DBTestUtils.createUser1();
        User user2 = DBTestUtils.createUser2();

        try {
            ctx = new ServerContext(connString);

            project = factory.createProject(ctx);
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

            prjRep = ctx.getRepository("project");
            prjRep.save(project);

            IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(
                    Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("dbunit/ProjectInsertExpectedDataset.xml"));

            IDataSet actualDataSet = tester.getConnection().createDataSet();

            String[] ignored = {"id", "admin_id", "owner_id"};

            Assertion.assertEqualsIgnoreCols(expectedDataSet, actualDataSet, "projects", ignored);
        } finally {
            prjRep.delete(project);

            DBTestUtils.deleteUser(user1);
            DBTestUtils.deleteUser(user2);

            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
