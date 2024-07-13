package org.example;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeUI extends JFrame{

    private JPanel HomeUIPanel;
    private JButton manageOrdersButton;
    private JButton manageSuppliersButton;
    private JButton manageInventoryButton;
    private JButton manageEmployeesButton;
    private JButton generateMonthlyReportButton;

    public HomeUI() {
        setContentPane(HomeUIPanel);
        setTitle("Home");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(UIManager.getWidth(),UIManager.getHeight());
        setLocationRelativeTo(null);
        setVisible(true);

        manageOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageOrdersUI();
                setVisible(false);
            }
        });

        manageSuppliersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new ManageSuppliersUI();
            }
        });


        manageInventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new ManageInventoryUI();
            }
        });

        manageEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new ManageEmployeesUI();
            }
        });


        generateMonthlyReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new GenerateReport();
            }
        });
    }
}
