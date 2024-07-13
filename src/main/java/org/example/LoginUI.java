package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginUI extends JFrame{
    private JTextField usernameTxt;
    private JPasswordField passwordTxt;
    private JPanel loginFormPanel;
    private JButton loginBtn;

    private Connection con;
    private Statement statement;
    ResultSet resultSet;
    public LoginUI(){
        setContentPane(loginFormPanel);
        setTitle("User Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(UIManager.getWidth(),UIManager.getHeight());
        setLocationRelativeTo(null);
        setVisible(true);

        // Connect to Database
        con=DatabaseConnector.getSQLConnection();

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username=usernameTxt.getText();
                String password=passwordTxt.getText();
                String sqlCode = "SELECT * FROM admins WHERE username = ? AND password = ?";
                try {
                    PreparedStatement preparedStatement = con.prepareStatement(sqlCode);
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    resultSet = preparedStatement.executeQuery();

                    if(resultSet.next()){
                            HomeUI ui=new HomeUI();
                            setVisible(false);
                    }else{
                        JOptionPane.showMessageDialog(LoginUI.this,"username or password is incorrect!");
                    }


                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }



            }
        });
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
