package com.healthcare.repository;

import com.healthcare.model.Prescription;

import java.util.*;
import java.util.stream.Collectors;

public class PrescriptionRepository {
    private final Map<String, Prescription> prescriptions;

    public PrescriptionRepository() {
        this.prescriptions = new HashMap<>();
    }

    public Prescription save(Prescription prescription) {
        prescriptions.put(prescription.getPrescriptionId(), prescription);
        return prescription;
    }

    public Optional<Prescription> findById(String prescriptionId) {
        return Optional.ofNullable(prescriptions.get(prescriptionId));
    }

    public List<Prescription> findAll() {
        return new ArrayList<>(prescriptions.values());
    }

    public List<Prescription> findByPatientId(String patientId) {
        return prescriptions.values().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public List<Prescription> findByDoctorId(String doctorId) {
        return prescriptions.values().stream()
                .filter(p -> p.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    public List<Prescription> findValidPrescriptions() {
        return prescriptions.values().stream()
                .filter(Prescription::isValid)
                .collect(Collectors.toList());
    }

    public boolean existsById(String prescriptionId) {
        return prescriptions.containsKey(prescriptionId);
    }

    public void deleteById(String prescriptionId) {
        prescriptions.remove(prescriptionId);
    }

    public long count() {
        return prescriptions.size();
    }

    public void clear() {
        prescriptions.clear();
    }
}
