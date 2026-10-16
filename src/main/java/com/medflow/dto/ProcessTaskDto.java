package com.medflow.dto;

/**
 * =============================================================================
 * ProcessTaskDto
 * Data Transfer Object representing an incoming task/process for OS scheduling.
 * 
 * OS Concept Mapping:
 * - Represents a Process Control Block (PCB) attributes needed by the scheduler.
 * =============================================================================
 */
public class ProcessTaskDto {

    private String processId;     // e.g., "P1", "P2" (mapped to Treatment ID)
    private String patientName;   // Name of patient receiving treatment
    private int arrivalTime;      // Arrival Time in simulation time units
    private int burstTime;        // Burst Time (Duration of treatment)
    private int priority;         // 1 = Emergency, 2 = Urgent, 3 = Routine

    public ProcessTaskDto() {
    }

    public ProcessTaskDto(String processId, String patientName, int arrivalTime, int burstTime, int priority) {
        this.processId = processId;
        this.patientName = patientName;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
