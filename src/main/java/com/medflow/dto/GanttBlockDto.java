package com.medflow.dto;

/**
 * =============================================================================
 * GanttBlockDto
 * Represents a single time slice/execution block on the OS Gantt Chart.
 * 
 * OS Concept Mapping:
 * - A Gantt Chart visually plots CPU execution over time.
 * - Each block shows which process executed from 'startTime' to 'endTime'.
 * =============================================================================
 */
public class GanttBlockDto {

    private String processId;   // e.g. "P1"
    private String label;       // e.g. "P1: Emergency Angioplasty"
    private int startTime;      // Execution start time unit
    private int endTime;        // Execution end time unit

    public GanttBlockDto() {
    }

    public GanttBlockDto(String processId, String label, int startTime, int endTime) {
        this.processId = processId;
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
