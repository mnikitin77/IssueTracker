package com.mvnikitin.itracker.dao.repositories.dictionaries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DictionaryUtils {

    public static Map<String, Integer> rsToMapValueId(ResultSet rs)
            throws SQLException {

        Map<String, Integer> items = new HashMap();

        while (rs.next()) {
            items.put(rs.getString(2), rs.getInt(1));
        }
        return items;
    }

    public static Map<Integer, String> rsToMapValueName(ResultSet rs)
            throws SQLException {

        Map<Integer, String> items = new HashMap();

        while (rs.next()) {
            items.put(rs.getInt(1), rs.getString(2));
        }
        return items;
    }
}
