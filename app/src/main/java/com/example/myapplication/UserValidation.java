package com.example.myapplication;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

public class UserValidation {
    public static boolean validateRegisterInputs(EditText emailField, EditText usernameField, EditText passwordField, EditText confirmPasswordField, Context context) {
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (isEmpty(emailField)) {
            emailField.setError("Email is required");
            Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailField.getText().toString()).matches()) {
            emailField.setError("Invalid email address");
            Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!emailField.getText().toString().endsWith("@ems.com")) {
            emailField.setError("Email must be from domain ems.com");
            Toast.makeText(context, "Email must be from domain ems.com", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isEmpty(usernameField)) {
            usernameField.setError("Username is required");
            Toast.makeText(context, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (usernameField.getText().toString().length() < 5) {
            usernameField.setError("Username must be at least 5 characters");
            Toast.makeText(context, "Username must be at least 5 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isEmpty(passwordField)) {
            passwordField.setError("Password is required");
            Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordField.getText().toString().length() < 6) {
            passwordField.setError("Password must be at least 6 characters");
            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if (isEmpty(confirmPasswordField)) {
            confirmPasswordField.setError("Confirm Password is required");
            Toast.makeText(context, "Confirm Password is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!passwordField.getText().toString().equals(confirmPasswordField.getText().toString())) {
            confirmPasswordField.setError("Passwords do not match");
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean validateLoginInputs(EditText emailField, EditText passwordField, Context context) {
        if (isEmpty(emailField)) {
            emailField.setError("Email is required");
            Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailField.getText().toString()).matches()) {
            emailField.setError("Invalid email address");
            Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!emailField.getText().toString().endsWith("@ems.com")) {
            emailField.setError("Email must be from domain ems.com");
            Toast.makeText(context, "Email must be from domain ems.com", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isEmpty(passwordField)) {
            passwordField.setError("Password is required");
            Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Helper method to check if an EditText field is empty
    private static boolean isEmpty(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString().trim());
    }
}