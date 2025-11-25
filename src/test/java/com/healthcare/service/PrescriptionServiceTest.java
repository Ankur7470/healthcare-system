package com.healthcare.service;

import com.healthcare.exception.InvalidDataException;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.model.Prescription;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionServiceTest {

    private PrescriptionService prescriptionService;
    private PatientService patientService;
    private DoctorService doctorService;
    private String validPatientId;
    private String validDoctorId;

    @BeforeEach
    void setUp() {
        PatientRepository patientRepo = new PatientRepository();
        DoctorRepository doctorRepo = new DoctorRepository();
        PrescriptionRepository prescriptionRepo = new PrescriptionRepository();
        
        patientService = new PatientService(patientRepo);
        doctorService = new DoctorService(doctorRepo);
        prescriptionService = new PrescriptionService(prescriptionRepo, patientService, doctorService);
        
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
    @DisplayName("Should create prescription successfully")
    void testCreatePrescription_Success() {
        Prescription prescription = createValidPrescription();
        
        Prescription created = prescriptionService.createPrescription(prescription);
        
        assertNotNull(created);
        assertNotNull(created.getPrescriptionId());
        assertEquals(validPatientId, created.getPatientId());
        assertEquals(validDoctorId, created.getDoctorId());
        assertEquals("Hypertension", created.getDiagnosis());
    }

    @Test
    @DisplayName("Should generate prescription ID when not provided")
    void testCreatePrescription_GeneratesId() {
        Prescription prescription = createValidPrescription();
        prescription.setPrescriptionId(null);
        
        Prescription created = prescriptionService.createPrescription(prescription);
        
        assertNotNull(created.getPrescriptionId());
        assertTrue(created.getPrescriptionId().startsWith("PRE"));
    }

    @Test
    @DisplayName("Should throw exception when patient does not exist")
    void testCreatePrescription_InvalidPatient() {
        Prescription prescription = createValidPrescription();
        prescription.setPatientId("INVALID_PATIENT");
        
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.createPrescription(prescription);
        });
    }

    @Test
    @DisplayName("Should throw exception when doctor does not exist")
    void testCreatePrescription_InvalidDoctor() {
        Prescription prescription = createValidPrescription();
        prescription.setDoctorId("INVALID_DOCTOR");
        
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.createPrescription(prescription);
        });
    }

    @Test
    @DisplayName("Should throw exception when diagnosis is empty")
    void testCreatePrescription_EmptyDiagnosis() {
        Prescription prescription = createValidPrescription();
        prescription.setDiagnosis("");
        
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.createPrescription(prescription);
        });
    }

    @Test
    @DisplayName("Should throw exception when validity days is zero")
    void testCreatePrescription_ZeroValidity() {
        Prescription prescription = createValidPrescription();
        prescription.setValidityDays(0);
        
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.createPrescription(prescription);
        });
    }

    @Test
    @DisplayName("Should throw exception when validity days is negative")
    void testCreatePrescription_NegativeValidity() {
        Prescription prescription = createValidPrescription();
        prescription.setValidityDays(-5);
        
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.createPrescription(prescription);
        });
    }

    @Test
    @DisplayName("Should retrieve prescription by ID")
    void testGetPrescriptionById_Success() {
        Prescription prescription = createValidPrescription();
        Prescription created = prescriptionService.createPrescription(prescription);
        
        Prescription retrieved = prescriptionService.getPrescriptionById(created.getPrescriptionId());
        
        assertNotNull(retrieved);
        assertEquals(created.getPrescriptionId(), retrieved.getPrescriptionId());
    }

    @Test
    @DisplayName("Should throw exception when prescription ID not found")
    void testGetPrescriptionById_NotFound() {
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.getPrescriptionById("INVALID_ID");
        });
    }

    @Test
    @DisplayName("Should get all prescriptions")
    void testGetAllPrescriptions() {
        prescriptionService.createPrescription(createValidPrescription());
        prescriptionService.createPrescription(createValidPrescription());
        
        List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
        
        assertEquals(2, prescriptions.size());
    }

    @Test
    @DisplayName("Should get prescriptions by patient")
    void testGetPrescriptionsByPatient() {
        prescriptionService.createPrescription(createValidPrescription());
        prescriptionService.createPrescription(createValidPrescription());
        
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatient(validPatientId);
        
        assertEquals(2, prescriptions.size());
        assertTrue(prescriptions.stream().allMatch(p -> p.getPatientId().equals(validPatientId)));
    }

    @Test
    @DisplayName("Should get prescriptions by doctor")
    void testGetPrescriptionsByDoctor() {
        prescriptionService.createPrescription(createValidPrescription());
        prescriptionService.createPrescription(createValidPrescription());
        
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByDoctor(validDoctorId);
        
        assertEquals(2, prescriptions.size());
        assertTrue(prescriptions.stream().allMatch(p -> p.getDoctorId().equals(validDoctorId)));
    }

    @Test
    @DisplayName("Should get valid prescriptions only")
    void testGetValidPrescriptions() {
        Prescription validPrescription = createValidPrescription();
        validPrescription.setValidityDays(30);
        prescriptionService.createPrescription(validPrescription);
        
        List<Prescription> validPrescriptions = prescriptionService.getValidPrescriptions();
        
        assertEquals(1, validPrescriptions.size());
        assertTrue(validPrescriptions.get(0).isValid());
    }

    @Test
    @DisplayName("Should add medication to prescription")
    void testAddMedication_Success() {
        Prescription prescription = createValidPrescription();
        Prescription created = prescriptionService.createPrescription(prescription);
        
        Prescription.Medication medication = new Prescription.Medication(
            "Aspirin", "100mg", "Once daily", 30
        );
        
        Prescription updated = prescriptionService.addMedication(created.getPrescriptionId(), medication);
        
        assertEquals(1, updated.getMedications().size());
        assertEquals("Aspirin", updated.getMedications().get(0).getMedicineName());
    }

    @Test
    @DisplayName("Should throw exception when adding medication with empty name")
    void testAddMedication_EmptyName() {
        Prescription prescription = createValidPrescription();
        Prescription created = prescriptionService.createPrescription(prescription);
        
        Prescription.Medication medication = new Prescription.Medication(
            "", "100mg", "Once daily", 30
        );
        
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.addMedication(created.getPrescriptionId(), medication);
        });
    }

    @Test
    @DisplayName("Should throw exception when adding medication with empty dosage")
    void testAddMedication_EmptyDosage() {
        Prescription prescription = createValidPrescription();
        Prescription created = prescriptionService.createPrescription(prescription);
        
        Prescription.Medication medication = new Prescription.Medication(
            "Aspirin", "", "Once daily", 30
        );
        
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.addMedication(created.getPrescriptionId(), medication);
        });
    }

    @Test
    @DisplayName("Should throw exception when adding medication with zero duration")
    void testAddMedication_ZeroDuration() {
        Prescription prescription = createValidPrescription();
        Prescription created = prescriptionService.createPrescription(prescription);
        
        Prescription.Medication medication = new Prescription.Medication(
            "Aspirin", "100mg", "Once daily", 0
        );
        
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.addMedication(created.getPrescriptionId(), medication);
        });
    }

    @Test
    @DisplayName("Should delete prescription successfully")
    void testDeletePrescription_Success() {
        Prescription prescription = createValidPrescription();
        Prescription created = prescriptionService.createPrescription(prescription);
        
        prescriptionService.deletePrescription(created.getPrescriptionId());
        
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.getPrescriptionById(created.getPrescriptionId());
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent prescription")
    void testDeletePrescription_NotFound() {
        assertThrows(InvalidDataException.class, () -> {
            prescriptionService.deletePrescription("INVALID_ID");
        });
    }

    @Test
    @DisplayName("Should return correct prescription count")
    void testGetTotalPrescriptionCount() {
        assertEquals(0, prescriptionService.getTotalPrescriptionCount());
        
        prescriptionService.createPrescription(createValidPrescription());
        assertEquals(1, prescriptionService.getTotalPrescriptionCount());
        
        prescriptionService.createPrescription(createValidPrescription());
        assertEquals(2, prescriptionService.getTotalPrescriptionCount());
    }

    private Prescription createValidPrescription() {
        return new Prescription(
            null,
            validPatientId,
            validDoctorId,
            null,
            "Hypertension"
        );
    }
}
