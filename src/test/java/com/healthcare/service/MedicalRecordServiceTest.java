package com.healthcare.service;

import com.healthcare.exception.InvalidDataException;
import com.healthcare.model.Doctor;
import com.healthcare.model.MedicalRecord;
import com.healthcare.model.Patient;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.MedicalRecordRepository;
import com.healthcare.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedicalRecordServiceTest {

    private MedicalRecordService medicalRecordService;
    private PatientService patientService;
    private DoctorService doctorService;
    private String validPatientId;
    private String validDoctorId;

    @BeforeEach
    void setUp() {
        PatientRepository patientRepo = new PatientRepository();
        DoctorRepository doctorRepo = new DoctorRepository();
        MedicalRecordRepository recordRepo = new MedicalRecordRepository();
        
        patientService = new PatientService(patientRepo);
        doctorService = new DoctorService(doctorRepo);
        medicalRecordService = new MedicalRecordService(recordRepo, patientService, doctorService);
        
        // Create test patient and doctor
        Patient patient = new Patient(null, "John", "Doe", LocalDate.of(1990, 1, 1),
                                      "Male", "9876543210", "john@example.com",
                                      "123 Main St", "A+");
        validPatientId = patientService.registerPatient(patient).getPatientId();
        
        Doctor doctor = new Doctor(null, "Jane", "Smith", "Cardiology",
                                   "9876543211", "jane@hospital.com", 10, "MBBS, MD");
        validDoctorId = doctorService.registerDoctor(doctor).getDoctorId();
    }

    @Test
    @DisplayName("Should create medical record successfully")
    void testCreateMedicalRecord_Success() {
        MedicalRecord record = createValidMedicalRecord();
        
        MedicalRecord created = medicalRecordService.createMedicalRecord(record);
        
        assertNotNull(created);
        assertNotNull(created.getRecordId());
        assertEquals(validPatientId, created.getPatientId());
        assertEquals(validDoctorId, created.getDoctorId());
        assertEquals("Chest pain", created.getChiefComplaint());
        assertEquals("Hypertension", created.getDiagnosis());
    }

    @Test
    @DisplayName("Should generate record ID when not provided")
    void testCreateMedicalRecord_GeneratesId() {
        MedicalRecord record = createValidMedicalRecord();
        record.setRecordId(null);
        
        MedicalRecord created = medicalRecordService.createMedicalRecord(record);
        
        assertNotNull(created.getRecordId());
        assertTrue(created.getRecordId().startsWith("REC"));
    }

    @Test
    @DisplayName("Should throw exception when patient does not exist")
    void testCreateMedicalRecord_InvalidPatient() {
        MedicalRecord record = createValidMedicalRecord();
        record.setPatientId("INVALID_PATIENT");
        
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.createMedicalRecord(record);
        });
    }

    @Test
    @DisplayName("Should throw exception when doctor does not exist")
    void testCreateMedicalRecord_InvalidDoctor() {
        MedicalRecord record = createValidMedicalRecord();
        record.setDoctorId("INVALID_DOCTOR");
        
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.createMedicalRecord(record);
        });
    }

    @Test
    @DisplayName("Should throw exception when chief complaint is empty")
    void testCreateMedicalRecord_EmptyChiefComplaint() {
        MedicalRecord record = createValidMedicalRecord();
        record.setChiefComplaint("");
        
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.createMedicalRecord(record);
        });
    }

    @Test
    @DisplayName("Should throw exception when chief complaint is null")
    void testCreateMedicalRecord_NullChiefComplaint() {
        MedicalRecord record = createValidMedicalRecord();
        record.setChiefComplaint(null);
        
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.createMedicalRecord(record);
        });
    }

    @Test
    @DisplayName("Should throw exception when diagnosis is empty")
    void testCreateMedicalRecord_EmptyDiagnosis() {
        MedicalRecord record = createValidMedicalRecord();
        record.setDiagnosis("");
        
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.createMedicalRecord(record);
        });
    }

    @Test
    @DisplayName("Should throw exception when diagnosis is null")
    void testCreateMedicalRecord_NullDiagnosis() {
        MedicalRecord record = createValidMedicalRecord();
        record.setDiagnosis(null);
        
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.createMedicalRecord(record);
        });
    }

    @Test
    @DisplayName("Should throw exception when patient ID is empty")
    void testCreateMedicalRecord_EmptyPatientId() {
        MedicalRecord record = createValidMedicalRecord();
        record.setPatientId("");
        
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.createMedicalRecord(record);
        });
    }

    @Test
    @DisplayName("Should throw exception when doctor ID is empty")
    void testCreateMedicalRecord_EmptyDoctorId() {
        MedicalRecord record = createValidMedicalRecord();
        record.setDoctorId("");
        
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.createMedicalRecord(record);
        });
    }

    @Test
    @DisplayName("Should retrieve medical record by ID")
    void testGetMedicalRecordById_Success() {
        MedicalRecord record = createValidMedicalRecord();
        MedicalRecord created = medicalRecordService.createMedicalRecord(record);
        
        MedicalRecord retrieved = medicalRecordService.getMedicalRecordById(created.getRecordId());
        
        assertNotNull(retrieved);
        assertEquals(created.getRecordId(), retrieved.getRecordId());
    }

    @Test
    @DisplayName("Should throw exception when medical record ID not found")
    void testGetMedicalRecordById_NotFound() {
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.getMedicalRecordById("INVALID_ID");
        });
    }

    @Test
    @DisplayName("Should get all medical records")
    void testGetAllMedicalRecords() {
        medicalRecordService.createMedicalRecord(createValidMedicalRecord());
        medicalRecordService.createMedicalRecord(createValidMedicalRecord());
        
        List<MedicalRecord> records = medicalRecordService.getAllMedicalRecords();
        
        assertEquals(2, records.size());
    }

    @Test
    @DisplayName("Should get medical records by patient")
    void testGetMedicalRecordsByPatient() {
        medicalRecordService.createMedicalRecord(createValidMedicalRecord());
        medicalRecordService.createMedicalRecord(createValidMedicalRecord());
        
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByPatient(validPatientId);
        
        assertEquals(2, records.size());
        assertTrue(records.stream().allMatch(r -> r.getPatientId().equals(validPatientId)));
    }

    @Test
    @DisplayName("Should throw exception when getting records with empty patient ID")
    void testGetMedicalRecordsByPatient_EmptyId() {
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.getMedicalRecordsByPatient("");
        });
    }

    @Test
    @DisplayName("Should get medical records by doctor")
    void testGetMedicalRecordsByDoctor() {
        medicalRecordService.createMedicalRecord(createValidMedicalRecord());
        medicalRecordService.createMedicalRecord(createValidMedicalRecord());
        
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByDoctor(validDoctorId);
        
        assertEquals(2, records.size());
        assertTrue(records.stream().allMatch(r -> r.getDoctorId().equals(validDoctorId)));
    }

    @Test
    @DisplayName("Should throw exception when getting records with empty doctor ID")
    void testGetMedicalRecordsByDoctor_EmptyId() {
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.getMedicalRecordsByDoctor("");
        });
    }

    @Test
    @DisplayName("Should update medical record successfully")
    void testUpdateMedicalRecord_Success() {
        MedicalRecord record = createValidMedicalRecord();
        MedicalRecord created = medicalRecordService.createMedicalRecord(record);
        
        MedicalRecord updatedData = createValidMedicalRecord();
        updatedData.setDiagnosis("Updated Diagnosis");
        updatedData.setTreatment("Updated Treatment");
        
        MedicalRecord updated = medicalRecordService.updateMedicalRecord(created.getRecordId(), updatedData);
        
        assertEquals("Updated Diagnosis", updated.getDiagnosis());
        assertEquals("Updated Treatment", updated.getTreatment());
        assertEquals(created.getRecordId(), updated.getRecordId());
        assertEquals(created.getRecordDateTime(), updated.getRecordDateTime());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent record")
    void testUpdateMedicalRecord_NotFound() {
        MedicalRecord record = createValidMedicalRecord();
        
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.updateMedicalRecord("INVALID_ID", record);
        });
    }

    @Test
    @DisplayName("Should delete medical record successfully")
    void testDeleteMedicalRecord_Success() {
        MedicalRecord record = createValidMedicalRecord();
        MedicalRecord created = medicalRecordService.createMedicalRecord(record);
        
        medicalRecordService.deleteMedicalRecord(created.getRecordId());
        
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.getMedicalRecordById(created.getRecordId());
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent record")
    void testDeleteMedicalRecord_NotFound() {
        assertThrows(InvalidDataException.class, () -> {
            medicalRecordService.deleteMedicalRecord("INVALID_ID");
        });
    }

    @Test
    @DisplayName("Should return correct medical record count")
    void testGetTotalRecordCount() {
        assertEquals(0, medicalRecordService.getTotalRecordCount());
        
        medicalRecordService.createMedicalRecord(createValidMedicalRecord());
        assertEquals(1, medicalRecordService.getTotalRecordCount());
        
        medicalRecordService.createMedicalRecord(createValidMedicalRecord());
        assertEquals(2, medicalRecordService.getTotalRecordCount());
    }

    @Test
    @DisplayName("Should create medical record with optional fields")
    void testCreateMedicalRecord_WithOptionalFields() {
        MedicalRecord record = createValidMedicalRecord();
        record.setVitalSigns("BP: 120/80, Pulse: 72");
        record.setLabResults("Blood test: Normal");
        record.setNotes("Patient is stable");
        record.setFollowUpInstructions("Return in 2 weeks");
        
        MedicalRecord created = medicalRecordService.createMedicalRecord(record);
        
        assertNotNull(created.getVitalSigns());
        assertNotNull(created.getLabResults());
        assertNotNull(created.getNotes());
        assertNotNull(created.getFollowUpInstructions());
    }

    private MedicalRecord createValidMedicalRecord() {
        MedicalRecord record = new MedicalRecord(
            null,
            validPatientId,
            validDoctorId,
            null,
            "Chest pain",
            "Hypertension"
        );
        record.setTreatment("Prescribed medication");
        return record;
    }
}
