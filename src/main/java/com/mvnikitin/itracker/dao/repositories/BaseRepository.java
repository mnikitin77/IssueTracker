package com.mvnikitin.itracker.dao.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseRepository<T, ID> implements IssueTrackerRepository<T, ID> {

    protected Connection con;

    protected PreparedStatement findByIdStmt;
    protected PreparedStatement findAllStmt;
    protected PreparedStatement insertStmt;
    protected PreparedStatement updateStmt;
    protected PreparedStatement countStmt;
    protected PreparedStatement existsStmt;
    protected PreparedStatement deleteStmt;

    @Override
    public long count() {
        long retval = 0;

        try (ResultSet rs = countStmt.executeQuery()) {

            if (rs.next()) {
                retval = rs.getLong(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return retval;
    }

    @Override
    public boolean existsById(ID primaryKey) {

        boolean retval = false;

        if (primaryKey == null) {
            throw new IllegalArgumentException();
        }

        try {
            existsStmt.setInt(1, (int)primaryKey);

            try (ResultSet rs = existsStmt.executeQuery()) {
                if (rs.next()) {
                    retval = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return retval;
    }

    public void close() {
        try {
            if (findByIdStmt != null) {
                findByIdStmt.close();
            }
            if (findAllStmt != null) {
                findAllStmt.close();
            }
            if (insertStmt != null) {
                insertStmt.close();
            }
            if (updateStmt != null) {
                updateStmt.close();
            }
            if (countStmt != null) {
                countStmt.close();
            }
            if (existsStmt != null) {
                existsStmt.close();
            }
            if (deleteStmt != null) {
                deleteStmt.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
