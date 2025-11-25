package com.healthcare.repository;

import com.healthcare.model.Doctor;

import java.util.*;
import java.util.stream.Collectors;

public class DoctorRepository {
    private final Map<String, Doctor> doctors;

    public DoctorRepository() {
        this.doctors = new HashMap<>();
    }

    public Doctor save(Doctor doctor) {
        doctors.put(doctor.getDoctorId(), doctor);
        return doctor;
    }

    public Optional<Doctor> findById(String doctorId) {
        return Optional.ofNullable(doctors.get(doctorId));
    }

    public List<Doctor> findAll() {
        return new ArrayList<>(doctors.values());
    }

    public List<Doctor> findBySpecialization(String specialization) {
        return doctors.values().stream()
                .filter(d -> d.getSpecialization().equalsIgnoreCase(specialization))
                .collect(Collectors.toList());
    }

    public List<Doctor> findAvailableDoctors() {
        return doctors.values().stream()
                .filter(Doctor::isAvailable)
                .collect(Collectors.toList());
    }

    public boolean existsById(String doctorId) {
        return doctors.containsKey(doctorId);
    }

    public void deleteById(String doctorId) {
        doctors.remove(doctorId);
    }

    public long count() {
        return doctors.size();
    }

    public void clear() {
        doctors.clear();
    }
}
