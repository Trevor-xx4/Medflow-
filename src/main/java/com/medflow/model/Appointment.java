package com.medflow.model;

import java.time.LocalDateTime;

/**
 * =============================================================================
 * Appointment Model
 * Links a Patient and a Doctor for a medical appointment.
 * 
 * DBMS Concept Mapping:
 * - Represents the foreign-key relationship between PATIENTS and DOCTORS.
 * - Maps to the Oracle 'APPOINTMENTS' table.
 * =============================================================================
 */
public class Appointment {

    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentTime;
    private String status; // 'Scheduled', 'Completed', 'Cancelled'

    public Appointment() {
    }

    public Appointment(Long appointmentId, Long patientId, Long doctorId, LocalDateTime appointmentTime, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
