package com.pharmacy.action;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.pharmacy.model.Order;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;



public class DashboardAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 1L;
    
    private Map<String, Object> session;
    
    private List<Prescription> pendingPrescriptions;
    private List<Order> pendingOrders;
    private int totalPrescriptions;
    private int totalOrders;
    
    private PrescriptionRepository prescriptionRepo = PrescriptionRepository.getInstance();
    private OrderRepository orderRepo = OrderRepository.getInstance();
    
    public String execute() {
        // Get pending prescriptions
        pendingPrescriptions = prescriptionRepo.findByStatus("PENDING");
        
        // Get pending orders
        pendingOrders = orderRepo.findByStatus("PENDING");
        
        // Get totals
        totalPrescriptions = prescriptionRepo.findAll().size();
        totalOrders = orderRepo.findAll().size();
        
        return SUCCESS;
    }
    
    // Getters
    public List<Prescription> getPendingPrescriptions() {
        return pendingPrescriptions;
    }
    
    public List<Order> getPendingOrders() {
        return pendingOrders;
    }
    
    public int getTotalPrescriptions() {
        return totalPrescriptions;
    }
    
    public int getTotalOrders() {
        return totalOrders;
    }
    

    public String logout() {
        HttpServletRequest request=ServletActionContext.getRequest();
        HttpServletResponse response=ServletActionContext.getResponse();

        try {
            request.logout();
        } catch (Exception e) {
            System.err.println("[ERROR] Error logging out");
            e.printStackTrace();
        }

        // Add a message
        addActionMessage("You have been successfully logged out !!!");
        System.out.println("Logout successfull");
        return SUCCESS;
    }
    
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}

// Made with Bob
