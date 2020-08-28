package com.mvnikitin.itracker.configuration;

import java.sql.Connection;

public interface DBConnection {
    Connection getConnection();
}
