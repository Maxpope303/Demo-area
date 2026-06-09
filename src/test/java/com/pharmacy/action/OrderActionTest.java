package com.pharmacy.action;

import com.pharmacy.model.Order;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderActionTest {
    
    private OrderAction action;
    private OrderRepository orderRepository;
    private PrescriptionRepository prescriptionRepository;
    
    @BeforeEach
    public void setUp() {
        action = new OrderAction();
        orderRepository = OrderRepository.getInstance();
        prescriptionRepository = PrescriptionRepository.getInstance();
    }
    
    @Test
    public void testList() {
        // When
        String result = action.list();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getOrders()).isNotNull();
    }
    
    @Test
    public void testViewWithValidId() {
        // Given - Create a test order first
        String orderId = orderRepository.generateId();
        Order testOrder = new Order(orderId, "RX001", "Test Patient", "P12345", 
            "MED001", "Test Med", 10, java.math.BigDecimal.TEN, 
            new java.util.Date(), "PENDING", "CASH", "Test");
        orderRepository.addOrder(testOrder);
        
        action.setOrderId(orderId);
        
        // When
        String result = action.view();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getOrder()).isNotNull();
        assertThat(action.getOrder().getId()).isEqualTo(orderId);
        
        // Cleanup
        orderRepository.deleteOrder(orderId);
    }
    
    @Test
    public void testViewWithInvalidId() {
        // Given
        action.setOrderId("ORD99999");
        
        // When
        String result = action.view();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testViewWithNullId() {
        // Given
        action.setOrderId(null);
        
        // When
        String result = action.view();
        
        // Then
        assertThat(result).isEqualTo("success");
    }
    
    @Test
    public void testCreateFromPrescriptionWithNullId() {
        // Given
        action.setPrescriptionId(null);
        
        // When
        String result = action.createFromPrescription();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testCreateFromPrescriptionWithInvalidId() {
        // Given
        action.setPrescriptionId("RX99999");
        
        // When
        String result = action.createFromPrescription();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testCreateFromPrescriptionWithNonValidatedPrescription() {
        // Given - RX001 should be PENDING
        Prescription prescription = prescriptionRepository.findById("RX001");
        String originalStatus = prescription.getStatus();
        prescription.setStatus("PENDING");
        prescriptionRepository.updatePrescription(prescription);
        
        action.setPrescriptionId("RX001");
        
        // When
        String result = action.createFromPrescription();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
        
        // Cleanup
        prescription.setStatus(originalStatus);
        prescriptionRepository.updatePrescription(prescription);
    }
    
    @Test
    public void testProcessPaymentWithNullOrderId() {
        // Given
        action.setOrderId(null);
        
        // When
        String result = action.processPayment();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testProcessPaymentWithInvalidOrderId() {
        // Given
        action.setOrderId("ORD99999");
        
        // When
        String result = action.processPayment();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testProcessPaymentWithNullPaymentMethod() {
        // Given - Create a test order
        String orderId = orderRepository.generateId();
        Order testOrder = new Order(orderId, "RX001", "Test Patient", "P12345", 
            "MED001", "Test Med", 10, java.math.BigDecimal.TEN, 
            new java.util.Date(), "PENDING", null, "Test");
        orderRepository.addOrder(testOrder);
        
        action.setOrderId(orderId);
        action.setPaymentMethod(null);
        
        // When
        String result = action.processPayment();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
        
        // Cleanup
        orderRepository.deleteOrder(orderId);
    }
    
    @Test
    public void testCollectWithNullOrderId() {
        // Given
        action.setOrderId(null);
        
        // When
        String result = action.collect();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testCollectWithInvalidOrderId() {
        // Given
        action.setOrderId("ORD99999");
        
        // When
        String result = action.collect();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
    }
    
    @Test
    public void testCollectWithUnpaidOrder() {
        // Given - Create a test order with PENDING status
        String orderId = orderRepository.generateId();
        Order testOrder = new Order(orderId, "RX001", "Test Patient", "P12345", 
            "MED001", "Test Med", 10, java.math.BigDecimal.TEN, 
            new java.util.Date(), "PENDING", "CASH", "Test");
        orderRepository.addOrder(testOrder);
        
        action.setOrderId(orderId);
        
        // When
        String result = action.collect();
        
        // Then
        assertThat(result).isEqualTo("error");
        assertThat(action.hasActionErrors()).isTrue();
        
        // Cleanup
        orderRepository.deleteOrder(orderId);
    }
    
    @Test
    public void testCollectWithPaidOrder() {
        // Given - Create a test order with PAID status
        String orderId = orderRepository.generateId();
        Order testOrder = new Order(orderId, "RX001", "Test Patient", "P12345", 
            "MED001", "Test Med", 10, java.math.BigDecimal.TEN, 
            new java.util.Date(), "PAID", "CASH", "Test");
        orderRepository.addOrder(testOrder);
        
        action.setOrderId(orderId);
        
        // When
        String result = action.collect();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.hasActionMessages()).isTrue();
        
        // Verify status changed
        Order updatedOrder = orderRepository.findById(orderId);
        assertThat(updatedOrder.getStatus()).isEqualTo("COLLECTED");
        
        // Cleanup
        orderRepository.deleteOrder(orderId);
    }
    
    @Test
    public void testGettersAndSetters() {
        // Given
        String orderId = "ORD001";
        String prescriptionId = "RX001";
        String paymentMethod = "CASH";
        String pharmacistNotes = "Test notes";
        
        // When
        action.setOrderId(orderId);
        action.setPrescriptionId(prescriptionId);
        action.setPaymentMethod(paymentMethod);
        action.setPharmacistNotes(pharmacistNotes);
        
        // Then
        assertThat(action.getOrderId()).isEqualTo(orderId);
        assertThat(action.getPrescriptionId()).isEqualTo(prescriptionId);
        assertThat(action.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(action.getPharmacistNotes()).isEqualTo(pharmacistNotes);
    }
}

// Made with Bob