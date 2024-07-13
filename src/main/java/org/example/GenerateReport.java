package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class GenerateReport extends JFrame {
    private Connection con;
    PreparedStatement preparedStatement;
    ResultSet resultSet;
    private JPanel GenerateReportForm;
    private JButton generateReportButton;
    private JButton homeButton;
    private JLabel carIncomeTxt;
    private JLabel empSalariesTxt;
    private JLabel itemExpensesTxt;
    private JLabel totalIncomeTxt;

    private double orderSum=0.0;
    private double salarySum=0.0;
    private double itemSum=0.0;
    public GenerateReport(){
        setContentPane(GenerateReportForm);
        setTitle("Generate Report");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(UIManager.getWidth(),UIManager.getHeight());
        setLocationRelativeTo(null);
        setVisible(true);



        con=DatabaseConnector.getSQLConnection(); //connect to database
        String sqlCode =  "select SUM(Amount) AS TotalAmount FROM orders";
        try {

            preparedStatement = con.prepareStatement(sqlCode);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                orderSum = resultSet.getDouble("TotalAmount");
            }

            sqlCode =  "select SUM(Salary) AS TotalAmount FROM employee";

            preparedStatement = con.prepareStatement(sqlCode);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                salarySum = resultSet.getDouble("TotalAmount");
            }

            sqlCode =  "select SUM(price) AS TotalAmount FROM inventory";

            preparedStatement = con.prepareStatement(sqlCode);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                itemSum = resultSet.getDouble("TotalAmount");
            }

            generateReportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    carIncomeTxt.setText(String.valueOf(orderSum));
                    empSalariesTxt.setText(String.valueOf(salarySum));
                    itemExpensesTxt.setText(String.valueOf(itemSum));
                    totalIncomeTxt.setText(String.valueOf(orderSum-itemSum-salarySum));
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeUI();
                setVisible(false);
            }
        });
    }
}
