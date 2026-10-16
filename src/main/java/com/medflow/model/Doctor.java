package com.medflow.model;

/**
 * =============================================================================
 * Doctor Model
 * Represents a medical professional in the hospital.
 * 
 * OS Concept Mapping:
 * - A doctor represents a specialized "Processing Core / Server" that executes
 *   patient treatment processes.
 * 
 * DBMS Concept Mapping:
 * - Maps to the Oracle 'DOCTORS' table.
 * =============================================================================
 */
public class Doctor {

    private Long doctorId;
    private String fullName;
    private String specialization;
    private String department;
    private String availabilityStatus; // 'Available', 'Busy', 'On-Leave'

    public Doctor() {
    }

    public Doctor(Long doctorId, String fullName, String specialization, String department, String availabilityStatus) {
        this.doctorId = doctorId;
        this.fullName = fullName;
        this.specialization = specialization;
        this.department = department;
        this.availabilityStatus = availabilityStatus;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
