package com.mvnikitin.itracker.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component("connection")
public class DBConnectionImpl implements DBConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBConnectionImpl.class);

    private Connection conn;
    @Value("${db.connection}")
    private String connectionString;
    @Value("${db.driver}")
    private String driver;

    @PostConstruct
    private void init() throws ClassNotFoundException, SQLException {
        Class.forName( driver );
        conn = DriverManager.getConnection(connectionString);
        LOGGER.debug("DB connection [" + conn + "] is open");
    }

    @Override
    public Connection getConnection() {
        try {
            if (!conn.isValid(1)) {
                conn = DriverManager.getConnection(connectionString);
                LOGGER.info("New DB connection is open");
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occured: ", e);
        }
        return conn;
    }

    @PreDestroy
    private void clear() throws SQLException {
        conn.close();
        LOGGER.debug("DB connection [" + conn + "] is closed");
    }
}
