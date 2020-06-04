package ui;

import entity.User;
import web.LoginWindowLogic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {
    private JTextField loginField;
    private JTextField passwordField;
    private JButton loginButton;
    private JButton registrationButton;
    private JLabel errorLabel;
    private JPanel rootPanel;

    LoginWindowLogic loginWindowLogic;

    public LoginWindow() {
        loginWindowLogic = new LoginWindowLogic();

        setContentPane(rootPanel);
        setVisible(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        loginButton.addActionListener(e -> login());

        registrationButton.addActionListener(e -> register());
    }

    public void login() {
        String username = loginField.getText();
        String password = passwordField.getText();
        if (!username.equals("") && !password.equals("")) {
            User user = loginWindowLogic.login(username, password);
            if (user == null) {
                errorLabel.setText("Can't find user. Check fields");
            }
            else {
                MainWindow mainWindow = new MainWindow(user, password);
                mainWindow.setSize(600, 600);
                setVisible(false);
                dispose();
            }
        }
        else {
            errorLabel.setText("Check fields");
        }
    }

    public void register() {
        String username = loginField.getText();
        String password = passwordField.getText();
        if (!username.equals("") && !password.equals("")) {
            loginWindowLogic.register(username, password);
            errorLabel.setText("Now you can log in");
        }
        else {
            errorLabel.setText("Check fields");
        }
    }
}
