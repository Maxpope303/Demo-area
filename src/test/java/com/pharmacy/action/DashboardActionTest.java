package com.pharmacy.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DashboardActionTest {
    
    private DashboardAction action;
    
    @BeforeEach
    public void setUp() {
        action = new DashboardAction();
        Map<String, Object> session = new HashMap<String, Object>();
        action.setSession(session);
    }
    
    @Test
    public void testExecute() {
        // When
        String result = action.execute();
        
        // Then
        assertThat(result).isEqualTo("success");
        assertThat(action.getPendingPrescriptions()).isNotNull();
        assertThat(action.getPendingOrders()).isNotNull();
        assertThat(action.getTotalPrescriptions()).isGreaterThanOrEqualTo(0);
        assertThat(action.getTotalOrders()).isGreaterThanOrEqualTo(0);
    }
    
    @Test
    public void testGetPendingPrescriptions() {
        // Given
        action.execute();
        
        // When
        int count = action.getPendingPrescriptions().size();
        
        // Then
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
    
    @Test
    public void testGetPendingOrders() {
        // Given
        action.execute();
        
        // When
        int count = action.getPendingOrders().size();
        
        // Then
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
    
    @Test
    public void testGetTotalPrescriptions() {
        // Given
        action.execute();
        
        // When
        int total = action.getTotalPrescriptions();
        
        // Then
        assertThat(total).isGreaterThanOrEqualTo(0);
    }
    
    @Test
    public void testGetTotalOrders() {
        // Given
        action.execute();
        
        // When
        int total = action.getTotalOrders();
        
        // Then
        assertThat(total).isGreaterThanOrEqualTo(0);
    }
    
    @Test
    public void testSetSession() {
        // Given
        Map<String, Object> session = new HashMap<String, Object>();
        session.put("testKey", "testValue");
        
        // When
        action.setSession(session);
        
        // Then - No exception should be thrown
        assertThat(action).isNotNull();
    }
}

// Made with Bob