package service;

import java.util.Optional;

import dao.UserDao;
import model.User;
import util.PasswordHasher;

/**
 * Business logic for registration and authentication.
 */
public class UserService {

    private final UserDao userDao;
    private final PasswordHasher passwordHasher;

    public UserService(UserDao userDao, PasswordHasher passwordHasher) {
        this.userDao = userDao;
        this.passwordHasher = passwordHasher;
    }

    public User registerUser(String username, String fullName, String email, String plainPassword) {
        String normalizedUsername = normalizeRequired(username, "Username");
        String normalizedFullName = normalizeRequired(fullName, "Full name");
        String normalizedEmail = normalizeRequired(email, "Email");
        String normalizedPassword = normalizeRequired(plainPassword, "Password");

        Optional<User> existingUser = userDao.readByUsername(normalizedUsername);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("That username is already taken.");
        }

        User newUser = new User();
        newUser.setUsername(normalizedUsername);
        newUser.setFullName(normalizedFullName);
        newUser.setEmail(normalizedEmail);
        newUser.setPasswordHash(passwordHasher.hash(normalizedPassword));
        newUser.setIsAdmin(false);

        int id = userDao.create(newUser);
        newUser.setId(id);
        return newUser;
    }

    public User login(String username, String plainPassword) {
        String normalizedUsername = normalizeRequired(username, "Username");
        String normalizedPassword = normalizeRequired(plainPassword, "Password");

        Optional<User> user = userDao.readByUsername(normalizedUsername);
        if (user.isEmpty()) {
            return null;
        }

        String storedHash = user.get().getPasswordHash();
        if (passwordHasher.matches(normalizedPassword, storedHash == null ? null : storedHash.trim())) {
            return user.get();
        }
        return null;
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }
}
