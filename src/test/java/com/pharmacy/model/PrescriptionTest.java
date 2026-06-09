package com.pharmacy.model;

import org.junit.jupiter.api.Test;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class PrescriptionTest {
    
    @Test
    public void testPrescriptionCreation() {
        // Given
        String id = "RX001";
        String patientName = "John Doe";
        String patientId = "P12345";
        String doctorName = "Dr. Smith";
        String medicineId = "MED001";
        String medicineName = "Amoxicillin";
        int quantity = 30;
        String dosage = "500mg twice daily";
        Date prescriptionDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate = cal.getTime();
        String status = "PENDING";
        String notes = "Take with food";
        
        // When
        Prescription prescription = new Prescription(id, patientName, patientId, doctorName,
                                                    medicineId, medicineName, quantity, dosage,
                                                    prescriptionDate, expiryDate, status, notes);
        
        // Then
        assertThat(prescription.getId()).isEqualTo(id);
        assertThat(prescription.getPatientName()).isEqualTo(patientName);
        assertThat(prescription.getPatientId()).isEqualTo(patientId);
        assertThat(prescription.getDoctorName()).isEqualTo(doctorName);
        assertThat(prescription.getMedicineId()).isEqualTo(medicineId);
        assertThat(prescription.getMedicineName()).isEqualTo(medicineName);
        assertThat(prescription.getQuantity()).isEqualTo(quantity);
        assertThat(prescription.getDosage()).isEqualTo(dosage);
        assertThat(prescription.getPrescriptionDate()).isEqualTo(prescriptionDate);
        assertThat(prescription.getExpiryDate()).isEqualTo(expiryDate);
        assertThat(prescription.getStatus()).isEqualTo(status);
        assertThat(prescription.getNotes()).isEqualTo(notes);
    }
    
    @Test
    public void testPrescriptionDefaultConstructor() {
        // When
        Prescription prescription = new Prescription();
        
        // Then
        assertThat(prescription).isNotNull();
        assertThat(prescription.getId()).isNull();
        assertThat(prescription.getPatientName()).isNull();
        assertThat(prescription.getStatus()).isNull();
    }
    
    @Test
    public void testPrescriptionSetters() {
        // Given
        Prescription prescription = new Prescription();
        Date prescriptionDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate = cal.getTime();
        
        // When
        prescription.setId("RX002");
        prescription.setPatientName("Jane Smith");
        prescription.setPatientId("P67890");
        prescription.setDoctorName("Dr. Johnson");
        prescription.setMedicineId("MED002");
        prescription.setMedicineName("Ibuprofen");
        prescription.setQuantity(20);
        prescription.setDosage("200mg three times daily");
        prescription.setPrescriptionDate(prescriptionDate);
        prescription.setExpiryDate(expiryDate);
        prescription.setStatus("VALIDATED");
        prescription.setNotes("Take after meals");
        
        // Then
        assertThat(prescription.getId()).isEqualTo("RX002");
        assertThat(prescription.getPatientName()).isEqualTo("Jane Smith");
        assertThat(prescription.getPatientId()).isEqualTo("P67890");
        assertThat(prescription.getDoctorName()).isEqualTo("Dr. Johnson");
        assertThat(prescription.getMedicineId()).isEqualTo("MED002");
        assertThat(prescription.getMedicineName()).isEqualTo("Ibuprofen");
        assertThat(prescription.getQuantity()).isEqualTo(20);
        assertThat(prescription.getDosage()).isEqualTo("200mg three times daily");
        assertThat(prescription.getPrescriptionDate()).isEqualTo(prescriptionDate);
        assertThat(prescription.getExpiryDate()).isEqualTo(expiryDate);
        assertThat(prescription.getStatus()).isEqualTo("VALIDATED");
        assertThat(prescription.getNotes()).isEqualTo("Take after meals");
    }
    
    @Test
    public void testPrescriptionStatusValues() {
        // Given
        Prescription prescription = new Prescription();
        
        // When & Then - Test different status values
        prescription.setStatus("PENDING");
        assertThat(prescription.getStatus()).isEqualTo("PENDING");
        
        prescription.setStatus("VALIDATED");
        assertThat(prescription.getStatus()).isEqualTo("VALIDATED");
        
        prescription.setStatus("FULFILLED");
        assertThat(prescription.getStatus()).isEqualTo("FULFILLED");
        
        prescription.setStatus("EXPIRED");
        assertThat(prescription.getStatus()).isEqualTo("EXPIRED");
    }
    
    @Test
    public void testPrescriptionDateHandling() {
        // Given
        Prescription prescription = new Prescription();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date futureDate = cal.getTime();
        
        // When
        prescription.setPrescriptionDate(now);
        prescription.setExpiryDate(futureDate);
        
        // Then
        assertThat(prescription.getPrescriptionDate()).isNotNull();
        assertThat(prescription.getExpiryDate()).isNotNull();
        assertThat(prescription.getExpiryDate()).isAfter(prescription.getPrescriptionDate());
    }
    
    @Test
    public void testPrescriptionQuantityHandling() {
        // Given
        Prescription prescription = new Prescription();
        
        // When
        prescription.setQuantity(60);
        
        // Then
        assertThat(prescription.getQuantity()).isEqualTo(60);
        assertThat(prescription.getQuantity()).isGreaterThan(0);
    }
    
    @Test
    public void testPrescriptionDosageInstructions() {
        // Given
        Prescription prescription = new Prescription();
        String dosageInstruction = "Take 1 tablet twice daily with meals";
        
        // When
        prescription.setDosage(dosageInstruction);
        
        // Then
        assertThat(prescription.getDosage()).isEqualTo(dosageInstruction);
        assertThat(prescription.getDosage()).contains("twice daily");
    }
    
    @Test
    public void testPrescriptionWithNullValues() {
        // Given
        Prescription prescription = new Prescription();
        
        // When
        prescription.setNotes(null);
        prescription.setDosage(null);
        
        // Then
        assertThat(prescription.getNotes()).isNull();
        assertThat(prescription.getDosage()).isNull();
    }
    
    @Test
    public void testPrescriptionPatientInformation() {
        // Given
        Prescription prescription = new Prescription();
        
        // When
        prescription.setPatientName("Robert Davis");
        prescription.setPatientId("P99999");
        
        // Then
        assertThat(prescription.getPatientName()).isEqualTo("Robert Davis");
        assertThat(prescription.getPatientId()).isEqualTo("P99999");
        assertThat(prescription.getPatientId()).startsWith("P");
    }
    
    @Test
    public void testPrescriptionDoctorInformation() {
        // Given
        Prescription prescription = new Prescription();
        
        // When
        prescription.setDoctorName("Dr. Emily White");
        
        // Then
        assertThat(prescription.getDoctorName()).isEqualTo("Dr. Emily White");
        assertThat(prescription.getDoctorName()).startsWith("Dr.");
    }
    
    @Test
    public void testPrescriptionMedicineInformation() {
        // Given
        Prescription prescription = new Prescription();
        
        // When
        prescription.setMedicineId("MED005");
        prescription.setMedicineName("Metformin 850mg");
        
        // Then
        assertThat(prescription.getMedicineId()).isEqualTo("MED005");
        assertThat(prescription.getMedicineName()).isEqualTo("Metformin 850mg");
        assertThat(prescription.getMedicineId()).startsWith("MED");
    }
}

// Made with Bob