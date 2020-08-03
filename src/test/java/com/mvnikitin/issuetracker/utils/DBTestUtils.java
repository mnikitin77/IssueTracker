package com.mvnikitin.issuetracker.utils;

import com.mvnikitin.issuetracker.configuration.ServerContext;
import com.mvnikitin.issuetracker.dao.repositories.BaseRepository;
import com.mvnikitin.issuetracker.user.User;

public class DBTestUtils {

    private final static String connString = "jdbc:postgresql://localhost:" +
            "5432/issue_tracker?user=postgres&password=Qwerty123";

    public static User createUser1() {

        ServerContext ctx = ctx = new ServerContext(connString);
        BaseRepository userRep = ctx.getRepository("user");

        User user = new User();

        user.setFirstName("Порфирий");
        user.setMiddleName("Петрович");
        user.setLastName("Прокофьев");
        user.setUsername("champion");
        user.setPassword("quertyquerty".hashCode());
        user.setPhone("+7 910 123 4567");
        user.setEmail("superporfiry@mail.ru");
        user.setEmployeeCode("100001");

        userRep.save(user);
        ctx.close();

        return user;
    }

    public static User createUser2() {

        ServerContext ctx = ctx = new ServerContext(connString);
        BaseRepository userRep = ctx.getRepository("user");

        User user = new User();

        user.setFirstName("Сидор");
        user.setMiddleName("Сидорович");
        user.setLastName("Иванов");
        user.setUsername("ivanov");
        user.setPassword("1abcDefg#2".hashCode());
        user.setPhone("+7 985 000 0001");
        user.setEmail("ivanov@mail.ru");
        user.setEmployeeCode("000001");

        userRep.save(user);
        ctx.close();

        return user;
    }

    public static void deleteUser(User user) {
        ServerContext ctx = ctx = new ServerContext(connString);
        BaseRepository userRep = ctx.getRepository("user");

        userRep.delete(user);
        ctx.close();
    }
}
