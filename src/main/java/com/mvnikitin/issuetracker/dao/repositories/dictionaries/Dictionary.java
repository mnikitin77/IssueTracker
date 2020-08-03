package com.mvnikitin.issuetracker.dao.repositories.dictionaries;

import java.util.List;

public interface Dictionary {

    List<String> getAllItems();

    int getIdByName(String name);

    String getNameById(int id);
}
