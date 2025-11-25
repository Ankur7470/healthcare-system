package com.healthcare.service;

import com.healthcare.exception.InvalidDataException;
import com.healthcare.model.Prescription;
import com.healthcare.repository.PrescriptionRepository;
import com.healthcare.util.ValidationUtil;

import java.util.List;
import java.util.UUID;

public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, 
                               PatientService patientService, 
                               DoctorService doctorService) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    public Prescription createPrescription(Prescription prescription) {
        validatePrescription(prescription);
        
        if (!patientService.patientExists(prescription.getPatientId())) {
            throw new InvalidDataException("Patient not found with ID: " + prescription.getPatientId());
        }
        
        if (!doctorService.doctorExists(prescription.getDoctorId())) {
            throw new InvalidDataException("Doctor not found with ID: " + prescription.getDoctorId());
        }
        
        if (prescription.getPrescriptionId() == null || prescription.getPrescriptionId().isEmpty()) {
            prescription.setPrescriptionId(generatePrescriptionId());
        }
        
        return prescriptionRepository.save(prescription);
    }

    public Prescription getPrescriptionById(String prescriptionId) {
        ValidationUtil.validateNotEmpty(prescriptionId, "Prescription ID");
        return prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new InvalidDataException("Prescription not found with ID: " + prescriptionId));
    }

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    public List<Prescription> getPrescriptionsByPatient(String patientId) {
        ValidationUtil.validateNotEmpty(patientId, "Patient ID");
        return prescriptionRepository.findByPatientId(patientId);
    }

    public List<Prescription> getPrescriptionsByDoctor(String doctorId) {
        ValidationUtil.validateNotEmpty(doctorId, "Doctor ID");
        return prescriptionRepository.findByDoctorId(doctorId);
    }

    public List<Prescription> getValidPrescriptions() {
        return prescriptionRepository.findValidPrescriptions();
    }

    public Prescription addMedication(String prescriptionId, Prescription.Medication medication) {
        Prescription prescription = getPrescriptionById(prescriptionId);
        validateMedication(medication);
        prescription.addMedication(medication);
        return prescriptionRepository.save(prescription);
    }

    public void deletePrescription(String prescriptionId) {
        ValidationUtil.validateNotEmpty(prescriptionId, "Prescription ID");
        if (!prescriptionRepository.existsById(prescriptionId)) {
            throw new InvalidDataException("Prescription not found with ID: " + prescriptionId);
        }
        prescriptionRepository.deleteById(prescriptionId);
    }

    public long getTotalPrescriptionCount() {
        return prescriptionRepository.count();
    }

    private void validatePrescription(Prescription prescription) {
        ValidationUtil.validateNotNull(prescription, "Prescription");
        ValidationUtil.validateNotEmpty(prescription.getPatientId(), "Patient ID");
        ValidationUtil.validateNotEmpty(prescription.getDoctorId(), "Doctor ID");
        ValidationUtil.validateNotEmpty(prescription.getDiagnosis(), "Diagnosis");
        ValidationUtil.validatePositiveNumber(prescription.getValidityDays(), "Validity days");
    }

    private void validateMedication(Prescription.Medication medication) {
        ValidationUtil.validateNotNull(medication, "Medication");
        ValidationUtil.validateNotEmpty(medication.getMedicineName(), "Medicine name");
        ValidationUtil.validateNotEmpty(medication.getDosage(), "Dosage");
        ValidationUtil.validateNotEmpty(medication.getFrequency(), "Frequency");
        ValidationUtil.validatePositiveNumber(medication.getDurationDays(), "Duration days");
    }

    private String generatePrescriptionId() {
        return "PRE" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
