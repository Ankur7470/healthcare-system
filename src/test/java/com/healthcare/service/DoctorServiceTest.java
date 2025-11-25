package com.healthcare.service;

import com.healthcare.exception.DoctorNotFoundException;
import com.healthcare.exception.InvalidDataException;
import com.healthcare.model.Doctor;
import com.healthcare.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DoctorServiceTest {

    private DoctorService doctorService;
    private DoctorRepository doctorRepository;

    @BeforeEach
    void setUp() {
        doctorRepository = new DoctorRepository();
        doctorService = new DoctorService(doctorRepository);
    }

    @Test
    @DisplayName("Should register a valid doctor successfully")
    void testRegisterDoctor_Success() {
        Doctor doctor = createValidDoctor();
        
        Doctor registered = doctorService.registerDoctor(doctor);
        
        assertNotNull(registered);
        assertNotNull(registered.getDoctorId());
        assertEquals("John", registered.getFirstName());
        assertEquals("Smith", registered.getLastName());
        assertEquals(1, doctorService.getTotalDoctorCount());
    }

    @Test
    @DisplayName("Should generate doctor ID when not provided")
    void testRegisterDoctor_GeneratesId() {
        Doctor doctor = createValidDoctor();
        doctor.setDoctorId(null);
        
        Doctor registered = doctorService.registerDoctor(doctor);
        
        assertNotNull(registered.getDoctorId());
        assertTrue(registered.getDoctorId().startsWith("DOC"));
    }

    @Test
    @DisplayName("Should throw exception when registering doctor with duplicate ID")
    void testRegisterDoctor_DuplicateId() {
        Doctor doctor1 = createValidDoctor();
        doctor1.setDoctorId("DOC001");
        doctorService.registerDoctor(doctor1);
        
        Doctor doctor2 = createValidDoctor();
        doctor2.setDoctorId("DOC001");
        
        assertThrows(InvalidDataException.class, () -> {
            doctorService.registerDoctor(doctor2);
        });
    }

    @Test
    @DisplayName("Should throw exception when first name is null")
    void testRegisterDoctor_NullFirstName() {
        Doctor doctor = createValidDoctor();
        doctor.setFirstName(null);
        
        assertThrows(InvalidDataException.class, () -> {
            doctorService.registerDoctor(doctor);
        });
    }

    @Test
    @DisplayName("Should throw exception when first name is empty")
    void testRegisterDoctor_EmptyFirstName() {
        Doctor doctor = createValidDoctor();
        doctor.setFirstName("");
        
        assertThrows(InvalidDataException.class, () -> {
            doctorService.registerDoctor(doctor);
        });
    }

    @Test
    @DisplayName("Should throw exception when last name is null")
    void testRegisterDoctor_NullLastName() {
        Doctor doctor = createValidDoctor();
        doctor.setLastName(null);
        
        assertThrows(InvalidDataException.class, () -> {
            doctorService.registerDoctor(doctor);
        });
    }

    @Test
    @DisplayName("Should throw exception when specialization is null")
    void testRegisterDoctor_NullSpecialization() {
        Doctor doctor = createValidDoctor();
        doctor.setSpecialization(null);
        
        assertThrows(InvalidDataException.class, () -> {
            doctorService.registerDoctor(doctor);
        });
    }

    @Test
    @DisplayName("Should throw exception when specialization is empty")
    void testRegisterDoctor_EmptySpecialization() {
        Doctor doctor = createValidDoctor();
        doctor.setSpecialization("");
        
        assertThrows(InvalidDataException.class, () -> {
            doctorService.registerDoctor(doctor);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456789", "12345678901", "abcdefghij", "123-456-7890"})
    @DisplayName("Should throw exception for invalid phone number")
    void testRegisterDoctor_InvalidPhoneNumber(String phone) {
        Doctor doctor = createValidDoctor();
        doctor.setPhoneNumber(phone);
        
        assertThrows(InvalidDataException.class, () -> {
            doctorService.registerDoctor(doctor);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "@example.com", "user@", "user.example.com"})
    @DisplayName("Should throw exception for invalid email")
    void testRegisterDoctor_InvalidEmail(String email) {
        Doctor doctor = createValidDoctor();
        doctor.setEmail(email);
        
        assertThrows(InvalidDataException.class, () -> {
            doctorService.registerDoctor(doctor);
        });
    }

    @Test
    @DisplayName("Should throw exception for negative years of experience")
    void testRegisterDoctor_NegativeExperience() {
        Doctor doctor = createValidDoctor();
        doctor.setYearsOfExperience(-1);
        
        assertThrows(InvalidDataException.class, () -> {
            doctorService.registerDoctor(doctor);
        });
    }

    @Test
    @DisplayName("Should accept zero years of experience")
    void testRegisterDoctor_ZeroExperience() {
        Doctor doctor = createValidDoctor();
        doctor.setYearsOfExperience(0);
        
        assertDoesNotThrow(() -> {
            doctorService.registerDoctor(doctor);
        });
    }

    @Test
    @DisplayName("Should throw exception when qualification is empty")
    void testRegisterDoctor_EmptyQualification() {
        Doctor doctor = createValidDoctor();
        doctor.setQualification("");
        
        assertThrows(InvalidDataException.class, () -> {
            doctorService.registerDoctor(doctor);
        });
    }

    @Test
    @DisplayName("Should retrieve doctor by ID successfully")
    void testGetDoctorById_Success() {
        Doctor doctor = createValidDoctor();
        Doctor registered = doctorService.registerDoctor(doctor);
        
        Doctor retrieved = doctorService.getDoctorById(registered.getDoctorId());
        
        assertNotNull(retrieved);
        assertEquals(registered.getDoctorId(), retrieved.getDoctorId());
        assertEquals("John", retrieved.getFirstName());
    }

    @Test
    @DisplayName("Should throw exception when doctor ID not found")
    void testGetDoctorById_NotFound() {
        assertThrows(DoctorNotFoundException.class, () -> {
            doctorService.getDoctorById("INVALID_ID");
        });
    }

    @Test
    @DisplayName("Should throw exception when doctor ID is empty")
    void testGetDoctorById_EmptyId() {
        assertThrows(InvalidDataException.class, () -> {
            doctorService.getDoctorById("");
        });
    }

    @Test
    @DisplayName("Should retrieve all doctors successfully")
    void testGetAllDoctors_Success() {
        doctorService.registerDoctor(createValidDoctor());
        doctorService.registerDoctor(createAnotherValidDoctor());
        
        List<Doctor> doctors = doctorService.getAllDoctors();
        
        assertEquals(2, doctors.size());
    }

    @Test
    @DisplayName("Should return empty list when no doctors exist")
    void testGetAllDoctors_Empty() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        
        assertTrue(doctors.isEmpty());
    }

    @Test
    @DisplayName("Should find doctors by specialization")
    void testGetDoctorsBySpecialization_Success() {
        Doctor doctor1 = createValidDoctor();
        doctor1.setSpecialization("Cardiology");
        doctorService.registerDoctor(doctor1);
        
        Doctor doctor2 = createAnotherValidDoctor();
        doctor2.setSpecialization("Cardiology");
        doctorService.registerDoctor(doctor2);
        
        Doctor doctor3 = createValidDoctor();
        doctor3.setSpecialization("Neurology");
        doctor3.setEmail("neuro@example.com");
        doctor3.setPhoneNumber("9876543212");
        doctorService.registerDoctor(doctor3);
        
        List<Doctor> cardiologists = doctorService.getDoctorsBySpecialization("Cardiology");
        
        assertEquals(2, cardiologists.size());
        assertTrue(cardiologists.stream().allMatch(d -> d.getSpecialization().equals("Cardiology")));
    }

    @Test
    @DisplayName("Should return empty list when no doctors match specialization")
    void testGetDoctorsBySpecialization_NotFound() {
        doctorService.registerDoctor(createValidDoctor());
        
        List<Doctor> doctors = doctorService.getDoctorsBySpecialization("Nonexistent");
        
        assertTrue(doctors.isEmpty());
    }

    @Test
    @DisplayName("Should get available doctors only")
    void testGetAvailableDoctors_Success() {
        Doctor doctor1 = createValidDoctor();
        doctor1.setAvailable(true);
        Doctor registered1 = doctorService.registerDoctor(doctor1);
        
        Doctor doctor2 = createAnotherValidDoctor();
        doctor2.setAvailable(true);
        Doctor registered2 = doctorService.registerDoctor(doctor2);
        
        Doctor doctor3 = createValidDoctor();
        doctor3.setEmail("unavailable@example.com");
        doctor3.setPhoneNumber("9876543212");
        Doctor registered3 = doctorService.registerDoctor(doctor3);
        doctorService.setDoctorAvailability(registered3.getDoctorId(), false);
        
        List<Doctor> availableDoctors = doctorService.getAvailableDoctors();
        
        assertEquals(2, availableDoctors.size());
        assertTrue(availableDoctors.stream().allMatch(Doctor::isAvailable));
    }

    @Test
    @DisplayName("Should update doctor successfully")
    void testUpdateDoctor_Success() {
        Doctor doctor = createValidDoctor();
        Doctor registered = doctorService.registerDoctor(doctor);
        
        Doctor updatedData = createValidDoctor();
        updatedData.setFirstName("Jane");
        updatedData.setPhoneNumber("9999999999");
        
        Doctor updated = doctorService.updateDoctor(registered.getDoctorId(), updatedData);
        
        assertEquals("Jane", updated.getFirstName());
        assertEquals("9999999999", updated.getPhoneNumber());
        assertEquals(registered.getDoctorId(), updated.getDoctorId());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent doctor")
    void testUpdateDoctor_NotFound() {
        Doctor doctor = createValidDoctor();
        
        assertThrows(DoctorNotFoundException.class, () -> {
            doctorService.updateDoctor("INVALID_ID", doctor);
        });
    }

    @Test
    @DisplayName("Should set doctor availability to false")
    void testSetDoctorAvailability_False() {
        Doctor doctor = createValidDoctor();
        Doctor registered = doctorService.registerDoctor(doctor);
        
        doctorService.setDoctorAvailability(registered.getDoctorId(), false);
        
        Doctor updated = doctorService.getDoctorById(registered.getDoctorId());
        assertFalse(updated.isAvailable());
    }

    @Test
    @DisplayName("Should set doctor availability to true")
    void testSetDoctorAvailability_True() {
        Doctor doctor = createValidDoctor();
        doctor.setAvailable(false);
        Doctor registered = doctorService.registerDoctor(doctor);
        
        doctorService.setDoctorAvailability(registered.getDoctorId(), true);
        
        Doctor updated = doctorService.getDoctorById(registered.getDoctorId());
        assertTrue(updated.isAvailable());
    }

    @Test
    @DisplayName("Should delete doctor successfully")
    void testDeleteDoctor_Success() {
        Doctor doctor = createValidDoctor();
        Doctor registered = doctorService.registerDoctor(doctor);
        
        doctorService.deleteDoctor(registered.getDoctorId());
        
        assertThrows(DoctorNotFoundException.class, () -> {
            doctorService.getDoctorById(registered.getDoctorId());
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent doctor")
    void testDeleteDoctor_NotFound() {
        assertThrows(DoctorNotFoundException.class, () -> {
            doctorService.deleteDoctor("INVALID_ID");
        });
    }

    @Test
    @DisplayName("Should check if doctor exists")
    void testDoctorExists_True() {
        Doctor doctor = createValidDoctor();
        Doctor registered = doctorService.registerDoctor(doctor);
        
        assertTrue(doctorService.doctorExists(registered.getDoctorId()));
    }

    @Test
    @DisplayName("Should return false when doctor does not exist")
    void testDoctorExists_False() {
        assertFalse(doctorService.doctorExists("INVALID_ID"));
    }

    @Test
    @DisplayName("Should return false when doctor ID is null")
    void testDoctorExists_Null() {
        assertFalse(doctorService.doctorExists(null));
    }

    @Test
    @DisplayName("Should return correct doctor count")
    void testGetTotalDoctorCount() {
        assertEquals(0, doctorService.getTotalDoctorCount());
        
        doctorService.registerDoctor(createValidDoctor());
        assertEquals(1, doctorService.getTotalDoctorCount());
        
        doctorService.registerDoctor(createAnotherValidDoctor());
        assertEquals(2, doctorService.getTotalDoctorCount());
    }

    private Doctor createValidDoctor() {
        return new Doctor(
            null,
            "John",
            "Smith",
            "Cardiology",
            "9876543210",
            "john.smith@hospital.com",
            15,
            "MBBS, MD"
        );
    }

    private Doctor createAnotherValidDoctor() {
        return new Doctor(
            null,
            "Sarah",
            "Johnson",
            "Pediatrics",
            "9876543211",
            "sarah.johnson@hospital.com",
            10,
            "MBBS, DCH"
        );
    }
}
