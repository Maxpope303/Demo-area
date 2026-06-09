package com.pharmacy.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {
    
    @Test
    public void testOrderCreation() {
        // Given
        String id = "ORD001";
        String prescriptionId = "RX001";
        String patientName = "John Doe";
        String patientId = "P12345";
        String medicineId = "MED001";
        String medicineName = "Aspirin";
        int quantity = 30;
        BigDecimal totalAmount = new BigDecimal("29.99");
        Date orderDate = new Date();
        String status = "PENDING";
        String paymentMethod = "CASH";
        String pharmacistNotes = "Take with food";
        
        // When
        Order order = new Order(id, prescriptionId, patientName, patientId,
                               medicineId, medicineName, quantity, totalAmount,
                               orderDate, status, paymentMethod, pharmacistNotes);
        
        // Then
        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getPrescriptionId()).isEqualTo(prescriptionId);
        assertThat(order.getPatientName()).isEqualTo(patientName);
        assertThat(order.getPatientId()).isEqualTo(patientId);
        assertThat(order.getMedicineId()).isEqualTo(medicineId);
        assertThat(order.getMedicineName()).isEqualTo(medicineName);
        assertThat(order.getQuantity()).isEqualTo(quantity);
        assertThat(order.getTotalAmount()).isEqualTo(totalAmount);
        assertThat(order.getOrderDate()).isEqualTo(orderDate);
        assertThat(order.getStatus()).isEqualTo(status);
        assertThat(order.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(order.getPharmacistNotes()).isEqualTo(pharmacistNotes);
    }
    
    @Test
    public void testOrderDefaultConstructor() {
        // When
        Order order = new Order();
        
        // Then
        assertThat(order).isNotNull();
        assertThat(order.getId()).isNull();
        assertThat(order.getPatientName()).isNull();
        assertThat(order.getStatus()).isNull();
    }
    
    @Test
    public void testOrderSetters() {
        // Given
        Order order = new Order();
        
        // When
        order.setId("ORD002");
        order.setPrescriptionId("RX002");
        order.setPatientName("Jane Smith");
        order.setPatientId("P67890");
        order.setMedicineId("MED002");
        order.setMedicineName("Ibuprofen");
        order.setQuantity(20);
        order.setTotalAmount(new BigDecimal("19.99"));
        Date orderDate = new Date();
        order.setOrderDate(orderDate);
        order.setStatus("VALIDATED");
        order.setPaymentMethod("CREDIT_CARD");
        order.setPharmacistNotes("No food restrictions");
        
        // Then
        assertThat(order.getId()).isEqualTo("ORD002");
        assertThat(order.getPrescriptionId()).isEqualTo("RX002");
        assertThat(order.getPatientName()).isEqualTo("Jane Smith");
        assertThat(order.getPatientId()).isEqualTo("P67890");
        assertThat(order.getMedicineId()).isEqualTo("MED002");
        assertThat(order.getMedicineName()).isEqualTo("Ibuprofen");
        assertThat(order.getQuantity()).isEqualTo(20);
        assertThat(order.getTotalAmount()).isEqualTo(new BigDecimal("19.99"));
        assertThat(order.getOrderDate()).isEqualTo(orderDate);
        assertThat(order.getStatus()).isEqualTo("VALIDATED");
        assertThat(order.getPaymentMethod()).isEqualTo("CREDIT_CARD");
        assertThat(order.getPharmacistNotes()).isEqualTo("No food restrictions");
    }
    
    @Test
    public void testOrderStatusValues() {
        // Given
        Order order = new Order();
        
        // When & Then - Test different status values
        order.setStatus("PENDING");
        assertThat(order.getStatus()).isEqualTo("PENDING");
        
        order.setStatus("VALIDATED");
        assertThat(order.getStatus()).isEqualTo("VALIDATED");
        
        order.setStatus("PAID");
        assertThat(order.getStatus()).isEqualTo("PAID");
        
        order.setStatus("COLLECTED");
        assertThat(order.getStatus()).isEqualTo("COLLECTED");
        
        order.setStatus("CANCELLED");
        assertThat(order.getStatus()).isEqualTo("CANCELLED");
    }
    
    @Test
    public void testOrderPaymentMethods() {
        // Given
        Order order = new Order();
        
        // When & Then - Test different payment methods
        order.setPaymentMethod("CASH");
        assertThat(order.getPaymentMethod()).isEqualTo("CASH");
        
        order.setPaymentMethod("CREDIT_CARD");
        assertThat(order.getPaymentMethod()).isEqualTo("CREDIT_CARD");
        
        order.setPaymentMethod("INSURANCE");
        assertThat(order.getPaymentMethod()).isEqualTo("INSURANCE");
    }
    
    @Test
    public void testOrderTotalAmountCalculation() {
        // Given
        Order order = new Order();
        BigDecimal unitPrice = new BigDecimal("9.99");
        int quantity = 10;
        BigDecimal expectedTotal = unitPrice.multiply(new BigDecimal(quantity));
        
        // When
        order.setQuantity(quantity);
        order.setTotalAmount(expectedTotal);
        
        // Then
        assertThat(order.getTotalAmount()).isEqualTo(new BigDecimal("99.90"));
        assertThat(order.getQuantity()).isEqualTo(10);
    }
    
    @Test
    public void testOrderDateHandling() {
        // Given
        Order order = new Order();
        Date now = new Date();
        
        // When
        order.setOrderDate(now);
        
        // Then
        assertThat(order.getOrderDate()).isNotNull();
        assertThat(order.getOrderDate()).isEqualTo(now);
    }
    
    @Test
    public void testOrderWithNullValues() {
        // Given
        Order order = new Order();
        
        // When
        order.setPharmacistNotes(null);
        order.setPaymentMethod(null);
        
        // Then
        assertThat(order.getPharmacistNotes()).isNull();
        assertThat(order.getPaymentMethod()).isNull();
    }
}

// Made with Bob