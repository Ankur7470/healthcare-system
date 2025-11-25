package com.healthcare.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    @DisplayName("Should parse valid date string")
    void testParseDate_Valid() {
        LocalDate date = DateUtil.parseDate("2024-12-25");
        
        assertNotNull(date);
        assertEquals(2024, date.getYear());
        assertEquals(12, date.getMonthValue());
        assertEquals(25, date.getDayOfMonth());
    }

    @Test
    @DisplayName("Should throw exception for invalid date format")
    void testParseDate_InvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            DateUtil.parseDate("25-12-2024");
        });
    }

    @Test
    @DisplayName("Should throw exception for invalid date")
    void testParseDate_InvalidDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            DateUtil.parseDate("2024-13-45");
        });
    }

    @Test
    @DisplayName("Should return null for null date string")
    void testParseDate_Null() {
        LocalDate date = DateUtil.parseDate(null);
        assertNull(date);
    }

    @Test
    @DisplayName("Should return null for empty date string")
    void testParseDate_Empty() {
        LocalDate date = DateUtil.parseDate("");
        assertNull(date);
    }

    @Test
    @DisplayName("Should return null for whitespace date string")
    void testParseDate_Whitespace() {
        LocalDate date = DateUtil.parseDate("   ");
        assertNull(date);
    }

    @Test
    @DisplayName("Should parse date with whitespace")
    void testParseDate_WithWhitespace() {
        LocalDate date = DateUtil.parseDate("  2024-12-25  ");
        
        assertNotNull(date);
        assertEquals(2024, date.getYear());
    }

    @Test
    @DisplayName("Should parse valid datetime string")
    void testParseDateTime_Valid() {
        LocalDateTime dateTime = DateUtil.parseDateTime("2024-12-25 14:30");
        
        assertNotNull(dateTime);
        assertEquals(2024, dateTime.getYear());
        assertEquals(12, dateTime.getMonthValue());
        assertEquals(25, dateTime.getDayOfMonth());
        assertEquals(14, dateTime.getHour());
        assertEquals(30, dateTime.getMinute());
    }

    @Test
    @DisplayName("Should throw exception for invalid datetime format")
    void testParseDateTime_InvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            DateUtil.parseDateTime("2024-12-25");
        });
    }

    @Test
    @DisplayName("Should throw exception for invalid datetime")
    void testParseDateTime_InvalidDateTime() {
        assertThrows(IllegalArgumentException.class, () -> {
            DateUtil.parseDateTime("2024-13-45 25:70");
        });
    }

    @Test
    @DisplayName("Should return null for null datetime string")
    void testParseDateTime_Null() {
        LocalDateTime dateTime = DateUtil.parseDateTime(null);
        assertNull(dateTime);
    }

    @Test
    @DisplayName("Should return null for empty datetime string")
    void testParseDateTime_Empty() {
        LocalDateTime dateTime = DateUtil.parseDateTime("");
        assertNull(dateTime);
    }

    @Test
    @DisplayName("Should format date correctly")
    void testFormatDate_Valid() {
        LocalDate date = LocalDate.of(2024, 12, 25);
        String formatted = DateUtil.formatDate(date);
        
        assertEquals("2024-12-25", formatted);
    }

    @Test
    @DisplayName("Should return empty string for null date")
    void testFormatDate_Null() {
        String formatted = DateUtil.formatDate(null);
        assertEquals("", formatted);
    }

    @Test
    @DisplayName("Should format datetime correctly")
    void testFormatDateTime_Valid() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 25, 14, 30);
        String formatted = DateUtil.formatDateTime(dateTime);
        
        assertEquals("2024-12-25 14:30", formatted);
    }

    @Test
    @DisplayName("Should return empty string for null datetime")
    void testFormatDateTime_Null() {
        String formatted = DateUtil.formatDateTime(null);
        assertEquals("", formatted);
    }

    @Test
    @DisplayName("Should validate correct date format")
    void testIsValidDate_Valid() {
        assertTrue(DateUtil.isValidDate("2024-12-25"));
        assertTrue(DateUtil.isValidDate("2000-01-01"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"25-12-2024", "2024/12/25", "invalid", "2024-13-01", ""})
    @DisplayName("Should return false for invalid date formats")
    void testIsValidDate_Invalid(String dateStr) {
        assertFalse(DateUtil.isValidDate(dateStr));
    }

    @Test
    @DisplayName("Should validate correct datetime format")
    void testIsValidDateTime_Valid() {
        assertTrue(DateUtil.isValidDateTime("2024-12-25 14:30"));
        assertTrue(DateUtil.isValidDateTime("2000-01-01 00:00"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-12-25", "25-12-2024 14:30", "invalid", "2024-13-01 25:70", ""})
    @DisplayName("Should return false for invalid datetime formats")
    void testIsValidDateTime_Invalid(String dateTimeStr) {
        assertFalse(DateUtil.isValidDateTime(dateTimeStr));
    }

    @Test
    @DisplayName("Should identify future date correctly")
    void testIsFutureDate_True() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertTrue(DateUtil.isFutureDate(futureDate));
    }

    @Test
    @DisplayName("Should identify past date correctly")
    void testIsFutureDate_False() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        assertFalse(DateUtil.isFutureDate(pastDate));
    }

    @Test
    @DisplayName("Should return false for null future date check")
    void testIsFutureDate_Null() {
        assertFalse(DateUtil.isFutureDate(null));
    }

    @Test
    @DisplayName("Should return false for today in future date check")
    void testIsFutureDate_Today() {
        LocalDate today = LocalDate.now();
        assertFalse(DateUtil.isFutureDate(today));
    }

    @Test
    @DisplayName("Should identify future datetime correctly")
    void testIsFutureDateTime_True() {
        LocalDateTime futureDateTime = LocalDateTime.now().plusHours(1);
        assertTrue(DateUtil.isFutureDateTime(futureDateTime));
    }

    @Test
    @DisplayName("Should identify past datetime correctly")
    void testIsFutureDateTime_False() {
        LocalDateTime pastDateTime = LocalDateTime.now().minusHours(1);
        assertFalse(DateUtil.isFutureDateTime(pastDateTime));
    }

    @Test
    @DisplayName("Should return false for null future datetime check")
    void testIsFutureDateTime_Null() {
        assertFalse(DateUtil.isFutureDateTime(null));
    }

    @Test
    @DisplayName("Should identify past date correctly")
    void testIsPastDate_True() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        assertTrue(DateUtil.isPastDate(pastDate));
    }

    @Test
    @DisplayName("Should identify future date as not past")
    void testIsPastDate_False() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertFalse(DateUtil.isPastDate(futureDate));
    }

    @Test
    @DisplayName("Should return false for null past date check")
    void testIsPastDate_Null() {
        assertFalse(DateUtil.isPastDate(null));
    }

    @Test
    @DisplayName("Should identify past datetime correctly")
    void testIsPastDateTime_True() {
        LocalDateTime pastDateTime = LocalDateTime.now().minusHours(1);
        assertTrue(DateUtil.isPastDateTime(pastDateTime));
    }

    @Test
    @DisplayName("Should identify future datetime as not past")
    void testIsPastDateTime_False() {
        LocalDateTime futureDateTime = LocalDateTime.now().plusHours(1);
        assertFalse(DateUtil.isPastDateTime(futureDateTime));
    }

    @Test
    @DisplayName("Should return false for null past datetime check")
    void testIsPastDateTime_Null() {
        assertFalse(DateUtil.isPastDateTime(null));
    }

    @Test
    @DisplayName("Should calculate age correctly")
    void testCalculateAge_Valid() {
        LocalDate birthDate = LocalDate.now().minusYears(30);
        int age = DateUtil.calculateAge(birthDate);
        
        assertEquals(30, age);
    }

    @Test
    @DisplayName("Should calculate age for person born today")
    void testCalculateAge_BornToday() {
        LocalDate birthDate = LocalDate.now();
        int age = DateUtil.calculateAge(birthDate);
        
        assertEquals(0, age);
    }

    @Test
    @DisplayName("Should calculate age when birthday hasn't occurred this year")
    void testCalculateAge_BeforeBirthday() {
        LocalDate today = LocalDate.now();
        LocalDate birthDate = LocalDate.of(today.getYear() - 30, today.getMonthValue(), today.getDayOfMonth()).plusDays(1);
        
        int age = DateUtil.calculateAge(birthDate);
        
        assertEquals(29, age);
    }

    @Test
    @DisplayName("Should calculate age when birthday has occurred this year")
    void testCalculateAge_AfterBirthday() {
        LocalDate today = LocalDate.now();
        LocalDate birthDate = LocalDate.of(today.getYear() - 30, today.getMonthValue(), today.getDayOfMonth()).minusDays(1);
        
        int age = DateUtil.calculateAge(birthDate);
        
        assertEquals(30, age);
    }

    @Test
    @DisplayName("Should return 0 for null birth date")
    void testCalculateAge_Null() {
        int age = DateUtil.calculateAge(null);
        assertEquals(0, age);
    }

    @Test
    @DisplayName("Should calculate age for leap year birth date")
    void testCalculateAge_LeapYear() {
        LocalDate birthDate = LocalDate.of(2000, 2, 29);
        int age = DateUtil.calculateAge(birthDate);
        
        assertTrue(age >= 24);
    }
}
