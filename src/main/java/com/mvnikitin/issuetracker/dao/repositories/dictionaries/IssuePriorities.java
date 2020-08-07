package com.mvnikitin.issuetracker.dao.repositories.dictionaries;

import com.mvnikitin.issuetracker.configuration.DBConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("priorities")
@DependsOn("connection")
public class IssuePriorities  implements Dictionary {

    private Map<String, Integer> nameToId;
    private Map<Integer, String> idToName;

    private DBConnection connMngr;

    @PostConstruct
    private void init() {
        Connection connection = connMngr.getConnection();

        try (Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery("SELECT * FROM issue_priorities")) {

            nameToId = DictionaryUtils.rsToMapValueId(rs);
            rs.beforeFirst();
            idToName = DictionaryUtils.rsToMapValueName(rs);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Autowired
    public void setConnMngr(DBConnection connMngr) {
        this.connMngr = connMngr;
    }

    @Override
    public List<String> getAllItems() {
        return nameToId.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public int getIdByName(String name) {
        return nameToId.get(name);
    }

    @Override
    public String getNameById(int id) {
        return idToName.get(id);
    }
}
