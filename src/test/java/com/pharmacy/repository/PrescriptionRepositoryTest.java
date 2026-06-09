package com.pharmacy.repository;

import com.pharmacy.model.Prescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PrescriptionRepositoryTest {
    
    private PrescriptionRepository repository;
    
    @BeforeEach
    public void setUp() {
        repository = PrescriptionRepository.getInstance();
    }
    
    @Test
    public void testGetInstance() {
        // When
        PrescriptionRepository instance1 = PrescriptionRepository.getInstance();
        PrescriptionRepository instance2 = PrescriptionRepository.getInstance();
        
        // Then
        assertThat(instance1).isNotNull();
        assertThat(instance1).isSameAs(instance2);
    }
    
    @Test
    public void testGenerateId() {
        // When
        String id1 = repository.generateId();
        String id2 = repository.generateId();
        
        // Then
        assertThat(id1).isNotNull();
        assertThat(id1).startsWith("RX");
        assertThat(id2).isNotNull();
        assertThat(id2).startsWith("RX");
        assertThat(id1).isNotEqualTo(id2);
    }
    
    @Test
    public void testFindAll() {
        // When
        List<Prescription> prescriptions = repository.findAll();
        
        // Then
        assertThat(prescriptions).isNotNull();
        assertThat(prescriptions).isNotEmpty();
        assertThat(prescriptions.size()).isGreaterThanOrEqualTo(3); // Sample data
    }
    
    @Test
    public void testFindById() {
        // Given - Using sample data
        String prescriptionId = "RX001";
        
        // When
        Prescription prescription = repository.findById(prescriptionId);
        
        // Then
        assertThat(prescription).isNotNull();
        assertThat(prescription.getId()).isEqualTo(prescriptionId);
        assertThat(prescription.getPatientName()).isNotNull();
    }
    
    @Test
    public void testFindByIdNotFound() {
        // When
        Prescription prescription = repository.findById("RX99999");
        
        // Then
        assertThat(prescription).isNull();
    }
    
    @Test
    public void testAddPrescription() {
        // Given
        String prescriptionId = repository.generateId();
        Calendar cal = Calendar.getInstance();
        Date prescDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate = cal.getTime();
        
        Prescription prescription = new Prescription(
            prescriptionId,
            "Test Patient",
            "P99999",
            "Dr. Test",
            "MED001",
            "Test Medicine",
            30,
            "1 tablet twice daily",
            prescDate,
            expiryDate,
            "PENDING",
            "Test notes"
        );
        
        // When
        repository.addPrescription(prescription);
        Prescription retrieved = repository.findById(prescriptionId);
        
        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(prescriptionId);
        assertThat(retrieved.getPatientName()).isEqualTo("Test Patient");
        
        // Cleanup
        repository.deletePrescription(prescriptionId);
    }
    
    @Test
    public void testFindByPatientId() {
        // Given - Using sample data
        String patientId = "P12345";
        
        // When
        List<Prescription> prescriptions = repository.findByPatientId(patientId);
        
        // Then
        assertThat(prescriptions).isNotNull();
        assertThat(prescriptions).isNotEmpty();
        assertThat(prescriptions.get(0).getPatientId()).isEqualTo(patientId);
    }
    
    @Test
    public void testFindByPatientIdNoResults() {
        // When
        List<Prescription> prescriptions = repository.findByPatientId("P99999");
        
        // Then
        assertThat(prescriptions).isNotNull();
        assertThat(prescriptions).isEmpty();
    }
    
    @Test
    public void testFindByStatus() {
        // Given - Using sample data
        String status = "PENDING";
        
        // When
        List<Prescription> prescriptions = repository.findByStatus(status);
        
        // Then
        assertThat(prescriptions).isNotNull();
        assertThat(prescriptions).isNotEmpty();
        for (Prescription p : prescriptions) {
            assertThat(p.getStatus()).isEqualTo(status);
        }
    }
    
    @Test
    public void testFindByStatusValidated() {
        // Given
        String status = "VALIDATED";
        
        // When
        List<Prescription> prescriptions = repository.findByStatus(status);
        
        // Then
        assertThat(prescriptions).isNotNull();
        // May or may not have results depending on sample data
        for (Prescription p : prescriptions) {
            assertThat(p.getStatus()).isEqualTo(status);
        }
    }
    
    @Test
    public void testFindByStatusFulfilled() {
        // Given
        String status = "FULFILLED";
        
        // When
        List<Prescription> prescriptions = repository.findByStatus(status);
        
        // Then
        assertThat(prescriptions).isNotNull();
        for (Prescription p : prescriptions) {
            assertThat(p.getStatus()).isEqualTo(status);
        }
    }
    
    @Test
    public void testFindByStatusNoResults() {
        // When
        List<Prescription> prescriptions = repository.findByStatus("NONEXISTENT_STATUS");
        
        // Then
        assertThat(prescriptions).isNotNull();
        assertThat(prescriptions).isEmpty();
    }
    
    @Test
    public void testUpdatePrescription() {
        // Given
        String prescriptionId = repository.generateId();
        Calendar cal = Calendar.getInstance();
        Date prescDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate = cal.getTime();
        
        Prescription prescription = new Prescription(
            prescriptionId,
            "Update Test Patient",
            "P88888",
            "Dr. Update",
            "MED002",
            "Update Medicine",
            20,
            "Original dosage",
            prescDate,
            expiryDate,
            "PENDING",
            "Original notes"
        );
        repository.addPrescription(prescription);
        
        // When
        prescription.setStatus("VALIDATED");
        prescription.setNotes("Updated notes");
        prescription.setDosage("Updated dosage");
        repository.updatePrescription(prescription);
        Prescription updated = repository.findById(prescriptionId);
        
        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo("VALIDATED");
        assertThat(updated.getNotes()).isEqualTo("Updated notes");
        assertThat(updated.getDosage()).isEqualTo("Updated dosage");
        
        // Cleanup
        repository.deletePrescription(prescriptionId);
    }
    
    @Test
    public void testDeletePrescription() {
        // Given
        String prescriptionId = repository.generateId();
        Calendar cal = Calendar.getInstance();
        Date prescDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate = cal.getTime();
        
        Prescription prescription = new Prescription(
            prescriptionId,
            "Delete Test",
            "P77777",
            "Dr. Delete",
            "MED003",
            "Delete Medicine",
            15,
            "To be deleted",
            prescDate,
            expiryDate,
            "PENDING",
            "Will be deleted"
        );
        repository.addPrescription(prescription);
        assertThat(repository.findById(prescriptionId)).isNotNull();
        
        // When
        repository.deletePrescription(prescriptionId);
        
        // Then
        assertThat(repository.findById(prescriptionId)).isNull();
    }
    
    @Test
    public void testMultiplePrescriptionsForSamePatient() {
        // Given
        String patientId = "P66666";
        String prescId1 = repository.generateId();
        String prescId2 = repository.generateId();
        
        Calendar cal = Calendar.getInstance();
        Date prescDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate = cal.getTime();
        
        Prescription presc1 = new Prescription(
            prescId1, "Multi Patient", patientId, "Dr. One", "MED001", "Medicine A",
            10, "Dosage 1", prescDate, expiryDate, "PENDING", "Notes 1"
        );
        Prescription presc2 = new Prescription(
            prescId2, "Multi Patient", patientId, "Dr. Two", "MED002", "Medicine B",
            20, "Dosage 2", prescDate, expiryDate, "VALIDATED", "Notes 2"
        );
        
        repository.addPrescription(presc1);
        repository.addPrescription(presc2);
        
        // When
        List<Prescription> patientPrescriptions = repository.findByPatientId(patientId);
        
        // Then
        assertThat(patientPrescriptions).hasSize(2);
        assertThat(patientPrescriptions).contains(presc1, presc2);
        
        // Cleanup
        repository.deletePrescription(prescId1);
        repository.deletePrescription(prescId2);
    }
    
    @Test
    public void testPrescriptionWithExpiryDate() {
        // Given
        String prescriptionId = repository.generateId();
        Calendar cal = Calendar.getInstance();
        Date prescDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate = cal.getTime();
        
        Prescription prescription = new Prescription(
            prescriptionId,
            "Expiry Test",
            "P55555",
            "Dr. Expiry",
            "MED004",
            "Expiry Medicine",
            25,
            "Test dosage",
            prescDate,
            expiryDate,
            "PENDING",
            "Expiry test"
        );
        
        // When
        repository.addPrescription(prescription);
        Prescription retrieved = repository.findById(prescriptionId);
        
        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getExpiryDate()).isNotNull();
        assertThat(retrieved.getExpiryDate()).isAfter(retrieved.getPrescriptionDate());
        
        // Cleanup
        repository.deletePrescription(prescriptionId);
    }
    
    @Test
    public void testPrescriptionStatusTransition() {
        // Given
        String prescriptionId = repository.generateId();
        Calendar cal = Calendar.getInstance();
        Date prescDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate = cal.getTime();
        
        Prescription prescription = new Prescription(
            prescriptionId,
            "Status Test",
            "P44444",
            "Dr. Status",
            "MED005",
            "Status Medicine",
            18,
            "Status dosage",
            prescDate,
            expiryDate,
            "PENDING",
            "Status test"
        );
        repository.addPrescription(prescription);
        
        // When & Then - Test status transitions
        prescription.setStatus("VALIDATED");
        repository.updatePrescription(prescription);
        assertThat(repository.findById(prescriptionId).getStatus()).isEqualTo("VALIDATED");
        
        prescription.setStatus("FULFILLED");
        repository.updatePrescription(prescription);
        assertThat(repository.findById(prescriptionId).getStatus()).isEqualTo("FULFILLED");
        
        // Cleanup
        repository.deletePrescription(prescriptionId);
    }
    
    @Test
    public void testSampleDataInitialization() {
        // When
        List<Prescription> allPrescriptions = repository.findAll();
        
        // Then - Verify sample data exists
        assertThat(allPrescriptions).hasSizeGreaterThanOrEqualTo(3);
        
        // Verify RX001 exists
        Prescription rx001 = repository.findById("RX001");
        assertThat(rx001).isNotNull();
        assertThat(rx001.getPatientName()).isEqualTo("John Smith");
        
        // Verify RX002 exists
        Prescription rx002 = repository.findById("RX002");
        assertThat(rx002).isNotNull();
        assertThat(rx002.getPatientName()).isEqualTo("Mary Johnson");
        
        // Verify RX003 exists
        Prescription rx003 = repository.findById("RX003");
        assertThat(rx003).isNotNull();
        assertThat(rx003.getPatientName()).isEqualTo("Robert Davis");
    }
}

// Made with Bob