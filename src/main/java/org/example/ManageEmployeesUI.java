package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class ManageEmployeesUI extends JFrame{
    private JPanel ManageEmployeesForm;
    private JTable employeesTable;

    private JScrollPane employeesTableSP;
    private JTabbedPane tabbedPane1;
    private JTextField addEmpName;
    private JTextField addEmpJob;
    private JTextField addEmpEmail;
    private JTextField addEmpSalary;
    private JButton addEmployeesButton;
    private JButton homeButton;
    private JLabel updateEmployeeID;
    private JTextField updateEmpName;
    private JTextField updateEmpJob;
    private JTextField updateEmpEmail;
    private JTextField updateEmpSalary;
    private JButton updateEmployeeDetailsButton;
    private JButton removeEmployeeButton;
    private JLabel removeEmpID;

    private Connection con;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    private static int rowValue=-100;

    public ManageEmployeesUI(){
        setContentPane(ManageEmployeesForm);
        setTitle("Manage Employees");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(UIManager.getWidth(),UIManager.getHeight());
        setLocationRelativeTo(null);
        setVisible(true);

        con=DatabaseConnector.getSQLConnection(); //connect to database
        populateTable();
        makeTableClickable();

        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeUI();
                setVisible(false);
            }
        });


        addEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode =  "INSERT INTO employee ( `Employee Name`, `Job Position`, `Email`, `Salary`) " +
                        "VALUES ( ?, ?, ?,?)";

                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, addEmpName.getText());
                    preparedStatement.setString(2, addEmpJob.getText());
                    preparedStatement.setString(3, addEmpEmail.getText());
                    preparedStatement.setString(4, addEmpSalary.getText());

                    int resultSet = preparedStatement.executeUpdate();

                    if(resultSet > 0){
                        JOptionPane.showMessageDialog(ManageEmployeesUI.this," successfully added!");
                        populateTable();
                        clearValues();
                        makeTableClickable();
                    }else{
                        JOptionPane.showMessageDialog(ManageEmployeesUI.this,"can't add  details");
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        updateEmployeeDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode = "UPDATE employee " +
                        "SET `Employee Name` = ?, `Job Position` = ?, `Email` = ?, `Salary` = ?" +
                        "WHERE `Employee ID` = ?";

                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, updateEmpName.getText());
                    preparedStatement.setString(2, updateEmpJob.getText());
                    preparedStatement.setString(3, updateEmpEmail.getText());
                    preparedStatement.setString(4, updateEmpSalary.getText());

                    if(rowValue==-100){
                        JOptionPane.showMessageDialog(ManageEmployeesUI.this,"Please Select Employee From The Table!");

                    }else {
                        preparedStatement.setString(5, employeesTable.getModel().getValueAt(rowValue, 0).toString());

                        int resultSet = preparedStatement.executeUpdate();

                        if(resultSet > 0){
                            JOptionPane.showMessageDialog(ManageEmployeesUI.this," updated successfully");
                            populateTable();
                            clearValues();
                            makeTableClickable();
                        }else{
                            JOptionPane.showMessageDialog(ManageEmployeesUI.this,"can't update  details");
                        }
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        removeEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode =  "DELETE FROM employee  WHERE `Employee ID` = ?";
                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, employeesTable.getModel().getValueAt(rowValue,0).toString());
                    int resultSet = preparedStatement.executeUpdate();

                    if(resultSet > 0){
                        JOptionPane.showMessageDialog(ManageEmployeesUI.this,"employee is successfully Deleted");
                        populateTable();
                        removeEmpID.setText("Please Select a Employee");
                        makeTableClickable();
                    }else{
                        JOptionPane.showMessageDialog(ManageEmployeesUI.this,"can't Delete the Employee");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
    }

    private  void populateTable(){

        String sqlCode = "SELECT * FROM employee";
        try {
            preparedStatement = con.prepareStatement(sqlCode);
            resultSet = preparedStatement.executeQuery();
            employeesTable =new JTable(buildTableModel(resultSet));

            JScrollPane scrollPane = new JScrollPane(employeesTable);
            // Set the viewport view of the OrdersTable JScrollPane
            employeesTableSP.setViewportView(scrollPane);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }


    private void clearValues(){
        addEmpName.setText("");
        addEmpEmail.setText("");
        addEmpJob.setText("");
        addEmpSalary.setText("");

        updateEmployeeID.setText("");
        updateEmpEmail.setText("");
        updateEmpName.setText("");
        updateEmpSalary.setText("");
        updateEmpJob.setText("");
    }
    private void makeTableClickable(){
        employeesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                rowValue=employeesTable.getSelectedRow();

                rowValue=employeesTable.getSelectedRow();

                updateEmployeeID.setText(employeesTable.getModel().getValueAt(rowValue,0).toString());
                updateEmpName.setText(employeesTable.getModel().getValueAt(rowValue,1).toString());
                updateEmpJob.setText(employeesTable.getModel().getValueAt(rowValue,2).toString());
                updateEmpEmail.setText(employeesTable.getModel().getValueAt(rowValue,3).toString());
                updateEmpSalary.setText(employeesTable.getModel().getValueAt(rowValue,4).toString());

                removeEmpID.setText(employeesTable.getModel().getValueAt(rowValue,0).toString());

            }
        });
    }

}
