package com.healthcare.service;

import com.healthcare.exception.AppointmentNotFoundException;
import com.healthcare.exception.InvalidDataException;
import com.healthcare.model.Appointment;
import com.healthcare.model.AppointmentStatus;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.util.DateUtil;
import com.healthcare.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public AppointmentService(AppointmentRepository appointmentRepository, 
                              PatientService patientService, 
                              DoctorService doctorService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    public Appointment scheduleAppointment(Appointment appointment) {
        validateAppointment(appointment);
        
        if (!patientService.patientExists(appointment.getPatientId())) {
            throw new InvalidDataException("Patient not found with ID: " + appointment.getPatientId());
        }
        
        if (!doctorService.doctorExists(appointment.getDoctorId())) {
            throw new InvalidDataException("Doctor not found with ID: " + appointment.getDoctorId());
        }
        
        if (!DateUtil.isFutureDateTime(appointment.getAppointmentDateTime())) {
            throw new InvalidDataException("Appointment date must be in the future");
        }
        
        if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isEmpty()) {
            appointment.setAppointmentId(generateAppointmentId());
        }
        
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        return appointmentRepository.save(appointment);
    }

    public Appointment getAppointmentById(String appointmentId) {
        ValidationUtil.validateNotEmpty(appointmentId, "Appointment ID");
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId));
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByPatient(String patientId) {
        ValidationUtil.validateNotEmpty(patientId, "Patient ID");
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        ValidationUtil.validateNotEmpty(doctorId, "Doctor ID");
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        ValidationUtil.validateNotNull(date, "Date");
        return appointmentRepository.findByDate(date);
    }

    public List<Appointment> getUpcomingAppointments() {
        return appointmentRepository.findUpcomingAppointments();
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        ValidationUtil.validateNotNull(status, "Status");
        return appointmentRepository.findByStatus(status);
    }

    public Appointment updateAppointmentStatus(String appointmentId, AppointmentStatus status) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public Appointment rescheduleAppointment(String appointmentId, LocalDateTime newDateTime) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        if (!DateUtil.isFutureDateTime(newDateTime)) {
            throw new InvalidDataException("New appointment date must be in the future");
        }
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED || 
            appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidDataException("Cannot reschedule a " + appointment.getStatus() + " appointment");
        }
        
        appointment.setAppointmentDateTime(newDateTime);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        return appointmentRepository.save(appointment);
    }

    public void cancelAppointment(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidDataException("Cannot cancel a completed appointment");
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public void completeAppointment(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }

    public void deleteAppointment(String appointmentId) {
        ValidationUtil.validateNotEmpty(appointmentId, "Appointment ID");
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId);
        }
        appointmentRepository.deleteById(appointmentId);
    }

    public long getTotalAppointmentCount() {
        return appointmentRepository.count();
    }

    private void validateAppointment(Appointment appointment) {
        ValidationUtil.validateNotNull(appointment, "Appointment");
        ValidationUtil.validateNotEmpty(appointment.getPatientId(), "Patient ID");
        ValidationUtil.validateNotEmpty(appointment.getDoctorId(), "Doctor ID");
        ValidationUtil.validateNotNull(appointment.getAppointmentDateTime(), "Appointment date/time");
        ValidationUtil.validateNotEmpty(appointment.getReason(), "Reason");
        ValidationUtil.validatePositiveNumber(appointment.getDurationMinutes(), "Duration");
    }

    private String generateAppointmentId() {
        return "APT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
