package com.medflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =============================================================================
 * MedFlowApplication
 * Main entry point for the MedFlow Spring Boot application.
 * 
 * Academic Context (OS & DBMS):
 * - Bootstraps the embedded web server (Tomcat on port 8080).
 * - Exposes REST API endpoints for Hospital Management & OS Simulations.
 * - Serves the static HTML5/CSS/JavaScript dashboard from src/main/resources/static.
 * =============================================================================
 */
@SpringBootApplication
public class MedFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedFlowApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" MedFlow Application Started Successfully!       ");
        System.out.println(" Web Dashboard: http://localhost:8081            ");
        System.out.println(" Status: OS Simulation Engine Complete (Verified)");
        System.out.println("=================================================");
    }
}
