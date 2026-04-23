package controller;

import java.util.Arrays;

import javax.swing.JOptionPane;

import model.User;
import service.TaskService;
import service.UserService;
import view.LoginView;
import view.RegisterView;

/**
 * Handles user interactions on the registration screen.
 */
public class RegisterController {

    private final RegisterView registerView;
    private final UserService userService;
    private final TaskService taskService;

    /**
     * Creates a controller for the supplied registration view.
     *
     * @param registerView registration screen managed by this controller
     */
    public RegisterController(RegisterView registerView, UserService userService, TaskService taskService) {
        this.registerView = registerView;
        this.userService = userService;
        this.taskService = taskService;
    }

    /**
     * Validates registration fields and shows a success response.
     */
    public void onRegisterButtonClick() {
        String fullName = registerView.getFullname();
        String username = registerView.getUsername();
        String email = registerView.getEmail();
        char[] password = registerView.getPassword();
        char[] confirmPassword = registerView.getConfirmPassword();

        try {
            if (fullName.isBlank() || username.isBlank() || email.isBlank()
                    || password.length == 0 || confirmPassword.length == 0) {
                registerView.setStatus("Please complete all fields.");
                return;
            }

            if (!isValidEmail(email)) {
                registerView.setStatus("Please enter a valid email address.");
                return;
            }

            if (!Arrays.equals(password, confirmPassword)) {
                registerView.setStatus("Passwords do not match.");
                return;
            }

            User newUser = userService.registerUser(username, fullName, email, new String(password));
            showInfo("Registration successful.", "Register");
            openLoginView(newUser);
        } catch (IllegalArgumentException ex) {
            registerView.setStatus(ex.getMessage());
        } catch (RuntimeException ex) {
            showError("Unable to complete registration: " + ex.getMessage(), "Registration Error");
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');
        }
    }

    /**
     * Returns to the login screen.
     */
    public void onLoginButtonClick() {
        openLoginView(null);
    }

    /**
     * Opens the login screen and optionally prefills its username field.
     *
     * @param registeredUser recently entered user information; may be {@code null}
     */
    private void openLoginView(User registeredUser) {
        LoginView loginView = new LoginView(registeredUser, userService, taskService);
        loginView.setVisible(true);
        registerView.dispose();
    }

    /**
     * Performs email validation.
     *
     * @param email email text entered by the user
     * @return {@code true} when the text resembles an email address
     */
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@")
                && email.indexOf('@') > 0
                && email.indexOf('@') < email.length() - 1;
    }

    /**
     * Shows a standard information dialog.
     *
     * @param message message to display
     * @param title dialog title
     */
    private void showInfo(String message, String title) {
        JOptionPane.showMessageDialog(registerView, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(registerView, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
