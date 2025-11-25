package com.healthcare.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Prescription {
    private String prescriptionId;
    private String patientId;
    private String doctorId;
    private String appointmentId;
    private LocalDate prescriptionDate;
    private List<Medication> medications;
    private String diagnosis;
    private String instructions;
    private int validityDays;

    public Prescription() {
        this.prescriptionDate = LocalDate.now();
        this.medications = new ArrayList<>();
        this.validityDays = 30;
    }

    public Prescription(String prescriptionId, String patientId, String doctorId, 
                        String appointmentId, String diagnosis) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.prescriptionDate = LocalDate.now();
        this.diagnosis = diagnosis;
        this.medications = new ArrayList<>();
        this.validityDays = 30;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

    public void addMedication(Medication medication) {
        this.medications.add(medication);
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(int validityDays) {
        this.validityDays = validityDays;
    }

    public boolean isValid() {
        LocalDate expiryDate = prescriptionDate.plusDays(validityDays);
        return LocalDate.now().isBefore(expiryDate) || LocalDate.now().isEqual(expiryDate);
    }

    public LocalDate getExpiryDate() {
        return prescriptionDate.plusDays(validityDays);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescription that = (Prescription) o;
        return Objects.equals(prescriptionId, that.prescriptionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prescriptionId);
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "prescriptionId='" + prescriptionId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", prescriptionDate=" + prescriptionDate +
                ", diagnosis='" + diagnosis + '\'' +
                ", medications=" + medications.size() +
                ", valid=" + isValid() +
                '}';
    }

    public static class Medication {
        private String medicineName;
        private String dosage;
        private String frequency;
        private int durationDays;
        private String instructions;

        public Medication() {}

        public Medication(String medicineName, String dosage, String frequency, int durationDays) {
            this.medicineName = medicineName;
            this.dosage = dosage;
            this.frequency = frequency;
            this.durationDays = durationDays;
        }

        public String getMedicineName() {
            return medicineName;
        }

        public void setMedicineName(String medicineName) {
            this.medicineName = medicineName;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public int getDurationDays() {
            return durationDays;
        }

        public void setDurationDays(int durationDays) {
            this.durationDays = durationDays;
        }

        public String getInstructions() {
            return instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        @Override
        public String toString() {
            return "Medication{" +
                    "medicine='" + medicineName + '\'' +
                    ", dosage='" + dosage + '\'' +
                    ", frequency='" + frequency + '\'' +
                    ", duration=" + durationDays + " days" +
                    '}';
        }
    }
}
