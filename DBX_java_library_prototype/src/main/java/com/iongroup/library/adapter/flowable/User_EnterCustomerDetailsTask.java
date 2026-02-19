package com.iongroup.library.adapter.flowable;

import com.iongroup.library.registry.DelegationType;
import com.iongroup.library.registry.WorkFlowOperation;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final Map<String, Object> data;

    public User_EnterCustomerDetailsTask(String processInstanceId, String nodeId,
                                          String backendUrl, Map<String, Object> data) {
        this.processInstanceId = processInstanceId;
        this.nodeId = nodeId;
        this.backendUrl = backendUrl;
        this.data = data;
    }

    public void run() throws Exception {
        String body = new ObjectMapper().writeValueAsString(data);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(backendUrl + "/task/complete/" + nodeId
                        + "?processInstanceId=" + processInstanceId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("[EnterCustomerDetailsTask] Details saved, process continuing...");
        } else {
            System.out.println("[EnterCustomerDetailsTask] Failed: " + response.body());
        }
    }
}