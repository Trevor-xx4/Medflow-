package com.medflow.dto;

import java.util.List;

/**
 * =============================================================================
 * BankerRequestDto
 * Holds input matrices for testing Banker's Algorithm safety & resource allocation.
 * 
 * OS Concept Mapping (Banker's Algorithm Matrices):
 * - Available Vector: Number of available instances of each resource type.
 * - Allocation Matrix: Current instances allocated to each process.
 * - Max Matrix: Maximum instances claimed by each process.
 * - Need Matrix (Computed as Max - Allocation).
 * =============================================================================
 */
public class BankerRequestDto {

    private List<String> processNames;       // e.g. ["P0", "P1", "P2", "P3"]
    private List<String> resourceNames;      // e.g. ["ICU_BED", "VENTILATOR", "OR"]
    private int[] available;                 // 1D Array of size M (Resources)
    private int[][] maxMatrix;               // 2D Array of size N x M (Processes x Resources)
    private int[][] allocationMatrix;        // 2D Array of size N x M (Processes x Resources)

    public BankerRequestDto() {
    }

    public BankerRequestDto(List<String> processNames, List<String> resourceNames, int[] available, int[][] maxMatrix, int[][] allocationMatrix) {
        this.processNames = processNames;
        this.resourceNames = resourceNames;
        this.available = available;
        this.maxMatrix = maxMatrix;
        this.allocationMatrix = allocationMatrix;
    }

    public List<String> getProcessNames() {
        return processNames;
    }

    public void setProcessNames(List<String> processNames) {
        this.processNames = processNames;
    }

    public List<String> getResourceNames() {
        return resourceNames;
    }

    public void setResourceNames(List<String> resourceNames) {
        this.resourceNames = resourceNames;
    }

    public int[] getAvailable() {
        return available;
    }

    public void setAvailable(int[] available) {
        this.available = available;
    }

    public int[][] getMaxMatrix() {
        return maxMatrix;
    }

    public void setMaxMatrix(int[][] maxMatrix) {
        this.maxMatrix = maxMatrix;
    }

    public int[][] getAllocationMatrix() {
        return allocationMatrix;
    }

    public void setAllocationMatrix(int[][] allocationMatrix) {
        this.allocationMatrix = allocationMatrix;
    }
}
