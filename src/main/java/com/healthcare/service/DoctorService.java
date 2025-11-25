package com.healthcare.service;

import com.healthcare.exception.DoctorNotFoundException;
import com.healthcare.exception.InvalidDataException;
import com.healthcare.model.Doctor;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.util.ValidationUtil;

import java.util.List;
import java.util.UUID;

public class DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor registerDoctor(Doctor doctor) {
        validateDoctor(doctor);
        
        if (doctor.getDoctorId() == null || doctor.getDoctorId().isEmpty()) {
            doctor.setDoctorId(generateDoctorId());
        }
        
        if (doctorRepository.existsById(doctor.getDoctorId())) {
            throw new InvalidDataException("Doctor with ID " + doctor.getDoctorId() + " already exists");
        }
        
        return doctorRepository.save(doctor);
    }

    public Doctor getDoctorById(String doctorId) {
        ValidationUtil.validateNotEmpty(doctorId, "Doctor ID");
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        ValidationUtil.validateNotEmpty(specialization, "Specialization");
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findAvailableDoctors();
    }

    public Doctor updateDoctor(String doctorId, Doctor updatedDoctor) {
        ValidationUtil.validateNotEmpty(doctorId, "Doctor ID");
        getDoctorById(doctorId); // Ensure doctor exists
        
        validateDoctor(updatedDoctor);
        updatedDoctor.setDoctorId(doctorId);
        
        return doctorRepository.save(updatedDoctor);
    }

    public void setDoctorAvailability(String doctorId, boolean available) {
        Doctor doctor = getDoctorById(doctorId);
        doctor.setAvailable(available);
        doctorRepository.save(doctor);
    }

    public void deleteDoctor(String doctorId) {
        ValidationUtil.validateNotEmpty(doctorId, "Doctor ID");
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException("Doctor not found with ID: " + doctorId);
        }
        doctorRepository.deleteById(doctorId);
    }

    public boolean doctorExists(String doctorId) {
        return doctorId != null && doctorRepository.existsById(doctorId);
    }

    public long getTotalDoctorCount() {
        return doctorRepository.count();
    }

    private void validateDoctor(Doctor doctor) {
        ValidationUtil.validateNotNull(doctor, "Doctor");
        ValidationUtil.validateNotEmpty(doctor.getFirstName(), "First name");
        ValidationUtil.validateNotEmpty(doctor.getLastName(), "Last name");
        ValidationUtil.validateNotEmpty(doctor.getSpecialization(), "Specialization");
        ValidationUtil.validatePhoneNumber(doctor.getPhoneNumber());
        ValidationUtil.validateEmail(doctor.getEmail());
        ValidationUtil.validateNonNegativeNumber(doctor.getYearsOfExperience(), "Years of experience");
        ValidationUtil.validateNotEmpty(doctor.getQualification(), "Qualification");
    }

    private String generateDoctorId() {
        return "DOC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
