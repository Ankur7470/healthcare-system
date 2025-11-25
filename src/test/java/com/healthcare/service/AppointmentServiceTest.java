package com.healthcare.service;

import com.healthcare.exception.AppointmentNotFoundException;
import com.healthcare.exception.InvalidDataException;
import com.healthcare.model.Appointment;
import com.healthcare.model.AppointmentStatus;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentServiceTest {

    private AppointmentService appointmentService;
    private PatientService patientService;
    private DoctorService doctorService;
    private String validPatientId;
    private String validDoctorId;

    @BeforeEach
    void setUp() {
        PatientRepository patientRepo = new PatientRepository();
        DoctorRepository doctorRepo = new DoctorRepository();
        AppointmentRepository appointmentRepo = new AppointmentRepository();
        
        patientService = new PatientService(patientRepo);
        doctorService = new DoctorService(doctorRepo);
        appointmentService = new AppointmentService(appointmentRepo, patientService, doctorService);
        
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
    @DisplayName("Should schedule appointment successfully")
    void testScheduleAppointment_Success() {
        Appointment appointment = createValidAppointment();
        
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        
        assertNotNull(scheduled);
        assertNotNull(scheduled.getAppointmentId());
        assertEquals(AppointmentStatus.SCHEDULED, scheduled.getStatus());
        assertEquals(validPatientId, scheduled.getPatientId());
        assertEquals(validDoctorId, scheduled.getDoctorId());
    }

    @Test
    @DisplayName("Should generate appointment ID when not provided")
    void testScheduleAppointment_GeneratesId() {
        Appointment appointment = createValidAppointment();
        appointment.setAppointmentId(null);
        
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        
        assertNotNull(scheduled.getAppointmentId());
        assertTrue(scheduled.getAppointmentId().startsWith("APT"));
    }

    @Test
    @DisplayName("Should throw exception when patient does not exist")
    void testScheduleAppointment_InvalidPatient() {
        Appointment appointment = createValidAppointment();
        appointment.setPatientId("INVALID_PATIENT");
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.scheduleAppointment(appointment);
        });
    }

    @Test
    @DisplayName("Should throw exception when doctor does not exist")
    void testScheduleAppointment_InvalidDoctor() {
        Appointment appointment = createValidAppointment();
        appointment.setDoctorId("INVALID_DOCTOR");
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.scheduleAppointment(appointment);
        });
    }

    @Test
    @DisplayName("Should throw exception when appointment date is in past")
    void testScheduleAppointment_PastDate() {
        Appointment appointment = createValidAppointment();
        appointment.setAppointmentDateTime(LocalDateTime.now().minusDays(1));
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.scheduleAppointment(appointment);
        });
    }

    @Test
    @DisplayName("Should throw exception when patient ID is empty")
    void testScheduleAppointment_EmptyPatientId() {
        Appointment appointment = createValidAppointment();
        appointment.setPatientId("");
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.scheduleAppointment(appointment);
        });
    }

    @Test
    @DisplayName("Should throw exception when doctor ID is empty")
    void testScheduleAppointment_EmptyDoctorId() {
        Appointment appointment = createValidAppointment();
        appointment.setDoctorId("");
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.scheduleAppointment(appointment);
        });
    }

    @Test
    @DisplayName("Should throw exception when reason is empty")
    void testScheduleAppointment_EmptyReason() {
        Appointment appointment = createValidAppointment();
        appointment.setReason("");
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.scheduleAppointment(appointment);
        });
    }

    @Test
    @DisplayName("Should throw exception when duration is zero")
    void testScheduleAppointment_ZeroDuration() {
        Appointment appointment = createValidAppointment();
        appointment.setDurationMinutes(0);
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.scheduleAppointment(appointment);
        });
    }

    @Test
    @DisplayName("Should throw exception when duration is negative")
    void testScheduleAppointment_NegativeDuration() {
        Appointment appointment = createValidAppointment();
        appointment.setDurationMinutes(-10);
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.scheduleAppointment(appointment);
        });
    }

    @Test
    @DisplayName("Should retrieve appointment by ID")
    void testGetAppointmentById_Success() {
        Appointment appointment = createValidAppointment();
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        
        Appointment retrieved = appointmentService.getAppointmentById(scheduled.getAppointmentId());
        
        assertNotNull(retrieved);
        assertEquals(scheduled.getAppointmentId(), retrieved.getAppointmentId());
    }

    @Test
    @DisplayName("Should throw exception when appointment ID not found")
    void testGetAppointmentById_NotFound() {
        assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentService.getAppointmentById("INVALID_ID");
        });
    }

    @Test
    @DisplayName("Should get all appointments")
    void testGetAllAppointments() {
        appointmentService.scheduleAppointment(createValidAppointment());
        appointmentService.scheduleAppointment(createValidAppointment());
        
        List<Appointment> appointments = appointmentService.getAllAppointments();
        
        assertEquals(2, appointments.size());
    }

    @Test
    @DisplayName("Should get appointments by patient")
    void testGetAppointmentsByPatient() {
        appointmentService.scheduleAppointment(createValidAppointment());
        appointmentService.scheduleAppointment(createValidAppointment());
        
        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(validPatientId);
        
        assertEquals(2, appointments.size());
        assertTrue(appointments.stream().allMatch(a -> a.getPatientId().equals(validPatientId)));
    }

    @Test
    @DisplayName("Should get appointments by doctor")
    void testGetAppointmentsByDoctor() {
        appointmentService.scheduleAppointment(createValidAppointment());
        appointmentService.scheduleAppointment(createValidAppointment());
        
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(validDoctorId);
        
        assertEquals(2, appointments.size());
        assertTrue(appointments.stream().allMatch(a -> a.getDoctorId().equals(validDoctorId)));
    }

    @Test
    @DisplayName("Should get appointments by date")
    void testGetAppointmentsByDate() {
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(7);
        Appointment appointment = createValidAppointment();
        appointment.setAppointmentDateTime(futureDateTime);
        appointmentService.scheduleAppointment(appointment);
        
        List<Appointment> appointments = appointmentService.getAppointmentsByDate(futureDateTime.toLocalDate());
        
        assertEquals(1, appointments.size());
    }

    @Test
    @DisplayName("Should get upcoming appointments")
    void testGetUpcomingAppointments() {
        Appointment futureAppointment = createValidAppointment();
        futureAppointment.setAppointmentDateTime(LocalDateTime.now().plusDays(7));
        appointmentService.scheduleAppointment(futureAppointment);
        
        List<Appointment> upcomingAppointments = appointmentService.getUpcomingAppointments();
        
        assertEquals(1, upcomingAppointments.size());
        assertTrue(upcomingAppointments.get(0).isUpcoming());
    }

    @Test
    @DisplayName("Should get appointments by status")
    void testGetAppointmentsByStatus() {
        Appointment appointment = createValidAppointment();
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        
        List<Appointment> scheduledAppointments = 
            appointmentService.getAppointmentsByStatus(AppointmentStatus.SCHEDULED);
        
        assertEquals(1, scheduledAppointments.size());
        assertEquals(AppointmentStatus.SCHEDULED, scheduledAppointments.get(0).getStatus());
    }

    @Test
    @DisplayName("Should update appointment status")
    void testUpdateAppointmentStatus() {
        Appointment appointment = createValidAppointment();
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        
        Appointment updated = appointmentService.updateAppointmentStatus(
            scheduled.getAppointmentId(), AppointmentStatus.CONFIRMED);
        
        assertEquals(AppointmentStatus.CONFIRMED, updated.getStatus());
    }

    @Test
    @DisplayName("Should reschedule appointment")
    void testRescheduleAppointment_Success() {
        Appointment appointment = createValidAppointment();
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(10);
        Appointment rescheduled = appointmentService.rescheduleAppointment(
            scheduled.getAppointmentId(), newDateTime);
        
        assertEquals(newDateTime, rescheduled.getAppointmentDateTime());
        assertEquals(AppointmentStatus.SCHEDULED, rescheduled.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when rescheduling to past date")
    void testRescheduleAppointment_PastDate() {
        Appointment appointment = createValidAppointment();
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(1);
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.rescheduleAppointment(scheduled.getAppointmentId(), pastDateTime);
        });
    }

    @Test
    @DisplayName("Should throw exception when rescheduling completed appointment")
    void testRescheduleAppointment_CompletedAppointment() {
        Appointment appointment = createValidAppointment();
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        appointmentService.updateAppointmentStatus(scheduled.getAppointmentId(), AppointmentStatus.COMPLETED);
        
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(10);
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.rescheduleAppointment(scheduled.getAppointmentId(), newDateTime);
        });
    }

    @Test
    @DisplayName("Should cancel appointment successfully")
    void testCancelAppointment_Success() {
        Appointment appointment = createValidAppointment();
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        
        appointmentService.cancelAppointment(scheduled.getAppointmentId());
        
        Appointment cancelled = appointmentService.getAppointmentById(scheduled.getAppointmentId());
        assertEquals(AppointmentStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when cancelling completed appointment")
    void testCancelAppointment_CompletedAppointment() {
        Appointment appointment = createValidAppointment();
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        appointmentService.updateAppointmentStatus(scheduled.getAppointmentId(), AppointmentStatus.COMPLETED);
        
        assertThrows(InvalidDataException.class, () -> {
            appointmentService.cancelAppointment(scheduled.getAppointmentId());
        });
    }

    @Test
    @DisplayName("Should complete appointment successfully")
    void testCompleteAppointment() {
        Appointment appointment = createValidAppointment();
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        
        appointmentService.completeAppointment(scheduled.getAppointmentId());
        
        Appointment completed = appointmentService.getAppointmentById(scheduled.getAppointmentId());
        assertEquals(AppointmentStatus.COMPLETED, completed.getStatus());
    }

    @Test
    @DisplayName("Should delete appointment successfully")
    void testDeleteAppointment_Success() {
        Appointment appointment = createValidAppointment();
        Appointment scheduled = appointmentService.scheduleAppointment(appointment);
        
        appointmentService.deleteAppointment(scheduled.getAppointmentId());
        
        assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentService.getAppointmentById(scheduled.getAppointmentId());
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent appointment")
    void testDeleteAppointment_NotFound() {
        assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentService.deleteAppointment("INVALID_ID");
        });
    }

    @Test
    @DisplayName("Should return correct appointment count")
    void testGetTotalAppointmentCount() {
        assertEquals(0, appointmentService.getTotalAppointmentCount());
        
        appointmentService.scheduleAppointment(createValidAppointment());
        assertEquals(1, appointmentService.getTotalAppointmentCount());
        
        appointmentService.scheduleAppointment(createValidAppointment());
        assertEquals(2, appointmentService.getTotalAppointmentCount());
    }

    private Appointment createValidAppointment() {
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(7);
        return new Appointment(
            null,
            validPatientId,
            validDoctorId,
            futureDateTime,
            "Regular checkup"
        );
    }
}
