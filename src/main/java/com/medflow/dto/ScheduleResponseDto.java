package com.medflow.dto;

import java.util.List;

/**
 * =============================================================================
 * ScheduleResponseDto
 * Encapsulates the results of a CPU scheduling simulation run.
 * 
 * OS Concept Metrics:
 * - Completion Time (CT): Time when a process completes execution.
 * - Turnaround Time (TAT): Completion Time - Arrival Time.
 * - Waiting Time (WT): Turnaround Time - Burst Time.
 * - Response Time (RT): Time from arrival until first scheduled.
 * =============================================================================
 */
public class ScheduleResponseDto {

    private String algorithm;              // 'FCFS', 'PRIORITY', or 'ROUND_ROBIN'
    private List<GanttBlockDto> ganttChart; // Sequence of executions over time
    private double averageWaitingTime;     // Avg WT across all processes
    private double averageTurnaroundTime;  // Avg TAT across all processes
    private String statusMessage;

    public ScheduleResponseDto() {
    }

    public ScheduleResponseDto(String algorithm, List<GanttBlockDto> ganttChart, double averageWaitingTime, double averageTurnaroundTime, String statusMessage) {
        this.algorithm = algorithm;
        this.ganttChart = ganttChart;
        this.averageWaitingTime = averageWaitingTime;
        this.averageTurnaroundTime = averageTurnaroundTime;
        this.statusMessage = statusMessage;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public List<GanttBlockDto> getGanttChart() {
        return ganttChart;
    }

    public void setGanttChart(List<GanttBlockDto> ganttChart) {
        this.ganttChart = ganttChart;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public void setAverageWaitingTime(double averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }

    public void setAverageTurnaroundTime(double averageTurnaroundTime) {
        this.averageTurnaroundTime = averageTurnaroundTime;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
