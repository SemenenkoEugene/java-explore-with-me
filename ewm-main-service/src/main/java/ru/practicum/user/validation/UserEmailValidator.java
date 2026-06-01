package ru.practicum.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserEmailValidator implements ConstraintValidator<UserEmail, String> {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public void initialize(final UserEmail userEmail) {
    }

    @Override
    public boolean isValid(final String email, final ConstraintValidatorContext context) {
        if (email == null) {
            return true;
        }

        final Pattern pattern = Pattern.compile(EMAIL_REGEX);
        final Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return false;
        }

        final String[] parts = email.split("@");

        final String login = parts[0];
        if (login.length() > 64) {
            return false;
        }

        final String domain = parts[1];
        final String[] domainParts = domain.split("\\.");
        for (String domainPart : domainParts) {
            if (domainPart.length() > 63) {
                return false;
            }
        }

        return true;
    }
}
