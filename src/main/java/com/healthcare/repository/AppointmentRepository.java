package com.healthcare.repository;

import com.healthcare.model.Appointment;
import com.healthcare.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AppointmentRepository {
    private final Map<String, Appointment> appointments;

    public AppointmentRepository() {
        this.appointments = new HashMap<>();
    }

    public Appointment save(Appointment appointment) {
        appointments.put(appointment.getAppointmentId(), appointment);
        return appointment;
    }

    public Optional<Appointment> findById(String appointmentId) {
        return Optional.ofNullable(appointments.get(appointmentId));
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(appointments.values());
    }

    public List<Appointment> findByPatientId(String patientId) {
        return appointments.values().stream()
                .filter(a -> a.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public List<Appointment> findByDoctorId(String doctorId) {
        return appointments.values().stream()
                .filter(a -> a.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointments.values().stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Appointment> findByDate(LocalDate date) {
        return appointments.values().stream()
                .filter(a -> a.getAppointmentDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<Appointment> findUpcomingAppointments() {
        return appointments.values().stream()
                .filter(Appointment::isUpcoming)
                .sorted(Comparator.comparing(Appointment::getAppointmentDateTime))
                .collect(Collectors.toList());
    }

    public boolean existsById(String appointmentId) {
        return appointments.containsKey(appointmentId);
    }

    public void deleteById(String appointmentId) {
        appointments.remove(appointmentId);
    }

    public long count() {
        return appointments.size();
    }

    public void clear() {
        appointments.clear();
    }
}
