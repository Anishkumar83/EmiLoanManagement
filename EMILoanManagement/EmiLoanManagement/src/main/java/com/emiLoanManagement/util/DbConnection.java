package com.emiLoanManagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {


    static {
        try{
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSql driver loaded");
        }
        catch (ClassNotFoundException e){
            throw new RuntimeException("Postgre sql driver not found", e);
        }
    }

    private static String jdbcURL
            = "jdbc:postgresql://localhost:5432/emidb";
    private static String username = "postgres";
    private static String password = "root";

    public DbConnection() {
    }



    public static Connection dbConnect() throws SQLException {
            return DriverManager.getConnection(jdbcURL,username,password);
    }
}
