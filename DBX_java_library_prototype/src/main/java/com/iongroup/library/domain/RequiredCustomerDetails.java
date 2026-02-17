package com.iongroup.library.domain;

import java.io.Serializable;

/**
 * Tracks which customer details are required in this workflow.
 * Populated dynamically from BPMN task configuration.
 */
public class RequiredCustomerDetails implements Serializable{
    private static final long serialVersionUID = 9L;
    private boolean customerName;
    private boolean contactNumber;
    private boolean customerAddress;
    private boolean panNumber;
    private boolean aadharNumber;
    private boolean monthlyIncome;

    public boolean isCustomerName() {
        return customerName;
    }

    public void setCustomerName(boolean customerName) {
        this.customerName = customerName;
    }

    public boolean isContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(boolean contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(boolean customerAddress) {
        this.customerAddress = customerAddress;
    }

    public boolean isPanNumber() {
        return panNumber;
    }

    public void setPanNumber(boolean panNumber) {
        this.panNumber = panNumber;
    }

    public boolean isAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(boolean aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public boolean isMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(boolean monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }
}
