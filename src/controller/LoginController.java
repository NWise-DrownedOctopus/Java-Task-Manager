package controller;

import java.util.Arrays;

import javax.swing.JOptionPane;

import model.User;
import service.TaskService;
import service.UserService;
import view.LoginView;
import view.ReadTaskListView;
import view.RegisterView;

/**
 * Handles user interactions on the login screen.
 */
public class LoginController {

    private final LoginView loginView;
    private final UserService userService;
    private final TaskService taskService;

    /**
     * Creates a controller for the supplied login view.
     *
     * @param loginView login screen managed by this controller
     */
    public LoginController(LoginView loginView, UserService userService, TaskService taskService) {
        this.loginView = loginView;
        this.userService = userService;
        this.taskService = taskService;
    }

    /**
     * Validates login fields and opens the task list when the form looks valid.
     */
    public void onLoginButtonClick() {
        String username = loginView.getUsername();
        char[] password = loginView.getPassword();

        try {
            if (username.isBlank() || password.length == 0) {
                loginView.setStatus("Please enter both username and password.");
                return;
            }

            User user = userService.login(username, new String(password));
            if (user != null) {
                ReadTaskListView taskListView = new ReadTaskListView(user, userService, taskService);
                taskListView.setVisible(true);
                loginView.dispose();
            } else {
                loginView.setStatus("Invalid username or password.");
            }
        } catch (IllegalArgumentException ex) {
            loginView.setStatus(ex.getMessage());
        } catch (RuntimeException ex) {
            showError("Unable to complete login: " + ex.getMessage(), "Login Error");
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    /**
     * Opens the register screen.
     */
    public void onRegisterButtonClick() {
        RegisterView registerView = new RegisterView(userService, taskService);
        registerView.setVisible(true);
        loginView.dispose();
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(loginView, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
