package com.pharmacy.action;

import com.pharmacy.model.Medicine;
import com.pharmacy.repository.MedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MedicineActionTest {
    
    private MedicineAction action;
    private MedicineRepository repository;
    
    @BeforeEach
    public void setUp() {
        action = new MedicineAction();
        repository = MedicineRepository.getInstance();
    }
    
    @Test
    public void testList() {
        // When
        String result = action.list();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getMedicines()).isNotNull();
        assertThat(action.getMedicines()).isNotEmpty();
    }
    
    @Test
    public void testViewWithValidId() {
        // Given
        action.setMedicineId("MED001");
        
        // When
        String result = action.view();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getMedicine()).isNotNull();
        assertThat(action.getMedicine().getId()).isEqualTo("MED001");
    }
    
    @Test
    public void testViewWithInvalidId() {
        // Given
        action.setMedicineId("MED999");
        
        // When
        String result = action.view();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testViewWithNullId() {
        // Given
        action.setMedicineId(null);
        
        // When
        String result = action.view();
        
        // Then
        assertThat(result).isEqualTo("success");
    }
    
    @Test
    public void testSearchWithQuery() {
        // Given
        action.setSearchQuery("Amoxicillin");
        
        // When
        String result = action.search();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getMedicines()).isNotNull();
        assertThat(action.getMedicines()).isNotEmpty();
    }
    
    @Test
    public void testSearchWithEmptyQuery() {
        // Given
        action.setSearchQuery("");
        
        // When
        String result = action.search();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getMedicines()).isNotNull();
        assertThat(action.getMedicines()).isNotEmpty();
    }
    
    @Test
    public void testSearchWithNullQuery() {
        // Given
        action.setSearchQuery(null);
        
        // When
        String result = action.search();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getMedicines()).isNotNull();
    }
    
    @Test
    public void testGettersAndSetters() {
        // Given
        String medicineId = "MED001";
        String searchQuery = "test";
        
        // When
        action.setMedicineId(medicineId);
        action.setSearchQuery(searchQuery);
        
        // Then
        assertThat(action.getMedicineId()).isNotNull();
        assertThat(action.getSearchQuery()).isEqualTo(searchQuery);
    }
}

// Made with Bob