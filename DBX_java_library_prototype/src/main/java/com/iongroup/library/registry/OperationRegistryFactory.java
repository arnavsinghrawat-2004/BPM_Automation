package com.iongroup.library.registry;

import java.util.Arrays;
import java.util.List;

/**
 * Factory class that pre-registers all workflow operations.
 * To add a new operation, simply add a call to registerXxxOperation() in
 * createRegistry().
 * 
 * This keeps the registration logic centralized and makes it trivial to extend.
 */
public class OperationRegistryFactory {

    private static volatile OperationRegistry instance;

    private OperationRegistryFactory() {
    }

    /**
     * Get or create the singleton registry with all operations registered.
     */
    public static synchronized OperationRegistry getRegistry() {
        if (instance == null) {
            instance = createRegistry();
        }
        return instance;
    }

    /**
     * Create a fresh registry and register all operations.
     * This is the main entry point for adding new operations.
     */
    public static OperationRegistry createRegistry() {
        OperationRegistry registry = new DefaultOperationRegistry();

        // ============ LOAN WORKFLOW OPERATIONS ============
        registerStartLoanApplication(registry);
        registerGetCustomerProfile(registry);
        registerCheckEligibility(registry);
        registerGetLoanPolicy(registry);
        registerCreateLoanOffer(registry);
        registerCustomerApproval(registry);
        registerNotifyCustomer(registry);
        registerEndLoanApplication(registry);
        registerEnterCustomerDetailTasks(registry);

        // ============ CARD WORKFLOW OPERATIONS ============
        registerStartCardApplication(registry);
        registerGetAvailableCreditCards(registry);
        registerCreateCardOffer(registry);
        registerIssueCreditCard(registry);

        return registry;
    }

    // ===== LOAN OPERATIONS =====

    private static void registerStartLoanApplication(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "StartLoanApplication",
                "Start a new loan application process. Generates an application ID and captures customer ID.",
                Arrays.asList("customerId", "basicRequestData"),
                Arrays.asList("applicationId", "customerId"),
                "com.iongroup.library.adapter.flowable.StartLoanApplicationTask",
                "loan"));
    }

    private static void registerEnterCustomerDetailTasks(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "EnterCustomerDetails",
                "Collect customer details dynamically",
                Arrays.asList("customerProfile"), // INPUTS
                Arrays.asList("customerProfile"), // OUTPUTS
                "com.iongroup.library.adapter.flowable.EnterCustomerDetailsTask",
                "common",
                DelegationType.SERVICE, 
                Arrays.asList(
                        "CUSTOMER_NAME",
                        "CONTACT_NUMBER",
                        "ADDRESS",
                        "PAN",
                        "AADHAR",
                        "MONTHLY_INCOME")));
    }

    private static void registerGetCustomerProfile(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "GetCustomerProfile",
                "Fetch customer profile details from CRM/database including income, credit score, account balance.",
                Arrays.asList("customerId"),
                Arrays.asList("customerProfile"),
                "com.iongroup.library.adapter.flowable.GetCustomerProfileTask",
                "common"));
    }

    private static void registerCheckEligibility(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "CheckEligibility",
                "Check credit score, income, and risk level to determine if customer is eligible for the product.",
                Arrays.asList("customerProfile"),
                Arrays.asList("eligibilityStatus"),
                "com.iongroup.library.adapter.flowable.CheckEligibilityTask",
                "common"));
    }

    private static void registerGetLoanPolicy(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "GetLoanPolicy",
                "Determine applicable loan pricing policy based on customer profile.",
                Arrays.asList("customerProfile"),
                Arrays.asList("loanPricingPolicy"),
                "com.iongroup.library.adapter.flowable.GetLoanPolicyTask",
                "loan"));
    }

    private static void registerCreateLoanOffer(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "CreateLoanOffer",
                "Generate loan terms including principal amount, interest rate, tenure, and EMI calculations.",
                Arrays.asList("customerProfile", "loanPricingPolicy"),
                Arrays.asList("loanOffer"),
                "com.iongroup.library.adapter.flowable.CreateLoanOfferTask",
                "loan"));
    }

    private static void registerCustomerApproval(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "CustomerApproval",
                "Allow customer to review and approve/sign the offer (loan or card).",
                Arrays.asList("loanOffer"),
                Arrays.asList("approvalStatus"),
                "com.iongroup.library.adapter.flowable.CustomerApprovalTask",
                "common"));
    }

    private static void registerNotifyCustomer(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "NotifyCustomer",
                "Send notification to customer regarding offer status, approval, or card issuance.",
                Arrays.asList("loanOffer", "approvalStatus"),
                Arrays.asList("notificationStatus"),
                "com.iongroup.library.adapter.flowable.NotifyCustomerTask",
                "common"));
    }

    private static void registerEndLoanApplication(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "EndLoanApplication",
                "Mark process completion and persist final application state.",
                Arrays.asList("applicationId", "approvalStatus"),
                Arrays.asList("processCompleted"),
                "com.iongroup.library.adapter.flowable.EndLoanApplicationTask",
                "loan"));
    }

    // ===== CARD OPERATIONS =====

    private static void registerStartCardApplication(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "StartCardApplication",
                "Start a new credit card application process. Generates an application ID.",
                Arrays.asList("customerId", "basicRequestData"),
                Arrays.asList("applicationId"),
                "com.iongroup.library.adapter.flowable.StartCardApplicationTask",
                "card"));
    }

    private static void registerGetAvailableCreditCards(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "GetAvailableCreditCards",
                "Fetch available credit card offers (cashback, travel, premium) based on customer eligibility.",
                Arrays.asList("customerProfile", "eligibilityStatus"),
                Arrays.asList("availableCardOffers"),
                "com.iongroup.library.adapter.flowable.GetAvailableCreditCardsTask",
                "card"));
    }

    private static void registerCreateCardOffer(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "CreateCardOffer",
                "Generate a concrete credit card offer including credit limit, annual fee, and rewards type.",
                Arrays.asList("customerProfile", "cardPricingPolicy"),
                Arrays.asList("creditCardOffer"),
                "com.iongroup.library.adapter.flowable.CreateCardOfferTask",
                "card"));
    }

    private static void registerIssueCreditCard(OperationRegistry registry) {
        registry.register(new OperationDescriptor(
                "IssueCreditCard",
                "Issue and provision the credit card to the customer after approval.",
                Arrays.asList("customerProfile", "eligibilityStatus"),
                Arrays.asList("cardIssued"),
                "com.iongroup.library.adapter.flowable.IssueCreditCardTask",
                "card"));
    }

    // ===== EXTENSION POINT =====

    /**
     * Template method to show how to add a new operation.
     * Simply call registry.register(...) with the operation descriptor.
     * 
     * Example:
     * private static void registerMyNewOperation(OperationRegistry registry) {
     * registry.register(new OperationDescriptor(
     * "MyNewOperation",
     * "Description of what this operation does.",
     * Arrays.asList("input1", "input2"),
     * Arrays.asList("output1"),
     * "com.iongroup.library.adapter.flowable.MyNewOperationTask",
     * "myCategory"
     * ));
     * }
     * 
     * Then add a call to registerMyNewOperation(registry) in createRegistry().
     */
}
