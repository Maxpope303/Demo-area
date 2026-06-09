package com.pharmacy.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class MedicineTest {
    
    @Test
    public void testMedicineCreation() {
        // Given
        String id = "MED001";
        String name = "Aspirin";
        String description = "Pain reliever";
        BigDecimal price = new BigDecimal("9.99");
        int stockQuantity = 100;
        String manufacturer = "PharmaCorp";
        
        // When
        Medicine medicine = new Medicine(id, name, description, price, stockQuantity, manufacturer);
        
        // Then
        assertThat(medicine.getId()).isEqualTo(id);
        assertThat(medicine.getName()).isEqualTo(name);
        assertThat(medicine.getDescription()).isEqualTo(description);
        assertThat(medicine.getPrice()).isEqualTo(price);
        assertThat(medicine.getStockQuantity()).isEqualTo(stockQuantity);
        assertThat(medicine.getManufacturer()).isEqualTo(manufacturer);
    }
    
    @Test
    public void testMedicineDefaultConstructor() {
        // When
        Medicine medicine = new Medicine();
        
        // Then
        assertThat(medicine).isNotNull();
        assertThat(medicine.getId()).isNull();
        assertThat(medicine.getName()).isNull();
    }
    
    @Test
    public void testMedicineSetters() {
        // Given
        Medicine medicine = new Medicine();
        
        // When
        medicine.setId("MED002");
        medicine.setName("Ibuprofen");
        medicine.setDescription("Anti-inflammatory");
        medicine.setPrice(new BigDecimal("12.50"));
        medicine.setStockQuantity(50);
        medicine.setManufacturer("HealthMeds");
        
        // Then
        assertThat(medicine.getId()).isEqualTo("MED002");
        assertThat(medicine.getName()).isEqualTo("Ibuprofen");
        assertThat(medicine.getDescription()).isEqualTo("Anti-inflammatory");
        assertThat(medicine.getPrice()).isEqualTo(new BigDecimal("12.50"));
        assertThat(medicine.getStockQuantity()).isEqualTo(50);
        assertThat(medicine.getManufacturer()).isEqualTo("HealthMeds");
    }
    
    @Test
    public void testMedicinePriceHandling() {
        // Given
        Medicine medicine = new Medicine();
        BigDecimal price = new BigDecimal("19.99");
        
        // When
        medicine.setPrice(price);
        
        // Then
        assertThat(medicine.getPrice()).isEqualTo(price);
        assertThat(medicine.getPrice().compareTo(new BigDecimal("19.99"))).isEqualTo(0);
    }
    
    @Test
    public void testMedicineStockQuantity() {
        // Given
        Medicine medicine = new Medicine();
        
        // When
        medicine.setStockQuantity(100);
        
        // Then
        assertThat(medicine.getStockQuantity()).isEqualTo(100);
        assertThat(medicine.getStockQuantity()).isGreaterThan(0);
    }
}

// Made with Bob