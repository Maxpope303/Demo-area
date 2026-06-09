package com.pharmacy.repository;

import com.pharmacy.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderRepositoryTest {
    
    private OrderRepository repository;
    
    @BeforeEach
    public void setUp() {
        repository = OrderRepository.getInstance();
    }
    
    @Test
    public void testGetInstance() {
        // When
        OrderRepository instance1 = OrderRepository.getInstance();
        OrderRepository instance2 = OrderRepository.getInstance();
        
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
        assertThat(id1).startsWith("ORD");
        assertThat(id2).isNotNull();
        assertThat(id2).startsWith("ORD");
        assertThat(id1).isNotEqualTo(id2);
    }
    
    @Test
    public void testAddOrder() {
        // Given
        String orderId = repository.generateId();
        Order order = new Order(
            orderId,
            "RX001",
            "Test Patient",
            "P99999",
            "MED001",
            "Test Medicine",
            10,
            new BigDecimal("99.99"),
            new Date(),
            "PENDING",
            "CASH",
            "Test notes"
        );
        
        // When
        repository.addOrder(order);
        Order retrieved = repository.findById(orderId);
        
        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(orderId);
        assertThat(retrieved.getPatientName()).isEqualTo("Test Patient");
        
        // Cleanup
        repository.deleteOrder(orderId);
    }
    
    @Test
    public void testFindById() {
        // Given
        String orderId = repository.generateId();
        Order order = new Order(
            orderId,
            "RX002",
            "John Doe",
            "P12345",
            "MED002",
            "Aspirin",
            20,
            new BigDecimal("19.99"),
            new Date(),
            "VALIDATED",
            "CREDIT_CARD",
            "Take with food"
        );
        repository.addOrder(order);
        
        // When
        Order found = repository.findById(orderId);
        
        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(orderId);
        assertThat(found.getPatientName()).isEqualTo("John Doe");
        
        // Cleanup
        repository.deleteOrder(orderId);
    }
    
    @Test
    public void testFindByIdNotFound() {
        // When
        Order order = repository.findById("ORD99999");
        
        // Then
        assertThat(order).isNull();
    }
    
    @Test
    public void testFindAll() {
        // Given
        int initialSize = repository.findAll().size();
        String orderId = repository.generateId();
        Order order = new Order(
            orderId,
            "RX003",
            "Jane Smith",
            "P67890",
            "MED003",
            "Ibuprofen",
            15,
            new BigDecimal("29.99"),
            new Date(),
            "PENDING",
            "INSURANCE",
            "No restrictions"
        );
        repository.addOrder(order);
        
        // When
        List<Order> orders = repository.findAll();
        
        // Then
        assertThat(orders).isNotNull();
        assertThat(orders.size()).isEqualTo(initialSize + 1);
        assertThat(orders).contains(order);
        
        // Cleanup
        repository.deleteOrder(orderId);
    }
    
    @Test
    public void testFindByPatientId() {
        // Given
        String patientId = "P11111";
        String orderId1 = repository.generateId();
        String orderId2 = repository.generateId();
        
        Order order1 = new Order(
            orderId1, "RX004", "Patient One", patientId, "MED001", "Medicine A",
            10, new BigDecimal("10.00"), new Date(), "PENDING", "CASH", "Notes 1"
        );
        Order order2 = new Order(
            orderId2, "RX005", "Patient One", patientId, "MED002", "Medicine B",
            20, new BigDecimal("20.00"), new Date(), "VALIDATED", "CASH", "Notes 2"
        );
        
        repository.addOrder(order1);
        repository.addOrder(order2);
        
        // When
        List<Order> orders = repository.findByPatientId(patientId);
        
        // Then
        assertThat(orders).isNotNull();
        assertThat(orders.size()).isGreaterThanOrEqualTo(2);
        assertThat(orders).contains(order1, order2);
        
        // Cleanup
        repository.deleteOrder(orderId1);
        repository.deleteOrder(orderId2);
    }
    
    @Test
    public void testFindByPatientIdNoResults() {
        // When
        List<Order> orders = repository.findByPatientId("P99999");
        
        // Then
        assertThat(orders).isNotNull();
        assertThat(orders).isEmpty();
    }
    
    @Test
    public void testFindByStatus() {
        // Given
        String orderId = repository.generateId();
        Order order = new Order(
            orderId, "RX006", "Test Patient", "P22222", "MED003", "Medicine C",
            5, new BigDecimal("50.00"), new Date(), "PAID", "CREDIT_CARD", "Paid order"
        );
        repository.addOrder(order);
        
        // When
        List<Order> orders = repository.findByStatus("PAID");
        
        // Then
        assertThat(orders).isNotNull();
        assertThat(orders).isNotEmpty();
        assertThat(orders).contains(order);
        
        // Cleanup
        repository.deleteOrder(orderId);
    }
    
    @Test
    public void testFindByStatusNoResults() {
        // When
        List<Order> orders = repository.findByStatus("NONEXISTENT_STATUS");
        
        // Then
        assertThat(orders).isNotNull();
        assertThat(orders).isEmpty();
    }
    
    @Test
    public void testFindByPrescriptionId() {
        // Given
        String prescriptionId = "RX999";
        String orderId = repository.generateId();
        Order order = new Order(
            orderId, prescriptionId, "Test Patient", "P33333", "MED004", "Medicine D",
            8, new BigDecimal("80.00"), new Date(), "COLLECTED", "CASH", "Collected"
        );
        repository.addOrder(order);
        
        // When
        List<Order> orders = repository.findByPrescriptionId(prescriptionId);
        
        // Then
        assertThat(orders).isNotNull();
        assertThat(orders).isNotEmpty();
        assertThat(orders).contains(order);
        assertThat(orders.get(0).getPrescriptionId()).isEqualTo(prescriptionId);
        
        // Cleanup
        repository.deleteOrder(orderId);
    }
    
    @Test
    public void testFindByPrescriptionIdNoResults() {
        // When
        List<Order> orders = repository.findByPrescriptionId("RX99999");
        
        // Then
        assertThat(orders).isNotNull();
        assertThat(orders).isEmpty();
    }
    
    @Test
    public void testUpdateOrder() {
        // Given
        String orderId = repository.generateId();
        Order order = new Order(
            orderId, "RX007", "Update Test", "P44444", "MED005", "Medicine E",
            12, new BigDecimal("120.00"), new Date(), "PENDING", "CASH", "Original notes"
        );
        repository.addOrder(order);
        
        // When
        order.setStatus("VALIDATED");
        order.setPharmacistNotes("Updated notes");
        repository.updateOrder(order);
        Order updated = repository.findById(orderId);
        
        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo("VALIDATED");
        assertThat(updated.getPharmacistNotes()).isEqualTo("Updated notes");
        
        // Cleanup
        repository.deleteOrder(orderId);
    }
    
    @Test
    public void testDeleteOrder() {
        // Given
        String orderId = repository.generateId();
        Order order = new Order(
            orderId, "RX008", "Delete Test", "P55555", "MED006", "Medicine F",
            7, new BigDecimal("70.00"), new Date(), "CANCELLED", "CASH", "To be deleted"
        );
        repository.addOrder(order);
        assertThat(repository.findById(orderId)).isNotNull();
        
        // When
        repository.deleteOrder(orderId);
        
        // Then
        assertThat(repository.findById(orderId)).isNull();
    }
    
    @Test
    public void testMultipleOrdersForSamePatient() {
        // Given
        String patientId = "P66666";
        String orderId1 = repository.generateId();
        String orderId2 = repository.generateId();
        String orderId3 = repository.generateId();
        
        Order order1 = new Order(orderId1, "RX009", "Multi Patient", patientId, "MED001", "Med 1",
            5, new BigDecimal("50.00"), new Date(), "PENDING", "CASH", "Order 1");
        Order order2 = new Order(orderId2, "RX010", "Multi Patient", patientId, "MED002", "Med 2",
            10, new BigDecimal("100.00"), new Date(), "VALIDATED", "CREDIT_CARD", "Order 2");
        Order order3 = new Order(orderId3, "RX011", "Multi Patient", patientId, "MED003", "Med 3",
            15, new BigDecimal("150.00"), new Date(), "PAID", "INSURANCE", "Order 3");
        
        repository.addOrder(order1);
        repository.addOrder(order2);
        repository.addOrder(order3);
        
        // When
        List<Order> patientOrders = repository.findByPatientId(patientId);
        
        // Then
        assertThat(patientOrders).hasSize(3);
        assertThat(patientOrders).contains(order1, order2, order3);
        
        // Cleanup
        repository.deleteOrder(orderId1);
        repository.deleteOrder(orderId2);
        repository.deleteOrder(orderId3);
    }
}

// Made with Bob