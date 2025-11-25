package com.healthcare;

import com.healthcare.exception.*;
import com.healthcare.model.*;
import com.healthcare.repository.*;
import com.healthcare.service.*;
import com.healthcare.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class HealthcareApp {
    private static final Scanner scanner = new Scanner(System.in);
    
    private static PatientService patientService;
    private static DoctorService doctorService;
    private static AppointmentService appointmentService;
    private static PrescriptionService prescriptionService;
    private static MedicalRecordService medicalRecordService;

    public static void main(String[] args) {
        initializeServices();
        loadSampleData();
        
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  HEALTHCARE MANAGEMENT SYSTEM v1.0     ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        boolean running = true;
        while (running) {
            try {
                displayMainMenu();
                int choice = getIntInput("Enter your choice: ");
                
                switch (choice) {
                    case 1:
                        patientMenu();
                        break;
                    case 2:
                        doctorMenu();
                        break;
                    case 3:
                        appointmentMenu();
                        break;
                    case 4:
                        prescriptionMenu();
                        break;
                    case 5:
                        medicalRecordMenu();
                        break;
                    case 6:
                        displayStatistics();
                        break;
                    case 0:
                        running = false;
                        System.out.println("\nThank you for using Healthcare Management System!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        
        scanner.close();
    }

    private static void initializeServices() {
        PatientRepository patientRepo = new PatientRepository();
        DoctorRepository doctorRepo = new DoctorRepository();
        AppointmentRepository appointmentRepo = new AppointmentRepository();
        PrescriptionRepository prescriptionRepo = new PrescriptionRepository();
        MedicalRecordRepository recordRepo = new MedicalRecordRepository();
        
        patientService = new PatientService(patientRepo);
        doctorService = new DoctorService(doctorRepo);
        appointmentService = new AppointmentService(appointmentRepo, patientService, doctorService);
        prescriptionService = new PrescriptionService(prescriptionRepo, patientService, doctorService);
        medicalRecordService = new MedicalRecordService(recordRepo, patientService, doctorService);
    }

    private static void loadSampleData() {
        // Sample Doctors
        Doctor doctor1 = new Doctor("DOC001", "John", "Smith", "Cardiology", 
                                    "9876543210", "john.smith@hospital.com", 15, "MBBS, MD");
        Doctor doctor2 = new Doctor("DOC002", "Sarah", "Johnson", "Pediatrics", 
                                    "9876543211", "sarah.johnson@hospital.com", 10, "MBBS, DCH");
        Doctor doctor3 = new Doctor("DOC003", "Michael", "Brown", "Orthopedics", 
                                    "9876543212", "michael.brown@hospital.com", 12, "MBBS, MS");
        
        doctorService.registerDoctor(doctor1);
        doctorService.registerDoctor(doctor2);
        doctorService.registerDoctor(doctor3);
        
        // Sample Patients
        Patient patient1 = new Patient("PAT001", "Alice", "Williams", 
                                       LocalDate.of(1990, 5, 15), "Female", 
                                       "9123456780", "alice.w@email.com", 
                                       "123 Main St, City", "A+");
        Patient patient2 = new Patient("PAT002", "Bob", "Davis", 
                                       LocalDate.of(1985, 8, 20), "Male", 
                                       "9123456781", "bob.d@email.com", 
                                       "456 Oak Ave, Town", "O+");
        Patient patient3 = new Patient("PAT003", "Charlie", "Miller", 
                                       LocalDate.of(2010, 3, 10), "Male", 
                                       "9123456782", "charlie.m@email.com", 
                                       "789 Pine Rd, Village", "B+");
        
        patientService.registerPatient(patient1);
        patientService.registerPatient(patient2);
        patientService.registerPatient(patient3);
        
        System.out.println("Sample data loaded successfully!\n");
    }

    private static void displayMainMenu() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│         MAIN MENU                   │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│ 1. Patient Management               │");
        System.out.println("│ 2. Doctor Management                │");
        System.out.println("│ 3. Appointment Management           │");
        System.out.println("│ 4. Prescription Management          │");
        System.out.println("│ 5. Medical Record Management        │");
        System.out.println("│ 6. View Statistics                  │");
        System.out.println("│ 0. Exit                             │");
        System.out.println("└─────────────────────────────────────┘");
    }

    private static void patientMenu() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│      PATIENT MANAGEMENT             │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│ 1. Register New Patient             │");
        System.out.println("│ 2. View All Patients                │");
        System.out.println("│ 3. Search Patient by ID             │");
        System.out.println("│ 4. Search Patients by Last Name     │");
        System.out.println("│ 5. Search Patients by Blood Group   │");
        System.out.println("│ 0. Back to Main Menu                │");
        System.out.println("└─────────────────────────────────────┘");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1:
                registerPatient();
                break;
            case 2:
                viewAllPatients();
                break;
            case 3:
                searchPatientById();
                break;
            case 4:
                searchPatientsByLastName();
                break;
            case 5:
                searchPatientsByBloodGroup();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void registerPatient() {
        System.out.println("\n--- Register New Patient ---");
        
        String firstName = getStringInput("First Name: ");
        String lastName = getStringInput("Last Name: ");
        String dobStr = getStringInput("Date of Birth (yyyy-MM-dd): ");
        LocalDate dob = DateUtil.parseDate(dobStr);
        String gender = getStringInput("Gender (Male/Female/Other): ");
        String phone = getStringInput("Phone Number (10 digits): ");
        String email = getStringInput("Email: ");
        String address = getStringInput("Address: ");
        String bloodGroup = getStringInput("Blood Group (e.g., A+, B-, O+): ");
        
        Patient patient = new Patient(null, firstName, lastName, dob, gender, 
                                      phone, email, address, bloodGroup);
        
        try {
            Patient registered = patientService.registerPatient(patient);
            System.out.println("\n✓ Patient registered successfully!");
            System.out.println("Patient ID: " + registered.getPatientId());
            System.out.println(registered);
        } catch (InvalidDataException e) {
            System.out.println("\n✗ Registration failed: " + e.getMessage());
        }
    }

    private static void viewAllPatients() {
        System.out.println("\n--- All Patients ---");
        List<Patient> patients = patientService.getAllPatients();
        
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }
        
        System.out.println("Total patients: " + patients.size());
        System.out.println();
        for (Patient patient : patients) {
            displayPatientInfo(patient);
        }
    }

    private static void searchPatientById() {
        String patientId = getStringInput("Enter Patient ID: ");
        
        try {
            Patient patient = patientService.getPatientById(patientId);
            displayPatientInfo(patient);
        } catch (PatientNotFoundException e) {
            System.out.println("\n✗ " + e.getMessage());
        }
    }

    private static void searchPatientsByLastName() {
        String lastName = getStringInput("Enter Last Name: ");
        List<Patient> patients = patientService.getPatientsByLastName(lastName);
        
        if (patients.isEmpty()) {
            System.out.println("No patients found with last name: " + lastName);
            return;
        }
        
        System.out.println("\nFound " + patients.size() + " patient(s):");
        for (Patient patient : patients) {
            displayPatientInfo(patient);
        }
    }

    private static void searchPatientsByBloodGroup() {
        String bloodGroup = getStringInput("Enter Blood Group: ");
        
        try {
            List<Patient> patients = patientService.getPatientsByBloodGroup(bloodGroup);
            
            if (patients.isEmpty()) {
                System.out.println("No patients found with blood group: " + bloodGroup);
                return;
            }
            
            System.out.println("\nFound " + patients.size() + " patient(s):");
            for (Patient patient : patients) {
                displayPatientInfo(patient);
            }
        } catch (InvalidDataException e) {
            System.out.println("\n✗ " + e.getMessage());
        }
    }

    private static void displayPatientInfo(Patient patient) {
        System.out.println("─────────────────────────────────────");
        System.out.println("Patient ID    : " + patient.getPatientId());
        System.out.println("Name          : " + patient.getFullName());
        System.out.println("Age           : " + patient.getAge() + " years");
        System.out.println("Gender        : " + patient.getGender());
        System.out.println("Blood Group   : " + patient.getBloodGroup());
        System.out.println("Phone         : " + patient.getPhoneNumber());
        System.out.println("Email         : " + patient.getEmail());
        System.out.println("Address       : " + patient.getAddress());
        System.out.println("Registered    : " + DateUtil.formatDate(patient.getRegistrationDate()));
        System.out.println();
    }

    private static void doctorMenu() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│      DOCTOR MANAGEMENT              │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│ 1. Register New Doctor              │");
        System.out.println("│ 2. View All Doctors                 │");
        System.out.println("│ 3. Search Doctor by ID              │");
        System.out.println("│ 4. Search Doctors by Specialization │");
        System.out.println("│ 5. View Available Doctors           │");
        System.out.println("│ 0. Back to Main Menu                │");
        System.out.println("└─────────────────────────────────────┘");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1:
                registerDoctor();
                break;
            case 2:
                viewAllDoctors();
                break;
            case 3:
                searchDoctorById();
                break;
            case 4:
                searchDoctorsBySpecialization();
                break;
            case 5:
                viewAvailableDoctors();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void registerDoctor() {
        System.out.println("\n--- Register New Doctor ---");
        
        String firstName = getStringInput("First Name: ");
        String lastName = getStringInput("Last Name: ");
        String specialization = getStringInput("Specialization: ");
        String phone = getStringInput("Phone Number (10 digits): ");
        String email = getStringInput("Email: ");
        int experience = getIntInput("Years of Experience: ");
        String qualification = getStringInput("Qualification: ");
        
        Doctor doctor = new Doctor(null, firstName, lastName, specialization, 
                                   phone, email, experience, qualification);
        
        try {
            Doctor registered = doctorService.registerDoctor(doctor);
            System.out.println("\n✓ Doctor registered successfully!");
            System.out.println("Doctor ID: " + registered.getDoctorId());
            System.out.println(registered);
        } catch (InvalidDataException e) {
            System.out.println("\n✗ Registration failed: " + e.getMessage());
        }
    }

    private static void viewAllDoctors() {
        System.out.println("\n--- All Doctors ---");
        List<Doctor> doctors = doctorService.getAllDoctors();
        
        if (doctors.isEmpty()) {
            System.out.println("No doctors found.");
            return;
        }
        
        System.out.println("Total doctors: " + doctors.size());
        System.out.println();
        for (Doctor doctor : doctors) {
            displayDoctorInfo(doctor);
        }
    }

    private static void searchDoctorById() {
        String doctorId = getStringInput("Enter Doctor ID: ");
        
        try {
            Doctor doctor = doctorService.getDoctorById(doctorId);
            displayDoctorInfo(doctor);
        } catch (DoctorNotFoundException e) {
            System.out.println("\n✗ " + e.getMessage());
        }
    }

    private static void searchDoctorsBySpecialization() {
        String specialization = getStringInput("Enter Specialization: ");
        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
        
        if (doctors.isEmpty()) {
            System.out.println("No doctors found with specialization: " + specialization);
            return;
        }
        
        System.out.println("\nFound " + doctors.size() + " doctor(s):");
        for (Doctor doctor : doctors) {
            displayDoctorInfo(doctor);
        }
    }

    private static void viewAvailableDoctors() {
        System.out.println("\n--- Available Doctors ---");
        List<Doctor> doctors = doctorService.getAvailableDoctors();
        
        if (doctors.isEmpty()) {
            System.out.println("No available doctors at the moment.");
            return;
        }
        
        System.out.println("Total available doctors: " + doctors.size());
        System.out.println();
        for (Doctor doctor : doctors) {
            displayDoctorInfo(doctor);
        }
    }

    private static void displayDoctorInfo(Doctor doctor) {
        System.out.println("─────────────────────────────────────");
        System.out.println("Doctor ID       : " + doctor.getDoctorId());
        System.out.println("Name            : " + doctor.getFullName());
        System.out.println("Specialization  : " + doctor.getSpecialization());
        System.out.println("Experience      : " + doctor.getYearsOfExperience() + " years");
        System.out.println("Qualification   : " + doctor.getQualification());
        System.out.println("Phone           : " + doctor.getPhoneNumber());
        System.out.println("Email           : " + doctor.getEmail());
        System.out.println("Available       : " + (doctor.isAvailable() ? "Yes" : "No"));
        System.out.println();
    }

    private static void appointmentMenu() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│    APPOINTMENT MANAGEMENT           │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│ 1. Schedule New Appointment         │");
        System.out.println("│ 2. View All Appointments            │");
        System.out.println("│ 3. View Upcoming Appointments       │");
        System.out.println("│ 4. Search Appointment by ID         │");
        System.out.println("│ 5. View Patient Appointments        │");
        System.out.println("│ 6. View Doctor Appointments         │");
        System.out.println("│ 7. Cancel Appointment               │");
        System.out.println("│ 8. Complete Appointment             │");
        System.out.println("│ 0. Back to Main Menu                │");
        System.out.println("└─────────────────────────────────────┘");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1:
                scheduleAppointment();
                break;
            case 2:
                viewAllAppointments();
                break;
            case 3:
                viewUpcomingAppointments();
                break;
            case 4:
                searchAppointmentById();
                break;
            case 5:
                viewPatientAppointments();
                break;
            case 6:
                viewDoctorAppointments();
                break;
            case 7:
                cancelAppointment();
                break;
            case 8:
                completeAppointment();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void scheduleAppointment() {
        System.out.println("\n--- Schedule New Appointment ---");
        
        String patientId = getStringInput("Patient ID: ");
        String doctorId = getStringInput("Doctor ID: ");
        String dateTimeStr = getStringInput("Appointment Date & Time (yyyy-MM-dd HH:mm): ");
        LocalDateTime dateTime = DateUtil.parseDateTime(dateTimeStr);
        String reason = getStringInput("Reason for Appointment: ");
        
        Appointment appointment = new Appointment(null, patientId, doctorId, dateTime, reason);
        
        try {
            Appointment scheduled = appointmentService.scheduleAppointment(appointment);
            System.out.println("\n✓ Appointment scheduled successfully!");
            System.out.println("Appointment ID: " + scheduled.getAppointmentId());
            System.out.println(scheduled);
        } catch (InvalidDataException e) {
            System.out.println("\n✗ Scheduling failed: " + e.getMessage());
        }
    }

    private static void viewAllAppointments() {
        System.out.println("\n--- All Appointments ---");
        List<Appointment> appointments = appointmentService.getAllAppointments();
        
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }
        
        System.out.println("Total appointments: " + appointments.size());
        System.out.println();
        for (Appointment appointment : appointments) {
            displayAppointmentInfo(appointment);
        }
    }

    private static void viewUpcomingAppointments() {
        System.out.println("\n--- Upcoming Appointments ---");
        List<Appointment> appointments = appointmentService.getUpcomingAppointments();
        
        if (appointments.isEmpty()) {
            System.out.println("No upcoming appointments.");
            return;
        }
        
        System.out.println("Total upcoming appointments: " + appointments.size());
        System.out.println();
        for (Appointment appointment : appointments) {
            displayAppointmentInfo(appointment);
        }
    }

    private static void searchAppointmentById() {
        String appointmentId = getStringInput("Enter Appointment ID: ");
        
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            displayAppointmentInfo(appointment);
        } catch (AppointmentNotFoundException e) {
            System.out.println("\n✗ " + e.getMessage());
        }
    }

    private static void viewPatientAppointments() {
        String patientId = getStringInput("Enter Patient ID: ");
        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
        
        if (appointments.isEmpty()) {
            System.out.println("No appointments found for patient: " + patientId);
            return;
        }
        
        System.out.println("\nFound " + appointments.size() + " appointment(s):");
        for (Appointment appointment : appointments) {
            displayAppointmentInfo(appointment);
        }
    }

    private static void viewDoctorAppointments() {
        String doctorId = getStringInput("Enter Doctor ID: ");
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        
        if (appointments.isEmpty()) {
            System.out.println("No appointments found for doctor: " + doctorId);
            return;
        }
        
        System.out.println("\nFound " + appointments.size() + " appointment(s):");
        for (Appointment appointment : appointments) {
            displayAppointmentInfo(appointment);
        }
    }

    private static void cancelAppointment() {
        String appointmentId = getStringInput("Enter Appointment ID to cancel: ");
        
        try {
            appointmentService.cancelAppointment(appointmentId);
            System.out.println("\n✓ Appointment cancelled successfully!");
        } catch (Exception e) {
            System.out.println("\n✗ Cancellation failed: " + e.getMessage());
        }
    }

    private static void completeAppointment() {
        String appointmentId = getStringInput("Enter Appointment ID to complete: ");
        
        try {
            appointmentService.completeAppointment(appointmentId);
            System.out.println("\n✓ Appointment marked as completed!");
        } catch (Exception e) {
            System.out.println("\n✗ Operation failed: " + e.getMessage());
        }
    }

    private static void displayAppointmentInfo(Appointment appointment) {
        System.out.println("─────────────────────────────────────");
        System.out.println("Appointment ID  : " + appointment.getAppointmentId());
        System.out.println("Patient ID      : " + appointment.getPatientId());
        System.out.println("Doctor ID       : " + appointment.getDoctorId());
        System.out.println("Date & Time     : " + DateUtil.formatDateTime(appointment.getAppointmentDateTime()));
        System.out.println("Reason          : " + appointment.getReason());
        System.out.println("Duration        : " + appointment.getDurationMinutes() + " minutes");
        System.out.println("Status          : " + appointment.getStatus());
        if (appointment.getNotes() != null) {
            System.out.println("Notes           : " + appointment.getNotes());
        }
        System.out.println();
    }

    private static void prescriptionMenu() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│   PRESCRIPTION MANAGEMENT           │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│ 1. Create New Prescription          │");
        System.out.println("│ 2. View All Prescriptions           │");
        System.out.println("│ 3. Search Prescription by ID        │");
        System.out.println("│ 4. View Patient Prescriptions       │");
        System.out.println("│ 5. View Doctor Prescriptions        │");
        System.out.println("│ 6. View Valid Prescriptions         │");
        System.out.println("│ 0. Back to Main Menu                │");
        System.out.println("└─────────────────────────────────────┘");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1:
                createPrescription();
                break;
            case 2:
                viewAllPrescriptions();
                break;
            case 3:
                searchPrescriptionById();
                break;
            case 4:
                viewPatientPrescriptions();
                break;
            case 5:
                viewDoctorPrescriptions();
                break;
            case 6:
                viewValidPrescriptions();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void createPrescription() {
        System.out.println("\n--- Create New Prescription ---");
        
        String patientId = getStringInput("Patient ID: ");
        String doctorId = getStringInput("Doctor ID: ");
        String appointmentId = getStringInput("Appointment ID (optional, press Enter to skip): ");
        if (appointmentId.trim().isEmpty()) {
            appointmentId = null;
        }
        String diagnosis = getStringInput("Diagnosis: ");
        String instructions = getStringInput("Instructions: ");
        int validityDays = getIntInput("Validity (days): ");
        
        Prescription prescription = new Prescription(null, patientId, doctorId, appointmentId, diagnosis);
        prescription.setInstructions(instructions);
        prescription.setValidityDays(validityDays);
        
        System.out.println("\nAdd medications (enter 0 medicines to skip):");
        int medCount = getIntInput("Number of medications to add: ");
        
        for (int i = 0; i < medCount; i++) {
            System.out.println("\nMedication " + (i + 1) + ":");
            String medicineName = getStringInput("  Medicine Name: ");
            String dosage = getStringInput("  Dosage (e.g., 500mg): ");
            String frequency = getStringInput("  Frequency (e.g., Twice daily): ");
            int durationDays = getIntInput("  Duration (days): ");
            String medInstructions = getStringInput("  Instructions (optional): ");
            
            Prescription.Medication medication = new Prescription.Medication(medicineName, dosage, frequency, durationDays);
            if (!medInstructions.trim().isEmpty()) {
                medication.setInstructions(medInstructions);
            }
            prescription.addMedication(medication);
        }
        
        try {
            Prescription created = prescriptionService.createPrescription(prescription);
            System.out.println("\n✓ Prescription created successfully!");
            System.out.println("Prescription ID: " + created.getPrescriptionId());
            System.out.println(created);
        } catch (InvalidDataException e) {
            System.out.println("\n✗ Creation failed: " + e.getMessage());
        }
    }

    private static void viewAllPrescriptions() {
        System.out.println("\n--- All Prescriptions ---");
        List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
        
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions found.");
            return;
        }
        
        System.out.println("Total prescriptions: " + prescriptions.size());
        System.out.println();
        for (Prescription prescription : prescriptions) {
            displayPrescriptionInfo(prescription);
        }
    }

    private static void searchPrescriptionById() {
        String prescriptionId = getStringInput("Enter Prescription ID: ");
        
        try {
            Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);
            displayPrescriptionInfo(prescription);
        } catch (InvalidDataException e) {
            System.out.println("\n✗ " + e.getMessage());
        }
    }

    private static void viewPatientPrescriptions() {
        String patientId = getStringInput("Enter Patient ID: ");
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
        
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions found for patient: " + patientId);
            return;
        }
        
        System.out.println("\nFound " + prescriptions.size() + " prescription(s):");
        for (Prescription prescription : prescriptions) {
            displayPrescriptionInfo(prescription);
        }
    }

    private static void viewDoctorPrescriptions() {
        String doctorId = getStringInput("Enter Doctor ID: ");
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorId);
        
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions found for doctor: " + doctorId);
            return;
        }
        
        System.out.println("\nFound " + prescriptions.size() + " prescription(s):");
        for (Prescription prescription : prescriptions) {
            displayPrescriptionInfo(prescription);
        }
    }

    private static void viewValidPrescriptions() {
        System.out.println("\n--- Valid Prescriptions ---");
        List<Prescription> prescriptions = prescriptionService.getValidPrescriptions();
        
        if (prescriptions.isEmpty()) {
            System.out.println("No valid prescriptions found.");
            return;
        }
        
        System.out.println("Total valid prescriptions: " + prescriptions.size());
        System.out.println();
        for (Prescription prescription : prescriptions) {
            displayPrescriptionInfo(prescription);
        }
    }

    private static void displayPrescriptionInfo(Prescription prescription) {
        System.out.println("─────────────────────────────────────");
        System.out.println("Prescription ID : " + prescription.getPrescriptionId());
        System.out.println("Patient ID      : " + prescription.getPatientId());
        System.out.println("Doctor ID       : " + prescription.getDoctorId());
        System.out.println("Date            : " + DateUtil.formatDate(prescription.getPrescriptionDate()));
        System.out.println("Diagnosis       : " + prescription.getDiagnosis());
        System.out.println("Valid Until     : " + DateUtil.formatDate(prescription.getExpiryDate()));
        System.out.println("Status          : " + (prescription.isValid() ? "Valid" : "Expired"));
        
        if (prescription.getInstructions() != null) {
            System.out.println("Instructions    : " + prescription.getInstructions());
        }
        
        if (!prescription.getMedications().isEmpty()) {
            System.out.println("Medications     :");
            for (Prescription.Medication med : prescription.getMedications()) {
                System.out.println("  - " + med.getMedicineName() + 
                                   " (" + med.getDosage() + ") - " + 
                                   med.getFrequency() + " for " + 
                                   med.getDurationDays() + " days");
            }
        }
        System.out.println();
    }

    private static void medicalRecordMenu() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│  MEDICAL RECORD MANAGEMENT          │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│ 1. Create New Medical Record        │");
        System.out.println("│ 2. View All Medical Records         │");
        System.out.println("│ 3. Search Medical Record by ID      │");
        System.out.println("│ 4. View Patient Medical Records     │");
        System.out.println("│ 5. View Doctor Medical Records      │");
        System.out.println("│ 0. Back to Main Menu                │");
        System.out.println("└─────────────────────────────────────┘");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1:
                createMedicalRecord();
                break;
            case 2:
                viewAllMedicalRecords();
                break;
            case 3:
                searchMedicalRecordById();
                break;
            case 4:
                viewPatientMedicalRecords();
                break;
            case 5:
                viewDoctorMedicalRecords();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void createMedicalRecord() {
        System.out.println("\n--- Create New Medical Record ---");
        
        String patientId = getStringInput("Patient ID: ");
        String doctorId = getStringInput("Doctor ID: ");
        String appointmentId = getStringInput("Appointment ID (optional, press Enter to skip): ");
        if (appointmentId.trim().isEmpty()) {
            appointmentId = null;
        }
        String chiefComplaint = getStringInput("Chief Complaint: ");
        String diagnosis = getStringInput("Diagnosis: ");
        String treatment = getStringInput("Treatment: ");
        String vitalSigns = getStringInput("Vital Signs (optional): ");
        String labResults = getStringInput("Lab Results (optional): ");
        String notes = getStringInput("Notes (optional): ");
        String followUp = getStringInput("Follow-up Instructions (optional): ");
        
        MedicalRecord record = new MedicalRecord(null, patientId, doctorId, 
                                                 appointmentId, chiefComplaint, diagnosis);
        record.setTreatment(treatment);
        if (!vitalSigns.trim().isEmpty()) record.setVitalSigns(vitalSigns);
        if (!labResults.trim().isEmpty()) record.setLabResults(labResults);
        if (!notes.trim().isEmpty()) record.setNotes(notes);
        if (!followUp.trim().isEmpty()) record.setFollowUpInstructions(followUp);
        
        try {
            MedicalRecord created = medicalRecordService.createMedicalRecord(record);
            System.out.println("\n✓ Medical record created successfully!");
            System.out.println("Record ID: " + created.getRecordId());
            System.out.println(created);
        } catch (InvalidDataException e) {
            System.out.println("\n✗ Creation failed: " + e.getMessage());
        }
    }

    private static void viewAllMedicalRecords() {
        System.out.println("\n--- All Medical Records ---");
        List<MedicalRecord> records = medicalRecordService.getAllMedicalRecords();
        
        if (records.isEmpty()) {
            System.out.println("No medical records found.");
            return;
        }
        
        System.out.println("Total medical records: " + records.size());
        System.out.println();
        for (MedicalRecord record : records) {
            displayMedicalRecordInfo(record);
        }
    }

    private static void searchMedicalRecordById() {
        String recordId = getStringInput("Enter Medical Record ID: ");
        
        try {
            MedicalRecord record = medicalRecordService.getMedicalRecordById(recordId);
            displayMedicalRecordInfo(record);
        } catch (InvalidDataException e) {
            System.out.println("\n✗ " + e.getMessage());
        }
    }

    private static void viewPatientMedicalRecords() {
        String patientId = getStringInput("Enter Patient ID: ");
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByPatient(patientId);
        
        if (records.isEmpty()) {
            System.out.println("No medical records found for patient: " + patientId);
            return;
        }
        
        System.out.println("\nFound " + records.size() + " medical record(s):");
        for (MedicalRecord record : records) {
            displayMedicalRecordInfo(record);
        }
    }

    private static void viewDoctorMedicalRecords() {
        String doctorId = getStringInput("Enter Doctor ID: ");
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByDoctor(doctorId);
        
        if (records.isEmpty()) {
            System.out.println("No medical records found for doctor: " + doctorId);
            return;
        }
        
        System.out.println("\nFound " + records.size() + " medical record(s):");
        for (MedicalRecord record : records) {
            displayMedicalRecordInfo(record);
        }
    }

    private static void displayMedicalRecordInfo(MedicalRecord record) {
        System.out.println("─────────────────────────────────────");
        System.out.println("Record ID         : " + record.getRecordId());
        System.out.println("Patient ID        : " + record.getPatientId());
        System.out.println("Doctor ID         : " + record.getDoctorId());
        System.out.println("Date & Time       : " + DateUtil.formatDateTime(record.getRecordDateTime()));
        System.out.println("Chief Complaint   : " + record.getChiefComplaint());
        System.out.println("Diagnosis         : " + record.getDiagnosis());
        System.out.println("Treatment         : " + record.getTreatment());
        
        if (record.getVitalSigns() != null) {
            System.out.println("Vital Signs       : " + record.getVitalSigns());
        }
        if (record.getLabResults() != null) {
            System.out.println("Lab Results       : " + record.getLabResults());
        }
        if (record.getNotes() != null) {
            System.out.println("Notes             : " + record.getNotes());
        }
        if (record.getFollowUpInstructions() != null) {
            System.out.println("Follow-up         : " + record.getFollowUpInstructions());
        }
        System.out.println();
    }

    private static void displayStatistics() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        SYSTEM STATISTICS               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Total Patients         : " + patientService.getTotalPatientCount());
        System.out.println("Total Doctors          : " + doctorService.getTotalDoctorCount());
        System.out.println("Total Appointments     : " + appointmentService.getTotalAppointmentCount());
        System.out.println("Upcoming Appointments  : " + appointmentService.getUpcomingAppointments().size());
        System.out.println("Total Prescriptions    : " + prescriptionService.getTotalPrescriptionCount());
        System.out.println("Valid Prescriptions    : " + prescriptionService.getValidPrescriptions().size());
        System.out.println("Total Medical Records  : " + medicalRecordService.getTotalRecordCount());
        System.out.println("Available Doctors      : " + doctorService.getAvailableDoctors().size());
        System.out.println();
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
