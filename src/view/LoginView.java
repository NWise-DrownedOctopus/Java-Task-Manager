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

import controller.LoginController;
import model.User;
import service.TaskService;
import service.UserService;

/**
 * Login window.
 */
public class LoginView extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblStatus;

    private final LoginController controller;

    /**
     * Creates a login screen with no prefilled user information.
     */
    public LoginView(UserService userService, TaskService taskService) {
        this(null, userService, taskService);
    }

    /**
     * Creates a login screen and optionally prefills known user data.
     *
     * @param user user whose username should be shown on the form; may be null
     */
    public LoginView(User user, UserService userService, TaskService taskService) {
        controller = new LoginController(this, userService, taskService);
        buildUI();
        applyInitialState(user);
    }

    /**
     * Builds the Swing components for the screen.
     */
    private void buildUI() {
        setTitle("Task Manager - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 10));

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setBorder(BorderFactory.createEmptyBorder(14, 14, 0, 14));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.add(new JLabel("Sign in to continue"));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField(20);
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField(20);
        formPanel.add(txtPassword);

        formWrap.add(titlePanel, BorderLayout.NORTH);
        formWrap.add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        lblStatus = new JLabel("Enter username and password, or register a new account.");

        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.add(btnRegister);
        btnRow.add(btnLogin);

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
     *
     * @param user optional user whose username should be prefilled
     */
    private void applyInitialState(User user) {
        if (user != null && user.getUsername() != null) {
            txtUsername.setText(user.getUsername());
            setStatus("Registration complete. You can sign in with the suggested username.");
        }

        pack();
        addActionListeners();
        setResizable(false);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnLogin);
        SwingUtilities.invokeLater(() -> txtUsername.requestFocusInWindow());
    }

    /**
     * @return trimmed username from the text field
     */
    public String getUsername() {
        return txtUsername.getText().trim();
    }

    /**
     * @return password characters from the password field
     */
    public char[] getPassword() {
        return txtPassword.getPassword();
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
