package com.mvnikitin.issuetracker.configuration;

import java.sql.Connection;

public interface DBConnection {
    Connection getConnection();
}
