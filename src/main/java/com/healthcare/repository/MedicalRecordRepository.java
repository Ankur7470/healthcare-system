package com.healthcare.repository;

import com.healthcare.model.MedicalRecord;

import java.util.*;
import java.util.stream.Collectors;

public class MedicalRecordRepository {
    private final Map<String, MedicalRecord> records;

    public MedicalRecordRepository() {
        this.records = new HashMap<>();
    }

    public MedicalRecord save(MedicalRecord record) {
        records.put(record.getRecordId(), record);
        return record;
    }

    public Optional<MedicalRecord> findById(String recordId) {
        return Optional.ofNullable(records.get(recordId));
    }

    public List<MedicalRecord> findAll() {
        return new ArrayList<>(records.values());
    }

    public List<MedicalRecord> findByPatientId(String patientId) {
        return records.values().stream()
                .filter(r -> r.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(MedicalRecord::getRecordDateTime).reversed())
                .collect(Collectors.toList());
    }

    public List<MedicalRecord> findByDoctorId(String doctorId) {
        return records.values().stream()
                .filter(r -> r.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    public Optional<MedicalRecord> findByAppointmentId(String appointmentId) {
        return records.values().stream()
                .filter(r -> r.getAppointmentId().equals(appointmentId))
                .findFirst();
    }

    public boolean existsById(String recordId) {
        return records.containsKey(recordId);
    }

    public void deleteById(String recordId) {
        records.remove(recordId);
    }

    public long count() {
        return records.size();
    }

    public void clear() {
        records.clear();
    }
}
