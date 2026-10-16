package com.medflow.model;

/**
 * =============================================================================
 * Patient Model
 * Represents an admitted hospital patient.
 * 
 * OS Concept Mapping:
 * - Each patient requiring medical treatment represents a "Process" in the OS.
 * - priorityLevel (1=Emergency, 2=Urgent, 3=Routine) directly drives the OS
 *   Priority CPU Scheduling algorithm.
 * 
 * DBMS Concept Mapping:
 * - Maps to the Oracle 'PATIENTS' table.
 * =============================================================================
 */
public class Patient {

    private Long patientId;
    private String fullName;
    private int age;
    private String gender;
    private String contactNumber;
    private int priorityLevel; // 1 = Highest Priority (Emergency), 3 = Lowest (Routine)
    private String admissionStatus;

    // Default Constructor (Needed for JSON serialization)
    public Patient() {
    }

    // Parameterized Constructor
    public Patient(Long patientId, String fullName, int age, String gender, String contactNumber, int priorityLevel, String admissionStatus) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.priorityLevel = priorityLevel;
        this.admissionStatus = admissionStatus;
    }

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getAdmissionStatus() {
        return admissionStatus;
    }

    public void setAdmissionStatus(String admissionStatus) {
        this.admissionStatus = admissionStatus;
    }
}
