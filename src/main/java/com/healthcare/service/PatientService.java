package com.healthcare.service;

import com.healthcare.exception.InvalidDataException;
import com.healthcare.exception.PatientNotFoundException;
import com.healthcare.model.Patient;
import com.healthcare.repository.PatientRepository;
import com.healthcare.util.ValidationUtil;

import java.util.List;
import java.util.UUID;

public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient registerPatient(Patient patient) {
        validatePatient(patient);
        
        if (patient.getPatientId() == null || patient.getPatientId().isEmpty()) {
            patient.setPatientId(generatePatientId());
        }
        
        if (patientRepository.existsById(patient.getPatientId())) {
            throw new InvalidDataException("Patient with ID " + patient.getPatientId() + " already exists");
        }
        
        return patientRepository.save(patient);
    }

    public Patient getPatientById(String patientId) {
        ValidationUtil.validateNotEmpty(patientId, "Patient ID");
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + patientId));
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Patient> getPatientsByLastName(String lastName) {
        ValidationUtil.validateNotEmpty(lastName, "Last name");
        return patientRepository.findByLastName(lastName);
    }

    public List<Patient> getPatientsByBloodGroup(String bloodGroup) {
        ValidationUtil.validateBloodGroup(bloodGroup);
        return patientRepository.findByBloodGroup(bloodGroup);
    }

    public Patient updatePatient(String patientId, Patient updatedPatient) {
        ValidationUtil.validateNotEmpty(patientId, "Patient ID");
        Patient existingPatient = getPatientById(patientId);
        
        validatePatient(updatedPatient);
        
        updatedPatient.setPatientId(patientId);
        updatedPatient.setRegistrationDate(existingPatient.getRegistrationDate());
        
        return patientRepository.save(updatedPatient);
    }

    public void deletePatient(String patientId) {
        ValidationUtil.validateNotEmpty(patientId, "Patient ID");
        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Patient not found with ID: " + patientId);
        }
        patientRepository.deleteById(patientId);
    }

    public boolean patientExists(String patientId) {
        return patientId != null && patientRepository.existsById(patientId);
    }

    public long getTotalPatientCount() {
        return patientRepository.count();
    }

    private void validatePatient(Patient patient) {
        ValidationUtil.validateNotNull(patient, "Patient");
        ValidationUtil.validateNotEmpty(patient.getFirstName(), "First name");
        ValidationUtil.validateNotEmpty(patient.getLastName(), "Last name");
        ValidationUtil.validateNotNull(patient.getDateOfBirth(), "Date of birth");
        ValidationUtil.validateGender(patient.getGender());
        ValidationUtil.validatePhoneNumber(patient.getPhoneNumber());
        ValidationUtil.validateEmail(patient.getEmail());
        ValidationUtil.validateBloodGroup(patient.getBloodGroup());
        ValidationUtil.validateNotEmpty(patient.getAddress(), "Address");
    }

    private String generatePatientId() {
        return "PAT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
