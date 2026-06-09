package com.pharmacy.repository;

import com.pharmacy.model.Medicine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MedicineRepositoryTest {
    
    private MedicineRepository repository;
    
    @BeforeEach
    public void setUp() {
        repository = MedicineRepository.getInstance();
    }
    
    @Test
    public void testGetInstance() {
        // When
        MedicineRepository instance1 = MedicineRepository.getInstance();
        MedicineRepository instance2 = MedicineRepository.getInstance();
        
        // Then
        assertThat(instance1).isNotNull();
        assertThat(instance1).isSameAs(instance2);
    }
    
    @Test
    public void testFindAll() {
        // When
        List<Medicine> medicines = repository.findAll();
        
        // Then
        assertThat(medicines).isNotNull();
        assertThat(medicines).isNotEmpty();
        assertThat(medicines.size()).isGreaterThan(0);
    }
    
    @Test
    public void testFindById() {
        // Given
        String medicineId = "MED001";
        
        // When
        Medicine medicine = repository.findById(medicineId);
        
        // Then
        assertThat(medicine).isNotNull();
        assertThat(medicine.getId()).isEqualTo(medicineId);
        assertThat(medicine.getName()).isNotNull();
    }
    
    @Test
    public void testFindByIdNotFound() {
        // Given
        String nonExistentId = "MED999";
        
        // When
        Medicine medicine = repository.findById(nonExistentId);
        
        // Then
        assertThat(medicine).isNull();
    }
    
    @Test
    public void testSearchByName() {
        // Given
        String searchQuery = "Amoxicillin";
        
        // When
        List<Medicine> results = repository.searchByName(searchQuery);
        
        // Then
        assertThat(results).isNotNull();
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getName()).containsIgnoringCase(searchQuery);
    }
    
    @Test
    public void testSearchByNameCaseInsensitive() {
        // Given
        String searchQuery = "amoxicillin";
        
        // When
        List<Medicine> results = repository.searchByName(searchQuery);
        
        // Then
        assertThat(results).isNotNull();
        assertThat(results).isNotEmpty();
    }
    
    @Test
    public void testSearchByNamePartialMatch() {
        // Given
        String searchQuery = "Amox";
        
        // When
        List<Medicine> results = repository.searchByName(searchQuery);
        
        // Then
        assertThat(results).isNotNull();
        assertThat(results).isNotEmpty();
    }
    
    @Test
    public void testSearchByNameNoResults() {
        // Given
        String searchQuery = "NonExistentMedicine";
        
        // When
        List<Medicine> results = repository.searchByName(searchQuery);
        
        // Then
        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
    }
    
    @Test
    public void testAddMedicine() {
        // Given
        Medicine newMedicine = new Medicine(
            "MED999",
            "Test Medicine",
            "Test Description",
            new BigDecimal("25.00"),
            50,
            "TestManufacturer"
        );
        
        // When
        repository.addMedicine(newMedicine);
        Medicine retrieved = repository.findById("MED999");
        
        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("Test Medicine");
        
        // Cleanup
        repository.deleteMedicine("MED999");
    }
    
    @Test
    public void testUpdateMedicine() {
        // Given
        Medicine medicine = repository.findById("MED001");
        assertThat(medicine).isNotNull();
        
        String originalName = medicine.getName();
        medicine.setName("Updated Medicine Name");
        
        // When
        repository.updateMedicine(medicine);
        Medicine updated = repository.findById("MED001");
        
        // Then
        assertThat(updated.getName()).isEqualTo("Updated Medicine Name");
        
        // Cleanup - restore original
        medicine.setName(originalName);
        repository.updateMedicine(medicine);
    }
    
    @Test
    public void testUpdateStockSuccess() {
        // Given
        Medicine medicine = repository.findById("MED001");
        assertThat(medicine).isNotNull();
        int originalStock = medicine.getStockQuantity();
        int quantityToReduce = 10;
        
        // When
        boolean result = repository.updateStock("MED001", quantityToReduce);
        Medicine updated = repository.findById("MED001");
        
        // Then
        assertThat(result).isTrue();
        assertThat(updated.getStockQuantity()).isEqualTo(originalStock - quantityToReduce);
        
        // Cleanup - restore original stock
        updated.setStockQuantity(originalStock);
        repository.updateMedicine(updated);
    }
    
    @Test
    public void testUpdateStockInsufficientQuantity() {
        // Given
        Medicine medicine = repository.findById("MED001");
        assertThat(medicine).isNotNull();
        int originalStock = medicine.getStockQuantity();
        int excessiveQuantity = originalStock + 1000;
        
        // When
        boolean result = repository.updateStock("MED001", excessiveQuantity);
        
        // Then
        assertThat(result).isFalse();
        
        // Verify stock unchanged
        Medicine unchanged = repository.findById("MED001");
        assertThat(unchanged.getStockQuantity()).isEqualTo(originalStock);
    }
    
    @Test
    public void testUpdateStockNonExistentMedicine() {
        // When
        boolean result = repository.updateStock("MED999", 10);
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    public void testDeleteMedicine() {
        // Given
        Medicine testMedicine = new Medicine(
            "MED998",
            "Medicine To Delete",
            "Test",
            new BigDecimal("10.00"),
            10,
            "Test"
        );
        repository.addMedicine(testMedicine);
        assertThat(repository.findById("MED998")).isNotNull();
        
        // When
        repository.deleteMedicine("MED998");
        
        // Then
        assertThat(repository.findById("MED998")).isNull();
    }
}

// Made with Bob