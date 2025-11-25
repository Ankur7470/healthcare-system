package com.healthcare.service;

import com.healthcare.exception.InvalidDataException;
import com.healthcare.exception.PatientNotFoundException;
import com.healthcare.model.Patient;
import com.healthcare.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatientServiceTest {

    private PatientService patientService;
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        patientRepository = new PatientRepository();
        patientService = new PatientService(patientRepository);
    }

    @Test
    @DisplayName("Should register a valid patient successfully")
    void testRegisterPatient_Success() {
        Patient patient = createValidPatient();
        
        Patient registered = patientService.registerPatient(patient);
        
        assertNotNull(registered);
        assertNotNull(registered.getPatientId());
        assertEquals("John", registered.getFirstName());
        assertEquals("Doe", registered.getLastName());
        assertEquals(1, patientService.getTotalPatientCount());
    }

    @Test
    @DisplayName("Should generate patient ID when not provided")
    void testRegisterPatient_GeneratesId() {
        Patient patient = createValidPatient();
        patient.setPatientId(null);
        
        Patient registered = patientService.registerPatient(patient);
        
        assertNotNull(registered.getPatientId());
        assertTrue(registered.getPatientId().startsWith("PAT"));
    }

    @Test
    @DisplayName("Should throw exception when registering patient with duplicate ID")
    void testRegisterPatient_DuplicateId() {
        Patient patient1 = createValidPatient();
        patient1.setPatientId("PAT001");
        patientService.registerPatient(patient1);
        
        Patient patient2 = createValidPatient();
        patient2.setPatientId("PAT001");
        
        assertThrows(InvalidDataException.class, () -> {
            patientService.registerPatient(patient2);
        });
    }

    @Test
    @DisplayName("Should throw exception when first name is null")
    void testRegisterPatient_NullFirstName() {
        Patient patient = createValidPatient();
        patient.setFirstName(null);
        
        assertThrows(InvalidDataException.class, () -> {
            patientService.registerPatient(patient);
        });
    }

    @Test
    @DisplayName("Should throw exception when first name is empty")
    void testRegisterPatient_EmptyFirstName() {
        Patient patient = createValidPatient();
        patient.setFirstName("");
        
        assertThrows(InvalidDataException.class, () -> {
            patientService.registerPatient(patient);
        });
    }

    @Test
    @DisplayName("Should throw exception when last name is null")
    void testRegisterPatient_NullLastName() {
        Patient patient = createValidPatient();
        patient.setLastName(null);
        
        assertThrows(InvalidDataException.class, () -> {
            patientService.registerPatient(patient);
        });
    }

    @Test
    @DisplayName("Should throw exception when date of birth is null")
    void testRegisterPatient_NullDateOfBirth() {
        Patient patient = createValidPatient();
        patient.setDateOfBirth(null);
        
        assertThrows(InvalidDataException.class, () -> {
            patientService.registerPatient(patient);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"InvalidGender", "M", "F", ""})
    @DisplayName("Should throw exception for invalid gender")
    void testRegisterPatient_InvalidGender(String gender) {
        Patient patient = createValidPatient();
        patient.setGender(gender);
        
        assertThrows(InvalidDataException.class, () -> {
            patientService.registerPatient(patient);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456789", "12345678901", "abcdefghij", "123-456-7890"})
    @DisplayName("Should throw exception for invalid phone number")
    void testRegisterPatient_InvalidPhoneNumber(String phone) {
        Patient patient = createValidPatient();
        patient.setPhoneNumber(phone);
        
        assertThrows(InvalidDataException.class, () -> {
            patientService.registerPatient(patient);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "@example.com", "user@", "user.example.com"})
    @DisplayName("Should throw exception for invalid email")
    void testRegisterPatient_InvalidEmail(String email) {
        Patient patient = createValidPatient();
        patient.setEmail(email);
        
        assertThrows(InvalidDataException.class, () -> {
            patientService.registerPatient(patient);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "B+C", "AB", "XY+", "O"})
    @DisplayName("Should throw exception for invalid blood group")
    void testRegisterPatient_InvalidBloodGroup(String bloodGroup) {
        Patient patient = createValidPatient();
        patient.setBloodGroup(bloodGroup);
        
        assertThrows(InvalidDataException.class, () -> {
            patientService.registerPatient(patient);
        });
    }

    @Test
    @DisplayName("Should throw exception when address is empty")
    void testRegisterPatient_EmptyAddress() {
        Patient patient = createValidPatient();
        patient.setAddress("");
        
        assertThrows(InvalidDataException.class, () -> {
            patientService.registerPatient(patient);
        });
    }

    @Test
    @DisplayName("Should retrieve patient by ID successfully")
    void testGetPatientById_Success() {
        Patient patient = createValidPatient();
        Patient registered = patientService.registerPatient(patient);
        
        Patient retrieved = patientService.getPatientById(registered.getPatientId());
        
        assertNotNull(retrieved);
        assertEquals(registered.getPatientId(), retrieved.getPatientId());
        assertEquals("John", retrieved.getFirstName());
    }

    @Test
    @DisplayName("Should throw exception when patient ID not found")
    void testGetPatientById_NotFound() {
        assertThrows(PatientNotFoundException.class, () -> {
            patientService.getPatientById("INVALID_ID");
        });
    }

    @Test
    @DisplayName("Should throw exception when patient ID is empty")
    void testGetPatientById_EmptyId() {
        assertThrows(InvalidDataException.class, () -> {
            patientService.getPatientById("");
        });
    }

    @Test
    @DisplayName("Should retrieve all patients successfully")
    void testGetAllPatients_Success() {
        patientService.registerPatient(createValidPatient());
        patientService.registerPatient(createAnotherValidPatient());
        
        List<Patient> patients = patientService.getAllPatients();
        
        assertEquals(2, patients.size());
    }

    @Test
    @DisplayName("Should return empty list when no patients exist")
    void testGetAllPatients_Empty() {
        List<Patient> patients = patientService.getAllPatients();
        
        assertTrue(patients.isEmpty());
    }

    @Test
    @DisplayName("Should find patients by last name")
    void testGetPatientsByLastName_Success() {
        Patient patient1 = createValidPatient();
        patient1.setLastName("Smith");
        patientService.registerPatient(patient1);
        
        Patient patient2 = createAnotherValidPatient();
        patient2.setLastName("Smith");
        patientService.registerPatient(patient2);
        
        Patient patient3 = createValidPatient();
        patient3.setLastName("Jones");
        patient3.setEmail("jones@example.com");
        patient3.setPhoneNumber("9876543211");
        patientService.registerPatient(patient3);
        
        List<Patient> smithPatients = patientService.getPatientsByLastName("Smith");
        
        assertEquals(2, smithPatients.size());
        assertTrue(smithPatients.stream().allMatch(p -> p.getLastName().equals("Smith")));
    }

    @Test
    @DisplayName("Should return empty list when no patients match last name")
    void testGetPatientsByLastName_NotFound() {
        patientService.registerPatient(createValidPatient());
        
        List<Patient> patients = patientService.getPatientsByLastName("Nonexistent");
        
        assertTrue(patients.isEmpty());
    }

    @Test
    @DisplayName("Should find patients by blood group")
    void testGetPatientsByBloodGroup_Success() {
        Patient patient1 = createValidPatient();
        patient1.setBloodGroup("A+");
        patientService.registerPatient(patient1);
        
        Patient patient2 = createAnotherValidPatient();
        patient2.setBloodGroup("A+");
        patientService.registerPatient(patient2);
        
        Patient patient3 = createValidPatient();
        patient3.setBloodGroup("O+");
        patient3.setEmail("other@example.com");
        patient3.setPhoneNumber("9876543211");
        patientService.registerPatient(patient3);
        
        List<Patient> aPositivePatients = patientService.getPatientsByBloodGroup("A+");
        
        assertEquals(2, aPositivePatients.size());
        assertTrue(aPositivePatients.stream().allMatch(p -> p.getBloodGroup().equals("A+")));
    }

    @Test
    @DisplayName("Should throw exception for invalid blood group search")
    void testGetPatientsByBloodGroup_Invalid() {
        assertThrows(InvalidDataException.class, () -> {
            patientService.getPatientsByBloodGroup("INVALID");
        });
    }

    @Test
    @DisplayName("Should update patient successfully")
    void testUpdatePatient_Success() {
        Patient patient = createValidPatient();
        Patient registered = patientService.registerPatient(patient);
        
        Patient updatedData = createValidPatient();
        updatedData.setFirstName("Jane");
        updatedData.setPhoneNumber("9999999999");
        
        Patient updated = patientService.updatePatient(registered.getPatientId(), updatedData);
        
        assertEquals("Jane", updated.getFirstName());
        assertEquals("9999999999", updated.getPhoneNumber());
        assertEquals(registered.getPatientId(), updated.getPatientId());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent patient")
    void testUpdatePatient_NotFound() {
        Patient patient = createValidPatient();
        
        assertThrows(PatientNotFoundException.class, () -> {
            patientService.updatePatient("INVALID_ID", patient);
        });
    }

    @Test
    @DisplayName("Should delete patient successfully")
    void testDeletePatient_Success() {
        Patient patient = createValidPatient();
        Patient registered = patientService.registerPatient(patient);
        
        patientService.deletePatient(registered.getPatientId());
        
        assertThrows(PatientNotFoundException.class, () -> {
            patientService.getPatientById(registered.getPatientId());
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent patient")
    void testDeletePatient_NotFound() {
        assertThrows(PatientNotFoundException.class, () -> {
            patientService.deletePatient("INVALID_ID");
        });
    }

    @Test
    @DisplayName("Should check if patient exists")
    void testPatientExists_True() {
        Patient patient = createValidPatient();
        Patient registered = patientService.registerPatient(patient);
        
        assertTrue(patientService.patientExists(registered.getPatientId()));
    }

    @Test
    @DisplayName("Should return false when patient does not exist")
    void testPatientExists_False() {
        assertFalse(patientService.patientExists("INVALID_ID"));
    }

    @Test
    @DisplayName("Should return false when patient ID is null")
    void testPatientExists_Null() {
        assertFalse(patientService.patientExists(null));
    }

    @Test
    @DisplayName("Should return correct patient count")
    void testGetTotalPatientCount() {
        assertEquals(0, patientService.getTotalPatientCount());
        
        patientService.registerPatient(createValidPatient());
        assertEquals(1, patientService.getTotalPatientCount());
        
        patientService.registerPatient(createAnotherValidPatient());
        assertEquals(2, patientService.getTotalPatientCount());
    }

    private Patient createValidPatient() {
        return new Patient(
            null,
            "John",
            "Doe",
            LocalDate.of(1990, 1, 1),
            "Male",
            "9876543210",
            "john.doe@example.com",
            "123 Main St, City",
            "A+"
        );
    }

    private Patient createAnotherValidPatient() {
        return new Patient(
            null,
            "Jane",
            "Smith",
            LocalDate.of(1985, 5, 15),
            "Female",
            "9876543220",
            "jane.smith@example.com",
            "456 Oak Ave, Town",
            "B+"
        );
    }
}
