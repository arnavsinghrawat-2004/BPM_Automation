package com.iongroup.library.adapter.flowable;
 
import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.registry.DelegationType;
import com.iongroup.library.registry.WorkFlowOperation;
 
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
 
@WorkFlowOperation(
    id = "EnterCustomerDetails",
    description = "Collect customer details dynamically",
    category = "common",
    type = DelegationType.USER_TASK,
    inputs = {"customerProfile"},
    outputs = {"customerProfile"},
    selectableFields = {
        "CUSTOMER_NAME",
        "CONTACT_NUMBER",
        "ADDRESS",
        "PAN",
        "AADHAR",
        "MONTHLY_INCOME"},
    customizableFields = {}
)
public class User_EnterCustomerDetailsTask {
 
    private final String processInstanceId;
    private final String nodeId;
    private final String backendUrl;
 
    public User_EnterCustomerDetailsTask(String processInstanceId, String nodeId, String backendUrl) {
        this.processInstanceId = processInstanceId;
        this.nodeId = nodeId;
        this.backendUrl = backendUrl;
    }
 
    public void run() throws Exception {
        Scanner scanner = new Scanner(System.in);
 
        System.out.println("\n=== Enter Customer Details ===");
 
        System.out.print("Customer Name: ");
        String customerName = scanner.nextLine();
 
        System.out.print("Contact Number: ");
        String contactNumber = scanner.nextLine();
 
        System.out.print("PAN Number: ");
        String panNumber = scanner.nextLine();
 
        System.out.print("Aadhar Number: ");
        String aadharNumber = scanner.nextLine();
 
        System.out.print("Address: ");
        String address = scanner.nextLine();
 
        System.out.print("Monthly Income: ");
        BigDecimal monthlyIncome = new BigDecimal(scanner.nextLine());
 
        System.out.print("Account Balance: ");
        BigDecimal accountBalance = new BigDecimal(scanner.nextLine());
 
        // Build JSON body manually
        String body = String.format("""
                {
                    "customerName": "%s",
                    "contactNumber": "%s",
                    "panNumber": "%s",
                    "aadharNumber": "%s",
                    "customerAddress": "%s",
                    "monthlyIncome": %s,
                    "accountBalance": %s
                }
                """,
                customerName, contactNumber, panNumber,
                aadharNumber, address, monthlyIncome, accountBalance);
 
        // POST to backend — signals Flowable to complete the task and saves variables
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(backendUrl + "/task/complete/" + nodeId
                        + "?processInstanceId=" + processInstanceId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
 
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
 
        if (response.statusCode() == 200) {
            System.out.println("\n[EnterCustomerDetailsTask] Details saved, process continuing...");
        } else {
            System.out.println("\n[EnterCustomerDetailsTask] Failed: " + response.body());
        }
    }
}