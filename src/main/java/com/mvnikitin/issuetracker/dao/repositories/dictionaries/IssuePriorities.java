package com.mvnikitin.issuetracker.dao.repositories.dictionaries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IssuePriorities  implements Dictionary {

    private Map<String, Integer> nameToId;
    private Map<Integer, String> idToName;

    public IssuePriorities(Connection connection) {
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
