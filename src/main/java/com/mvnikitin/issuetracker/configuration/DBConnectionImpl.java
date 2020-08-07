package com.mvnikitin.issuetracker.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component("connection")
public class DBConnectionImpl implements DBConnection {

    private Connection conn;
    @Value("${db.connection}")
    private String connectionString;
    @Value("${db.driver}")
    private String driver;

    @PostConstruct
    private void init() throws ClassNotFoundException, SQLException {
        Class.forName( driver );
        conn = DriverManager.getConnection(connectionString);
    }

    @Override
    public Connection getConnection() {
        try {
            if (!conn.isValid(1)) {
                conn = DriverManager.getConnection(connectionString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    @PreDestroy
    private void clear() throws SQLException {
        conn.close();
    }
}
