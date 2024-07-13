package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class ManageInventoryUI extends JFrame {
    private JPanel ManageInventoryForm;
    private JTable InventoryTable;
    private JTabbedPane tabbedPane1;
    private JTextField addItemName;
    private JTextField addItemCategory;
    private JTextField addItemQty;
    private JTextField addItemPrice;
    private JButton addItemButton;
    private JScrollPane InventoryTableSP;
    private JButton homeButton;
    private JLabel updateItemID;
    private JTextField updateItemName;
    private JTextField updateItemCategory;
    private JTextField updateItemPrice;
    private JButton updateItemButton;
    private JTextField updateItemQty;
    private JButton removeItemButton;
    private JLabel removeItemID;

    private static int rowValue=-100;

    private Connection con;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    public ManageInventoryUI() {
        setContentPane(ManageInventoryForm);
        setTitle("Manage Inventory");
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

        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode =  "INSERT INTO inventory ( `Item Name`, `Category`, `Quantity`, `Price`) " +
                        "VALUES ( ?, ?, ?,?)";

                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, addItemName.getText());
                    preparedStatement.setString(2, addItemCategory.getText());
                    preparedStatement.setString(3, addItemQty.getText());
                    preparedStatement.setString(4, addItemPrice.getText());

                    int resultSet = preparedStatement.executeUpdate();

                    if(resultSet > 0){
                        JOptionPane.showMessageDialog(ManageInventoryUI.this,"supplier is successfully added!");
                        populateTable();
                        clearValues();
                        makeTableClickable();
                    }else{
                        JOptionPane.showMessageDialog(ManageInventoryUI.this,"can't add supplier details");
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        updateItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode = "UPDATE inventory " +
                        "SET `Item Name` = ?, `Category` = ?, `Quantity` = ?, `Price`= ?" +
                        "WHERE `item ID` = ?";

                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, updateItemName.getText());
                    preparedStatement.setString(2, updateItemCategory.getText());
                    preparedStatement.setString(3, updateItemQty.getText());
                    preparedStatement.setString(4, updateItemPrice.getText());

                    if(rowValue==-100){
                        JOptionPane.showMessageDialog(ManageInventoryUI.this,"Please Select Item From The Table!");

                    }else {
                        preparedStatement.setString(5, InventoryTable.getModel().getValueAt(rowValue, 0).toString());

                        int resultSet = preparedStatement.executeUpdate();

                        if(resultSet > 0){
                            JOptionPane.showMessageDialog(ManageInventoryUI.this," updated successfully");
                            populateTable();
                            clearValues();
                            makeTableClickable();
                        }else{
                            JOptionPane.showMessageDialog(ManageInventoryUI.this,"can't update  details");
                        }
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlCode =  "DELETE FROM inventory  WHERE `item ID` = ?";
                try {
                    preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, InventoryTable.getModel().getValueAt(rowValue,0).toString());
                    int resultSet = preparedStatement.executeUpdate();

                    if(resultSet > 0){
                        JOptionPane.showMessageDialog(ManageInventoryUI.this,"item is successfully Deleted");
                        populateTable();
                        removeItemID.setText("Please Select a item");
                        makeTableClickable();
                    }else{
                        JOptionPane.showMessageDialog(ManageInventoryUI.this,"can't Delete the item");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

    }

    private  void populateTable(){

        String sqlCode = "SELECT * FROM inventory";
        try {
            preparedStatement = con.prepareStatement(sqlCode);
            resultSet = preparedStatement.executeQuery();
            InventoryTable =new JTable(buildTableModel(resultSet));

            JScrollPane scrollPane = new JScrollPane(InventoryTable);
            // Set the viewport view of the OrdersTable JScrollPane
            InventoryTableSP.setViewportView(scrollPane);

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
        addItemName.setText("");
        addItemCategory.setText("");
        addItemQty.setText("");
        addItemPrice.setText("");

        updateItemID.setText("");
        updateItemName.setText("");
        updateItemCategory.setText("");
        updateItemPrice.setText("");
        updateItemQty.setText("");
    }
    private void makeTableClickable(){
        InventoryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                rowValue=InventoryTable.getSelectedRow();

                rowValue=InventoryTable.getSelectedRow();

                updateItemID.setText(InventoryTable.getModel().getValueAt(rowValue,0).toString());
                updateItemName.setText(InventoryTable.getModel().getValueAt(rowValue,1).toString());
                updateItemCategory.setText(InventoryTable.getModel().getValueAt(rowValue,2).toString());
                updateItemQty.setText(InventoryTable.getModel().getValueAt(rowValue,3).toString());
                updateItemPrice.setText(InventoryTable.getModel().getValueAt(rowValue,4).toString());

                removeItemID.setText(InventoryTable.getModel().getValueAt(rowValue,0).toString());

            }
        });
    }

}
