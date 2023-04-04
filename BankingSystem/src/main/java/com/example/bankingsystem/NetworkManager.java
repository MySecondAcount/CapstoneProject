package com.example.bankingsystem;

import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.logging.Logger;

public class NetworkManager {
    private static NetworkManager instance = null;
    private Logger logger = Logger.getLogger(NetworkManager.class.getName());
    private RestTemplate restTemplate = new RestTemplate();
    private String databaseIP = "35.184.131.198";
    private HashMap<String, String> tokenToWorkerName = new HashMap<>();

    private NetworkManager() {
        logger.info("NetworkManager created and connected to " + databaseIP);
    }

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public void setDatabaseIP(String databaseIP) {
        this.databaseIP = databaseIP;
    }

    public JSONObject getUserCredentials(String username) {
        String url = "http://" + databaseIP + ":8081/register/" + username;
        return restTemplate.getForObject(url, JSONObject.class);
    }

    public void addWorkerNameToToken(String token, String workerName) {
        tokenToWorkerName.put(token, workerName);
    }

    public void getWorkerName(String token) {
        tokenToWorkerName.get(token);
    }
}
