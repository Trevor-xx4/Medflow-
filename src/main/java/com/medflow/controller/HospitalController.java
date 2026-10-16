package com.medflow.controller;

import com.medflow.model.Doctor;
import com.medflow.model.HospitalResource;
import com.medflow.model.Patient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * =============================================================================
 * HospitalController (Phase 1 REST Controller Stub)
 * 
 * Provides mock endpoints for Hospital Management entities (Patients, Doctors, Resources).
 * In Phase 2, these will query the Oracle Database via Spring Data JPA.
 * =============================================================================
 */
@RestController
@RequestMapping("/api/hospital")
@CrossOrigin(origins = "*") // Allows cross-origin requests for local development
public class HospitalController {

    /**
     * GET /api/hospital/patients
     * Returns a mock list of admitted patients with triage priority levels.
     */
    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getPatients() {
        List<Patient> mockPatients = Arrays.asList(
                new Patient(1L, "John Doe", 45, "Male", "+91-9876543210", 1, "Admitted"),
                new Patient(2L, "Anita Roy", 32, "Female", "+91-9876543211", 2, "Admitted"),
                new Patient(3L, "Robert Smith", 58, "Male", "+91-9876543212", 3, "Admitted"),
                new Patient(4L, "Pooja Verma", 28, "Female", "+91-9876543213", 1, "Admitted")
        );
        return ResponseEntity.ok(mockPatients);
    }

    /**
     * GET /api/hospital/resources
     * Returns a mock list of scarce hospital resources (ICU beds, ventilators, ORs).
     */
    @GetMapping("/resources")
    public ResponseEntity<List<HospitalResource>> getResources() {
        List<HospitalResource> mockResources = Arrays.asList(
                new HospitalResource(1L, "ICU_BED", 10, 4, "Block A - Critical Care"),
                new HospitalResource(2L, "VENTILATOR", 6, 2, "Block A - Respiratory Unit"),
                new HospitalResource(3L, "OPERATION_THEATER", 4, 1, "Block B - Surgical Wing")
        );
        return ResponseEntity.ok(mockResources);
    }

    /**
     * GET /api/hospital/doctors
     * Returns a mock list of medical doctors.
     */
    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getDoctors() {
        List<Doctor> mockDoctors = Arrays.asList(
                new Doctor(1L, "Dr. Sarah Jenkins", "Cardiology", "Cardiology", "Available"),
                new Doctor(2L, "Dr. Rajesh Sharma", "Emergency Medicine", "Trauma Care", "Available"),
                new Doctor(3L, "Dr. Emily Chen", "Pulmonology", "Critical Care", "Busy")
        );
        return ResponseEntity.ok(mockDoctors);
    }
}
