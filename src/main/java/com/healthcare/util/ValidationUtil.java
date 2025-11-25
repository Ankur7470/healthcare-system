package com.healthcare.util;

import com.healthcare.exception.InvalidDataException;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10}$");
    
    private static final Pattern BLOOD_GROUP_PATTERN = 
        Pattern.compile("^(A|B|AB|O)[+-]$");

    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new InvalidDataException(fieldName + " cannot be null");
        }
    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(fieldName + " cannot be empty");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidDataException("Invalid email format: " + email);
        }
    }

    public static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new InvalidDataException("Invalid phone number. Must be 10 digits: " + phoneNumber);
        }
    }

    public static void validateBloodGroup(String bloodGroup) {
        if (bloodGroup == null || !BLOOD_GROUP_PATTERN.matcher(bloodGroup).matches()) {
            throw new InvalidDataException("Invalid blood group. Must be A+, A-, B+, B-, AB+, AB-, O+, or O-: " + bloodGroup);
        }
    }

    public static void validatePositiveNumber(int number, String fieldName) {
        if (number <= 0) {
            throw new InvalidDataException(fieldName + " must be a positive number");
        }
    }

    public static void validateNonNegativeNumber(int number, String fieldName) {
        if (number < 0) {
            throw new InvalidDataException(fieldName + " cannot be negative");
        }
    }

    public static void validateGender(String gender) {
        if (gender == null || (!gender.equalsIgnoreCase("Male") && 
                               !gender.equalsIgnoreCase("Female") && 
                               !gender.equalsIgnoreCase("Other"))) {
            throw new InvalidDataException("Invalid gender. Must be Male, Female, or Other");
        }
    }

    public static void validateIdFormat(String id, String idType) {
        validateNotEmpty(id, idType);
        if (id.length() < 3) {
            throw new InvalidDataException(idType + " must be at least 3 characters long");
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    public static boolean isValidBloodGroup(String bloodGroup) {
        return bloodGroup != null && BLOOD_GROUP_PATTERN.matcher(bloodGroup).matches();
    }
}
