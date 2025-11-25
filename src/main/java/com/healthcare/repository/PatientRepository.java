package com.healthcare.repository;

import com.healthcare.model.Patient;

import java.util.*;
import java.util.stream.Collectors;

public class PatientRepository {
    private final Map<String, Patient> patients;

    public PatientRepository() {
        this.patients = new HashMap<>();
    }

    public Patient save(Patient patient) {
        patients.put(patient.getPatientId(), patient);
        return patient;
    }

    public Optional<Patient> findById(String patientId) {
        return Optional.ofNullable(patients.get(patientId));
    }

    public List<Patient> findAll() {
        return new ArrayList<>(patients.values());
    }

    public List<Patient> findByLastName(String lastName) {
        return patients.values().stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    public List<Patient> findByBloodGroup(String bloodGroup) {
        return patients.values().stream()
                .filter(p -> p.getBloodGroup().equalsIgnoreCase(bloodGroup))
                .collect(Collectors.toList());
    }

    public boolean existsById(String patientId) {
        return patients.containsKey(patientId);
    }

    public void deleteById(String patientId) {
        patients.remove(patientId);
    }

    public long count() {
        return patients.size();
    }

    public void clear() {
        patients.clear();
    }
}
