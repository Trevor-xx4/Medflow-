package com.medflow.model;

/**
 * =============================================================================
 * HospitalResource Model
 * Represents physical medical equipment / assets with finite availability.
 * 
 * OS Concept Mapping:
 * - This represents an OS "Resource Type" (e.g., Resource R1, R2, R3).
 * - Examples:
 *     R1 = ICU_BED (Total: 10, Available: 4)
 *     R2 = VENTILATOR (Total: 6, Available: 2)
 *     R3 = OPERATION_THEATER (Total: 4, Available: 1)
 * - These form the "Available Vector" in Banker's Algorithm: Available = [4, 2, 1]
 * 
 * DBMS Concept Mapping:
 * - Maps to the Oracle 'HOSPITAL_RESOURCES' table.
 * =============================================================================
 */
public class HospitalResource {

    private Long resourceId;
    private String resourceName; // 'ICU_BED', 'VENTILATOR', 'OPERATION_THEATER'
    private int totalUnits;      // Total capacity in the hospital
    private int availableUnits;  // Currently unallocated units
    private String locationBlock;

    public HospitalResource() {
    }

    public HospitalResource(Long resourceId, String resourceName, int totalUnits, int availableUnits, String locationBlock) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.totalUnits = totalUnits;
        this.availableUnits = availableUnits;
        this.locationBlock = locationBlock;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }

    public int getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = availableUnits;
    }

    public String getLocationBlock() {
        return locationBlock;
    }

    public void setLocationBlock(String locationBlock) {
        this.locationBlock = locationBlock;
    }
}
