package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import controller.RegisterController;
import service.TaskService;
import service.UserService;

/**
 * Registration window.
 */
public class RegisterView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final RegisterController controller;

    private JTextField txtFullname;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblStatus;

    /**
     * Creates the registration screen.
     */
    public RegisterView(UserService userService, TaskService taskService) {
        controller = new RegisterController(this, userService, taskService);
        buildUI();
        applyInitialState();
    }

    /**
     * Builds the Swing components for the screen.
     */
    private void buildUI() {
        setTitle("Task Manager - Register");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 10));

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setBorder(BorderFactory.createEmptyBorder(14, 14, 0, 14));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.add(new JLabel("Create a new account"));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        formPanel.add(new JLabel("Full name:"));
        txtFullname = new JTextField(20);
        formPanel.add(txtFullname);

        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField(20);
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField(20);
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Confirm password:"));
        txtConfirmPassword = new JPasswordField(20);
        formPanel.add(txtConfirmPassword);

        formWrap.add(titlePanel, BorderLayout.NORTH);
        formWrap.add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        lblStatus = new JLabel("Fill out your details, or click Login to return.");

        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.add(btnLogin);
        btnRow.add(btnRegister);

        bottomPanel.add(lblStatus, BorderLayout.NORTH);
        bottomPanel.add(btnRow, BorderLayout.SOUTH);

        add(formWrap, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Registers listeners that delegate button clicks to the controller.
     */
    private void addActionListeners() {
        btnLogin.addActionListener(e -> controller.onLoginButtonClick());
        btnRegister.addActionListener(e -> controller.onRegisterButtonClick());
    }

    /**
     * Applies common startup configuration after the components exist.
     */
    private void applyInitialState() {
        pack();
        addActionListeners();
        setResizable(false);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnRegister);
        SwingUtilities.invokeLater(() -> txtFullname.requestFocusInWindow());
    }

    /**
     * @return trimmed full name from the form
     */
    public String getFullname() {
        return txtFullname.getText().trim();
    }

    /**
     * @return trimmed username from the form
     */
    public String getUsername() {
        return txtUsername.getText().trim();
    }

    /**
     * @return trimmed email from the form
     */
    public String getEmail() {
        return txtEmail.getText().trim();
    }

    /**
     * @return password characters from the password field
     */
    public char[] getPassword() {
        return txtPassword.getPassword();
    }

    /**
     * @return confirmation password characters from the password field
     */
    public char[] getConfirmPassword() {
        return txtConfirmPassword.getPassword();
    }

    /**
     * Updates the status/help text at the bottom of the screen.
     *
     * @param msg status message to display
     */
    public void setStatus(String msg) {
        lblStatus.setText(msg);
    }
}
