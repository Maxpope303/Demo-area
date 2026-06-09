package com.pharmacy.action;

import com.pharmacy.model.Prescription;
import com.pharmacy.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PrescriptionActionTest {
    
    private PrescriptionAction action;
    private PrescriptionRepository repository;
    
    @BeforeEach
    public void setUp() {
        action = new PrescriptionAction();
        repository = PrescriptionRepository.getInstance();
    }
    
    @Test
    public void testList() {
        // When
        String result = action.list();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getPrescriptions()).isNotNull();
        assertThat(action.getPrescriptions()).isNotEmpty();
    }
    
    @Test
    public void testViewWithValidId() {
        // Given
        action.setPrescriptionId("RX001");
        
        // When
        String result = action.view();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getPrescription()).isNotNull();
        assertThat(action.getPrescription().getId()).isEqualTo("RX001");
    }
    
    @Test
    public void testViewWithInvalidId() {
        // Given
        action.setPrescriptionId("RX99999");
        
        // When
        String result = action.view();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testViewWithNullId() {
        // Given
        action.setPrescriptionId(null);
        
        // When
        String result = action.view();
        
        // Then
        assertThat(result).isEqualTo("success");
    }
    
    @Test
    public void testCreate() {
        // When
        String result = action.create();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getMedicines()).isNotNull();
        assertThat(action.getMedicines()).isNotEmpty();
    }
    
    @Test
    public void testSaveWithValidData() {
        // Given
        action.setPatientName("Test Patient");
        action.setPatientId("P99999");
        action.setDoctorName("Dr. Test");
        action.setMedicineId("MED001");
        action.setQuantity(30);
        action.setDosage("1 tablet twice daily");
        action.setNotes("Test notes");
        
        // When
        String result = action.save();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.hasActionMessages()).isTrue();
    }
    
    @Test
    public void testSaveWithInvalidMedicine() {
        // Given
        action.setPatientName("Test Patient");
        action.setPatientId("P99999");
        action.setDoctorName("Dr. Test");
        action.setMedicineId("MED99999");
        action.setQuantity(30);
        action.setDosage("1 tablet twice daily");
        
        // When
        String result = action.save();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testValidatePrescriptionWithValidId() {
        // Given
        action.setPrescriptionId("RX001");
        
        // When
        String result = action.validatePrescription();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.hasActionMessages()).isTrue();
        
        // Verify status changed
        Prescription prescription = repository.findById("RX001");
        assertThat(prescription.getStatus()).isEqualTo("VALIDATED");
        
        // Cleanup - reset to PENDING
        prescription.setStatus("PENDING");
        repository.updatePrescription(prescription);
    }
    
    @Test
    public void testValidatePrescriptionWithInvalidId() {
        // Given
        action.setPrescriptionId("RX99999");
        
        // When
        String result = action.validatePrescription();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testValidatePrescriptionWithNullId() {
        // Given
        action.setPrescriptionId(null);
        
        // When
        String result = action.validatePrescription();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testGettersAndSetters() {
        // Given
        String prescriptionId = "RX001";
        String patientName = "John Doe";
        String patientId = "P12345";
        String doctorName = "Dr. Smith";
        String medicineId = "MED001";
        int quantity = 30;
        String dosage = "Test dosage";
        String notes = "Test notes";
        
        // When
        action.setPrescriptionId(prescriptionId);
        action.setPatientName(patientName);
        action.setPatientId(patientId);
        action.setDoctorName(doctorName);
        action.setMedicineId(medicineId);
        action.setQuantity(quantity);
        action.setDosage(dosage);
        action.setNotes(notes);
        
        // Then
        assertThat(action.getPrescriptionId()).isEqualTo(prescriptionId);
        assertThat(action.getPatientName()).isEqualTo(patientName);
        assertThat(action.getPatientId()).isEqualTo(patientId);
        assertThat(action.getDoctorName()).isEqualTo(doctorName);
        assertThat(action.getMedicineId()).isEqualTo(medicineId);
        assertThat(action.getQuantity()).isEqualTo(quantity);
        assertThat(action.getDosage()).isEqualTo(dosage);
        assertThat(action.getNotes()).isEqualTo(notes);
    }
}

// Made with Bob