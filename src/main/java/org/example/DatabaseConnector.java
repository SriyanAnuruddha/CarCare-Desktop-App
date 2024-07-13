package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    public static Connection getSQLConnection(){
        try {
            Connection con =DriverManager.getConnection("jdbc:mysql://localhost:3306/CarCare","root","");
            return con;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
