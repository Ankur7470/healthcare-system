package com.healthcare.service;

import com.healthcare.exception.InvalidDataException;
import com.healthcare.model.MedicalRecord;
import com.healthcare.repository.MedicalRecordRepository;
import com.healthcare.util.ValidationUtil;

import java.util.List;
import java.util.UUID;

public class MedicalRecordService {
    private final MedicalRecordRepository recordRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public MedicalRecordService(MedicalRecordRepository recordRepository, 
                                PatientService patientService, 
                                DoctorService doctorService) {
        this.recordRepository = recordRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    public MedicalRecord createMedicalRecord(MedicalRecord record) {
        validateMedicalRecord(record);
        
        if (!patientService.patientExists(record.getPatientId())) {
            throw new InvalidDataException("Patient not found with ID: " + record.getPatientId());
        }
        
        if (!doctorService.doctorExists(record.getDoctorId())) {
            throw new InvalidDataException("Doctor not found with ID: " + record.getDoctorId());
        }
        
        if (record.getRecordId() == null || record.getRecordId().isEmpty()) {
            record.setRecordId(generateRecordId());
        }
        
        return recordRepository.save(record);
    }

    public MedicalRecord getMedicalRecordById(String recordId) {
        ValidationUtil.validateNotEmpty(recordId, "Record ID");
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new InvalidDataException("Medical record not found with ID: " + recordId));
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        return recordRepository.findAll();
    }

    public List<MedicalRecord> getMedicalRecordsByPatient(String patientId) {
        ValidationUtil.validateNotEmpty(patientId, "Patient ID");
        return recordRepository.findByPatientId(patientId);
    }

    public List<MedicalRecord> getMedicalRecordsByDoctor(String doctorId) {
        ValidationUtil.validateNotEmpty(doctorId, "Doctor ID");
        return recordRepository.findByDoctorId(doctorId);
    }

    public MedicalRecord updateMedicalRecord(String recordId, MedicalRecord updatedRecord) {
        ValidationUtil.validateNotEmpty(recordId, "Record ID");
        MedicalRecord existingRecord = getMedicalRecordById(recordId);
        
        validateMedicalRecord(updatedRecord);
        updatedRecord.setRecordId(recordId);
        updatedRecord.setRecordDateTime(existingRecord.getRecordDateTime());
        
        return recordRepository.save(updatedRecord);
    }

    public void deleteMedicalRecord(String recordId) {
        ValidationUtil.validateNotEmpty(recordId, "Record ID");
        if (!recordRepository.existsById(recordId)) {
            throw new InvalidDataException("Medical record not found with ID: " + recordId);
        }
        recordRepository.deleteById(recordId);
    }

    public long getTotalRecordCount() {
        return recordRepository.count();
    }

    private void validateMedicalRecord(MedicalRecord record) {
        ValidationUtil.validateNotNull(record, "Medical record");
        ValidationUtil.validateNotEmpty(record.getPatientId(), "Patient ID");
        ValidationUtil.validateNotEmpty(record.getDoctorId(), "Doctor ID");
        ValidationUtil.validateNotEmpty(record.getChiefComplaint(), "Chief complaint");
        ValidationUtil.validateNotEmpty(record.getDiagnosis(), "Diagnosis");
    }

    private String generateRecordId() {
        return "REC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
