package com.medflow.model;

import java.time.LocalDateTime;

/**
 * =============================================================================
 * Treatment Model
 * Represents a medical treatment or surgery assigned to a patient.
 * 
 * OS Concept Mapping:
 * - This is the central entity that maps directly to an OS "Process" (Task).
 * - burstTimeMinutes = CPU Burst Time required by the process.
 * - arrivalTime = Process Arrival Time in the Ready Queue.
 * 
 * DBMS Concept Mapping:
 * - Maps to the Oracle 'TREATMENTS' table.
 * =============================================================================
 */
public class Treatment {

    private Long treatmentId;
    private Long patientId;
    private Long doctorId;
    private String treatmentType;
    private int burstTimeMinutes; // OS CPU Burst Time
    private LocalDateTime arrivalTime; // OS Arrival Time
    private String scheduleStatus; // 'Pending', 'Running', 'Completed'

    public Treatment() {
    }

    public Treatment(Long treatmentId, Long patientId, Long doctorId, String treatmentType, int burstTimeMinutes, LocalDateTime arrivalTime, String scheduleStatus) {
        this.treatmentId = treatmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.treatmentType = treatmentType;
        this.burstTimeMinutes = burstTimeMinutes;
        this.arrivalTime = arrivalTime;
        this.scheduleStatus = scheduleStatus;
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Long treatmentId) {
        this.treatmentId = treatmentId;
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

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public int getBurstTimeMinutes() {
        return burstTimeMinutes;
    }

    public void setBurstTimeMinutes(int burstTimeMinutes) {
        this.burstTimeMinutes = burstTimeMinutes;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
}
