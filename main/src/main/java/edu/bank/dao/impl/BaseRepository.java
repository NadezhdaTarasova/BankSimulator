package edu.bank.dao.impl;

import edu.bank.exeption.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseRepository {

    public static final Connection connection = getConnection();

    private static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/bank", "nadezhda", "password");
        } catch (SQLException e) {
            throw new DAOException();
        }
    }
}
