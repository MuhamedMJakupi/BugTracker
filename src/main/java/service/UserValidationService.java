package service;

import common.enums.UserRole;
import domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserValidationService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public List<String> validateUser(User user) {
        List<String> errors = new ArrayList<>();

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            errors.add("Name is mandatory");
        } else if (user.getName().length() < 2 || user.getName().length() > 100) {
            errors.add("Name must be between 2 and 100 characters");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.add("Email is mandatory");
        } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.add("Email must be valid");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            errors.add("Password is mandatory");
        } else if (user.getPasswordHash().length() < 8) {
            errors.add("Password must be at least 8 characters");
        }
        if (user.getRoleId() < 1 || user.getRoleId() > 4) {
            errors.add("Invalid role");
        }

        try {
            UserRole.fromId(user.getRoleId());
        } catch (IllegalArgumentException e) {
            errors.add("Invalid role: " + e.getMessage());
        }
        return errors;
    }

    public List<String> validateUserForUpdate(User user) {
        List<String> errors = validateUser(user);

        if (user.getUserId() == null) {
            errors.add("User ID is required for updates");
        }
        return errors;
    }

    public List<String> validateUserForCreation(User user) {
        List<String> errors = validateUser(user);
        if (user.getUserId() != null) {
            errors.add("User ID should not be provided for new users");
        }
        return errors;
    }
}
