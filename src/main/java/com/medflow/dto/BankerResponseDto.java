package com.medflow.dto;

import java.util.List;

/**
 * =============================================================================
 * BankerResponseDto
 * Output result from Banker's Algorithm safety algorithm.
 * 
 * OS Concept Mapping:
 * - safe / isSafe: True if system is in a Safe State (zero deadlock risk).
 * - safeSequence: An ordered list of processes, e.g. ["T2", "T3", "T1", "T4"].
 * - auditExplanation: Human-readable rationale for viva examination and safety auditing.
 * =============================================================================
 */
public class BankerResponseDto {

    private boolean safe;
    private List<String> safeSequence;
    private String auditExplanation;

    public BankerResponseDto() {
    }

    public BankerResponseDto(boolean isSafe, List<String> safeSequence, String auditExplanation) {
        this.safe = isSafe;
        this.safeSequence = safeSequence;
        this.auditExplanation = auditExplanation;
    }

    public boolean isSafe() {
        return safe;
    }

    public boolean getSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public List<String> getSafeSequence() {
        return safeSequence;
    }

    public void setSafeSequence(List<String> safeSequence) {
        this.safeSequence = safeSequence;
    }

    public String getAuditExplanation() {
        return auditExplanation;
    }

    public void setAuditExplanation(String auditExplanation) {
        this.auditExplanation = auditExplanation;
    }
}
