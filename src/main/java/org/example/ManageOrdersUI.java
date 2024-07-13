package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class ManageOrdersUI extends JFrame {

    private JPanel ManageOrdersForm;
    private JTabbedPane tabbedPane1;
    private JTable table1;
    private JScrollPane OrdersTable;
    private JTextField addOrderDetails;
    private JTextField addCustomerName;
    private JTextField addCustomerEmail;
    private JTextField addCustomerMobile;
    private JTextField addAmount;
    private JButton addOrderBtn;
    private JTextField updateOrderDetails;
    private JTextField updateCustomerName;
    private JTextField updateCustomerEmail;
    private JTextField updateCustomerMobile;
    private JTextField updateAmount;
    private JButton updateOrder;
    private JLabel updateOrderID;
    private JComboBox updateOrderComboBox;
    private JButton deleteOrderButton;
    private JLabel selectedOrderTxt;
    private JButton homeButton;
    private JTable assignedEmpTbl;
    private JTable availableEmpTbl;
    private JScrollPane assignedEmpTblSP;
    private JScrollPane availableEmpTblSP;
    private JButton assignEmployeeButton;

    private Connection con;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    private static int selectedEmpRow=-100;
    private static int rowValue=-100;

    public ManageOrdersUI() {
        setContentPane(ManageOrdersForm);
        setTitle("Manage Orders");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(UIManager.getWidth(),UIManager.getHeight());
        setLocationRelativeTo(null);
        setVisible(true);

        con=DatabaseConnector.getSQLConnection(); //connect to database
        populateTable();
        makeTableClickable();

        addOrderBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String sqlCode =  "INSERT INTO orders (`Order Details`, Amount, `Customer Name`, `Customer Email`, `Customer Mobile`) " +
                        "VALUES ( ?, ?, ?, ?, ?)";

                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, addOrderDetails.getText());
                    preparedStatement.setString(2, addAmount.getText());
                    preparedStatement.setString(3, addCustomerName.getText());
                    preparedStatement.setString(4, addCustomerEmail.getText());
                    preparedStatement.setString(5, addCustomerMobile.getText());


                    int resultSet = preparedStatement.executeUpdate();

                    if(resultSet > 0){
                            JOptionPane.showMessageDialog(ManageOrdersUI.this,"Order is successfully added!");
                            populateTable();
                            populateAvailableEmpTable();
                            clearValues();
                            makeTableClickable();
                    }else{
                            JOptionPane.showMessageDialog(ManageOrdersUI.this,"can't add order details");
                    }




                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        updateOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode = "UPDATE orders " +
                        "SET `Order Details` = ?, Amount = ?, `Customer Name` = ?, `Customer Email` = ?, `Customer Mobile` = ?, `Order Status` =? " +
                        "WHERE `Order ID` = ?";

                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, updateOrderDetails.getText());
                    preparedStatement.setString(2, updateAmount.getText());
                    preparedStatement.setString(3, updateCustomerName.getText());
                    preparedStatement.setString(4, updateCustomerEmail.getText());
                    preparedStatement.setString(5, updateCustomerMobile.getText());
                    preparedStatement.setString(6, updateOrderComboBox.getSelectedItem().toString());


                    if(rowValue==-100){
                        JOptionPane.showMessageDialog(ManageOrdersUI.this,"Please Select Order From The Table!");

                    }else {
                        preparedStatement.setString(7, table1.getModel().getValueAt(rowValue, 0).toString());

                        int resultSet = preparedStatement.executeUpdate();

                        if(resultSet > 0){
                            JOptionPane.showMessageDialog(ManageOrdersUI.this,"Order is updated successfully");

                            // send email
                            if(updateOrderComboBox.getSelectedItem().toString().equalsIgnoreCase("Completed")){
                                String customerName = updateCustomerName.getText();
                                String email= updateCustomerEmail.getText();
                                String emailSubject = "Subject: Your Car Repair is Complete";
                                String emailBody = "Dear " + customerName + ",\n\n"
                                        + "We are delighted to inform you that the repairs on your car have been successfully completed. Your vehicle is now ready for pick up.\n\n"
                                        + "We appreciate your trust in CarCare PVT(LTD) for your automotive needs. If you have any questions or need further assistance, please feel free to contact us.\n\n"
                                        + "Best regards,\n"
                                        + "CarCare PVT(LTD)";

                                JavaMailUtil.sendMail(email,emailSubject,emailBody);
                                JOptionPane.showMessageDialog(ManageOrdersUI.this,"Sent notification to customer");

                            }

                            populateTable();
                            clearValues();
                            makeTableClickable();



                        }else{
                            JOptionPane.showMessageDialog(ManageOrdersUI.this,"can't update order details");
                        }
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        deleteOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode =  "DELETE FROM orders  WHERE `Order ID` = ?";
                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, table1.getModel().getValueAt(rowValue,0).toString());
                    int resultSet = preparedStatement.executeUpdate();

                    if(resultSet > 0){
                        JOptionPane.showMessageDialog(ManageOrdersUI.this,"Order is successfully Deleted");
                        populateTable();
                        selectedOrderTxt.setText("Please Select a Order");
                        makeTableClickable();
                    }else{
                        JOptionPane.showMessageDialog(ManageOrdersUI.this,"can't Delete the order");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });


        assignEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode = "INSERT INTO worksin VALUES (?, ?);";

                try {
                    // Initialize the preparedStatement
                    preparedStatement = con.prepareStatement(sqlCode);

                    if (rowValue == -100 || selectedEmpRow == -100) {
                        JOptionPane.showMessageDialog(ManageOrdersUI.this, "Please Select Order and Employee you want to assign");
                    } else {
                        preparedStatement.setString(1, table1.getModel().getValueAt(rowValue, 0).toString());
                        preparedStatement.setString(2, availableEmpTbl.getModel().getValueAt(selectedEmpRow, 0).toString());
                        String email=availableEmpTbl.getModel().getValueAt(rowValue, 2).toString();
                        String empName =availableEmpTbl.getModel().getValueAt(rowValue, 1).toString();

                        int resultSet = preparedStatement.executeUpdate();

                        if (resultSet > 0) {
                            JOptionPane.showMessageDialog(ManageOrdersUI.this, "Employee assigned successfully");
                            populateAvailableEmpTable();
                            populateAssignedEmpTbl();
                            makeTableClickable();

                            // sending email to assigned employee
                            String emailSubject = "New Job Assignment Notification";
                            String emailBody = "Dear "+ empName+",\n\n"
                                    + "Congratulations! We are pleased to inform you that you have been assigned a new job role within our company.\n\n"
                                    + "Your hard work and dedication have been recognized, and we are confident that you will excel in your new position. If you have any questions, please don't hesitate to reach out.\n\n"
                                    + "Best regards,\n"
                                    + "CarCare PVT(LTD)";

                            JavaMailUtil.sendMail(email,emailSubject,emailBody);
                            JOptionPane.showMessageDialog(ManageOrdersUI.this,"Sent notification to employee");

                        } else {
                            JOptionPane.showMessageDialog(ManageOrdersUI.this, "Can't assign employee");
                        }
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeUI();
                setVisible(false);
            }
        });
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


    private  void populateTable(){

        String sqlCode = "SELECT * FROM orders";
        try {
            preparedStatement = con.prepareStatement(sqlCode);
            resultSet = preparedStatement.executeQuery();
            table1 =new JTable(buildTableModel(resultSet));

            JScrollPane scrollPane = new JScrollPane(table1);
            // Set the viewport view of the OrdersTable JScrollPane
            OrdersTable.setViewportView(scrollPane);

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
        addAmount.setText("");
        addOrderDetails.setText("");
        addCustomerEmail.setText("");
        addCustomerName.setText("");
        addCustomerMobile.setText("");

        updateOrderID.setText("");
        updateAmount.setText("");
        updateOrderDetails.setText("");
        updateCustomerEmail.setText("");
        updateCustomerName.setText("");
        updateCustomerMobile.setText("");
    }

    private void makeTableClickable(){
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                rowValue=table1.getSelectedRow();

                updateOrderID.setText(table1.getModel().getValueAt(rowValue,0).toString());
                updateOrderDetails.setText(table1.getModel().getValueAt(rowValue,1).toString());
                updateAmount.setText(table1.getModel().getValueAt(rowValue,2).toString());
                updateCustomerName.setText(table1.getModel().getValueAt(rowValue,3).toString());
                updateCustomerEmail.setText(table1.getModel().getValueAt(rowValue,4).toString());
                updateCustomerMobile.setText(table1.getModel().getValueAt(rowValue,5).toString());

                selectedOrderTxt.setText(table1.getModel().getValueAt(rowValue,0).toString());

                populateAvailableEmpTable();
                selectedEmpID();
                populateAssignedEmpTbl();
            }
        });


    }

    private void populateAvailableEmpTable(){
        String sqlCode = "SELECT employee.`Employee ID`, employee.`Employee Name`, employee.`Email` FROM employee WHERE employee.`Employee ID` NOT IN (SELECT worksin.`Employee ID` FROM worksin WHERE worksin.`Order ID` = ?);";
        try {
            preparedStatement = con.prepareStatement(sqlCode);
            if(rowValue!=-100){
                preparedStatement.setString(1, table1.getModel().getValueAt(rowValue,0).toString());
                resultSet = preparedStatement.executeQuery();
                availableEmpTbl =new JTable(buildTableModel(resultSet));

                JScrollPane scrollPane = new JScrollPane(availableEmpTbl);
                // Set the viewport view of the OrdersTable JScrollPane
                availableEmpTblSP.setViewportView(scrollPane);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void  selectedEmpID(){
        availableEmpTbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                selectedEmpRow=availableEmpTbl.getSelectedRow();
                System.out.println(selectedEmpRow);
            }
        });

    }

    private void populateAssignedEmpTbl(){
        String sqlCode = "SELECT `worksin`.`Order ID`,`employee`.`Employee ID`,`employee`.`Employee Name`,`employee`.`Email` FROM employee,worksin WHERE `employee`.`Employee ID` = `worksin`.`Employee ID` AND `worksin`.`Order ID` = ?";
        try {
            preparedStatement = con.prepareStatement(sqlCode);
            if(rowValue!=-100){
                preparedStatement.setString(1, table1.getModel().getValueAt(rowValue,0).toString());
                resultSet = preparedStatement.executeQuery();
                assignedEmpTbl =new JTable(buildTableModel(resultSet));

                JScrollPane scrollPane = new JScrollPane(assignedEmpTbl);
                // Set the viewport view of the OrdersTable JScrollPane
                assignedEmpTblSP.setViewportView(scrollPane);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
