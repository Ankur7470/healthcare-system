package com.healthcare.util;

import com.healthcare.exception.InvalidDataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    @DisplayName("Should pass validation when value is not null")
    void testValidateNotNull_Valid() {
        assertDoesNotThrow(() -> {
            ValidationUtil.validateNotNull("value", "Field");
        });
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void testValidateNotNull_Null() {
        Exception exception = assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateNotNull(null, "Field");
        });
        assertTrue(exception.getMessage().contains("Field cannot be null"));
    }

    @Test
    @DisplayName("Should pass validation when string is not empty")
    void testValidateNotEmpty_Valid() {
        assertDoesNotThrow(() -> {
            ValidationUtil.validateNotEmpty("value", "Field");
        });
    }

    @Test
    @DisplayName("Should throw exception when string is empty")
    void testValidateNotEmpty_Empty() {
        Exception exception = assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateNotEmpty("", "Field");
        });
        assertTrue(exception.getMessage().contains("Field cannot be empty"));
    }

    @Test
    @DisplayName("Should throw exception when string is null")
    void testValidateNotEmpty_Null() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateNotEmpty(null, "Field");
        });
    }

    @Test
    @DisplayName("Should throw exception when string is whitespace")
    void testValidateNotEmpty_Whitespace() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateNotEmpty("   ", "Field");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "test@example.com",
        "user.name@example.com",
        "user+tag@example.co.uk",
        "user_name@example.org"
    })
    @DisplayName("Should pass validation for valid email formats")
    void testValidateEmail_Valid(String email) {
        assertDoesNotThrow(() -> {
            ValidationUtil.validateEmail(email);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid-email",
        "@example.com",
        "user@",
        "user.example.com",
        "user @example.com",
        "user@.com"
    })
    @DisplayName("Should throw exception for invalid email formats")
    void testValidateEmail_Invalid(String email) {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateEmail(email);
        });
    }

    @Test
    @DisplayName("Should throw exception for null email")
    void testValidateEmail_Null() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateEmail(null);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "9876543210",
        "1234567890",
        "0000000000"
    })
    @DisplayName("Should pass validation for valid phone numbers")
    void testValidatePhoneNumber_Valid(String phone) {
        assertDoesNotThrow(() -> {
            ValidationUtil.validatePhoneNumber(phone);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123456789",
        "12345678901",
        "abcdefghij",
        "123-456-7890",
        "+911234567890"
    })
    @DisplayName("Should throw exception for invalid phone numbers")
    void testValidatePhoneNumber_Invalid(String phone) {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validatePhoneNumber(phone);
        });
    }

    @Test
    @DisplayName("Should throw exception for null phone number")
    void testValidatePhoneNumber_Null() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validatePhoneNumber(null);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"})
    @DisplayName("Should pass validation for valid blood groups")
    void testValidateBloodGroup_Valid(String bloodGroup) {
        assertDoesNotThrow(() -> {
            ValidationUtil.validateBloodGroup(bloodGroup);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "B", "AB", "O", "C+", "XY+", "A+-", "Invalid"})
    @DisplayName("Should throw exception for invalid blood groups")
    void testValidateBloodGroup_Invalid(String bloodGroup) {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateBloodGroup(bloodGroup);
        });
    }

    @Test
    @DisplayName("Should throw exception for null blood group")
    void testValidateBloodGroup_Null() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateBloodGroup(null);
        });
    }

    @Test
    @DisplayName("Should pass validation for positive number")
    void testValidatePositiveNumber_Valid() {
        assertDoesNotThrow(() -> {
            ValidationUtil.validatePositiveNumber(10, "Field");
        });
    }

    @Test
    @DisplayName("Should throw exception for zero")
    void testValidatePositiveNumber_Zero() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validatePositiveNumber(0, "Field");
        });
    }

    @Test
    @DisplayName("Should throw exception for negative number")
    void testValidatePositiveNumber_Negative() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validatePositiveNumber(-5, "Field");
        });
    }

    @Test
    @DisplayName("Should pass validation for non-negative number")
    void testValidateNonNegativeNumber_Valid() {
        assertDoesNotThrow(() -> {
            ValidationUtil.validateNonNegativeNumber(0, "Field");
        });
        assertDoesNotThrow(() -> {
            ValidationUtil.validateNonNegativeNumber(10, "Field");
        });
    }

    @Test
    @DisplayName("Should throw exception for negative number in non-negative validation")
    void testValidateNonNegativeNumber_Negative() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateNonNegativeNumber(-1, "Field");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"Male", "Female", "Other", "male", "FEMALE", "other"})
    @DisplayName("Should pass validation for valid genders")
    void testValidateGender_Valid(String gender) {
        assertDoesNotThrow(() -> {
            ValidationUtil.validateGender(gender);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"M", "F", "Invalid", "Man", "Woman", ""})
    @DisplayName("Should throw exception for invalid genders")
    void testValidateGender_Invalid(String gender) {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateGender(gender);
        });
    }

    @Test
    @DisplayName("Should throw exception for null gender")
    void testValidateGender_Null() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateGender(null);
        });
    }

    @Test
    @DisplayName("Should pass ID format validation for valid ID")
    void testValidateIdFormat_Valid() {
        assertDoesNotThrow(() -> {
            ValidationUtil.validateIdFormat("PAT001", "Patient ID");
        });
    }

    @Test
    @DisplayName("Should throw exception for short ID")
    void testValidateIdFormat_Short() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateIdFormat("AB", "ID");
        });
    }

    @Test
    @DisplayName("Should throw exception for empty ID")
    void testValidateIdFormat_Empty() {
        assertThrows(InvalidDataException.class, () -> {
            ValidationUtil.validateIdFormat("", "ID");
        });
    }

    @Test
    @DisplayName("Should return true for valid email check")
    void testIsValidEmail_Valid() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
    }

    @Test
    @DisplayName("Should return false for invalid email check")
    void testIsValidEmail_Invalid() {
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    @DisplayName("Should return true for valid phone number check")
    void testIsValidPhoneNumber_Valid() {
        assertTrue(ValidationUtil.isValidPhoneNumber("9876543210"));
    }

    @Test
    @DisplayName("Should return false for invalid phone number check")
    void testIsValidPhoneNumber_Invalid() {
        assertFalse(ValidationUtil.isValidPhoneNumber("123"));
        assertFalse(ValidationUtil.isValidPhoneNumber(null));
    }

    @Test
    @DisplayName("Should return true for valid blood group check")
    void testIsValidBloodGroup_Valid() {
        assertTrue(ValidationUtil.isValidBloodGroup("A+"));
    }

    @Test
    @DisplayName("Should return false for invalid blood group check")
    void testIsValidBloodGroup_Invalid() {
        assertFalse(ValidationUtil.isValidBloodGroup("Invalid"));
        assertFalse(ValidationUtil.isValidBloodGroup(null));
    }
}
