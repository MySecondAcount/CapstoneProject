package com.example.bankingsystem;


import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.logging.Logger;

@Controller
public class BankingSystemController {
    private NetworkManager networkManager = NetworkManager.getInstance();
    private Logger logger = Logger.getLogger(BankingSystemController.class.getName());

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("newUser")
    public String newUser() {
        logger.info("Received request to register new user");
        return "new-user";
    }

    @GetMapping("userRegistration")
    public String userRegistration() {
        logger.info("Received request to register new user");
        return "user-registration";
    }

    @GetMapping("adminRegistration")
    public String adminRegistration() {
        logger.info("Received request to register new admin");
        return "admin-registration";
    }


    @PostMapping("registerNewUser")
    public String registerNewUser(@RequestParam String username, Model model) {
        logger.info("Received request to register new user " + username);

        JSONObject jsonObject = networkManager.getUserCredentials(username);
        String token = jsonObject.getString("token");
        String workerName = jsonObject.getString("workerName");

        model.addAttribute("username", username);
        model.addAttribute("token", token);
        networkManager.addWorkerNameToToken(token, workerName);

        return "show-user-credentials";
    }

}
