package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class ManageSuppliersUI extends  JFrame {
    private JPanel ManageSuppliersForm;
    private JTable suppliersTable;
    private JTabbedPane tabbedPane1;
    private JTextField addSuppName;
    private JTextField addSuppMobile;
    private JTextField addSuppEmail;
    private JButton addSupplierButton;
    private JButton homeButton;
    private JTextField updateSuppID;
    private JTextField updateSuppName;
    private JTextField updateSuppEmail;
    private JButton updateSupplierDetailsButton;
    private JLabel deleteSuppID;
    private JButton removeSupplierButton;
    private JTextField updateSuppMobile;
    private JLabel updateSuppIDtxt;
    private JScrollPane suppliersTableSP;

    private Connection con;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    private static int rowValue=-100;
    public ManageSuppliersUI() {
        setContentPane(ManageSuppliersForm);
        setTitle("Manage Orders");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(UIManager.getWidth(),UIManager.getHeight());
        setLocationRelativeTo(null);
        setVisible(true);

        con=DatabaseConnector.getSQLConnection(); //connect to database
        populateTable();
        makeTableClickable();


        addSupplierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode =  "INSERT INTO suppliers ( `Supplier Name`, `Mobile Number`, `Email`) " +
                        "VALUES ( ?, ?, ?)";

                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, addSuppName.getText());
                    preparedStatement.setString(2, addSuppMobile.getText());
                    preparedStatement.setString(3, addSuppEmail.getText());


                    int resultSet = preparedStatement.executeUpdate();

                    if(resultSet > 0){
                        JOptionPane.showMessageDialog(ManageSuppliersUI.this,"supplier is successfully added!");
                        populateTable();
                        clearValues();
                        makeTableClickable();
                    }else{
                        JOptionPane.showMessageDialog(ManageSuppliersUI.this,"can't add supplier details");
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        updateSupplierDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode = "UPDATE suppliers " +
                        "SET `Supplier Name` = ?, `Mobile Number` = ?, `Email` = ?" +
                        "WHERE `supplier ID` = ?";

                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, updateSuppName.getText());
                    preparedStatement.setString(2, updateSuppMobile.getText());
                    preparedStatement.setString(3, updateSuppEmail.getText());


                    if(rowValue==-100){
                        JOptionPane.showMessageDialog(ManageSuppliersUI.this,"Please Select Order From The Table!");

                    }else {
                        preparedStatement.setString(4, suppliersTable.getModel().getValueAt(rowValue, 0).toString());

                        int resultSet = preparedStatement.executeUpdate();

                        if(resultSet > 0){
                            JOptionPane.showMessageDialog(ManageSuppliersUI.this," updated successfully");
                            populateTable();
                            clearValues();
                            makeTableClickable();
                        }else{
                            JOptionPane.showMessageDialog(ManageSuppliersUI.this,"can't update  details");
                        }
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        removeSupplierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode =  "DELETE FROM suppliers  WHERE `supplier ID` = ?";
                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, suppliersTable.getModel().getValueAt(rowValue,0).toString());
                    int resultSet = preparedStatement.executeUpdate();

                    if(resultSet > 0){
                        JOptionPane.showMessageDialog(ManageSuppliersUI.this,"supplier is successfully Deleted");
                        populateTable();
                        deleteSuppID.setText("Please Select a supplier");
                        makeTableClickable();
                    }else{
                        JOptionPane.showMessageDialog(ManageSuppliersUI.this,"can't Delete the supplier");
                    }
                } catch (SQLException ex) {
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


    private  void populateTable(){

        String sqlCode = "SELECT * FROM suppliers";
        try {
            preparedStatement = con.prepareStatement(sqlCode);
            resultSet = preparedStatement.executeQuery();
            suppliersTable =new JTable(buildTableModel(resultSet));

            JScrollPane scrollPane = new JScrollPane(suppliersTable);
            // Set the viewport view of the OrdersTable JScrollPane
            suppliersTableSP.setViewportView(scrollPane);

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
        addSuppEmail.setText("");
        addSuppMobile.setText("");
        addSuppName.setText("");

        updateSuppEmail.setText("");
        updateSuppIDtxt.setText("");
        updateSuppName.setText("");
        updateSuppMobile.setText("");
    }
    private void makeTableClickable(){
        suppliersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                rowValue=suppliersTable.getSelectedRow();

                rowValue=suppliersTable.getSelectedRow();

                updateSuppIDtxt.setText(suppliersTable.getModel().getValueAt(rowValue,0).toString());
                updateSuppName.setText(suppliersTable.getModel().getValueAt(rowValue,1).toString());
                updateSuppMobile.setText(suppliersTable.getModel().getValueAt(rowValue,2).toString());
                updateSuppEmail.setText(suppliersTable.getModel().getValueAt(rowValue,3).toString());

                deleteSuppID.setText(suppliersTable.getModel().getValueAt(rowValue,0).toString());

            }
        });
    }


}

