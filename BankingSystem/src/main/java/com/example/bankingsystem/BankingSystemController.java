package com.example.bankingsystem;


import jakarta.servlet.http.HttpSession;
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

        JSONObject jsonObject = new JSONObject(networkManager.registerANewUser(username));
        logger.info("Received user credentials from the database: " + jsonObject.toString());
        String token = jsonObject.getString("token");
        int workerPort = jsonObject.getInt("workerPort");

        model.addAttribute("username", username);
        model.addAttribute("token", token);
        model.addAttribute("workerName", networkManager.getWorkerName(workerPort));

        networkManager.addWorkerPortToToken(token, workerPort);
        return "show-user-credentials";
    }

    @PostMapping("check-user-credentials")
    public String checkUserCredentials(@RequestParam String username, @RequestParam String token,
                                       HttpSession httpSession, Model model) {

        logger.info("Received request to check user credentials");
        model.addAttribute("username", username);
        model.addAttribute("token", token);

        if (networkManager.isAuthorizedUser(username, token)) {
            logger.info("User credentials are correct for " + username);
            int workerPort = networkManager.getWorkerPort(token);

            model.addAttribute("username", username);
            model.addAttribute("token", token);
            model.addAttribute("workerPort", workerPort);

            httpSession.setAttribute("username", username);
            httpSession.setAttribute("token", token);
            httpSession.setAttribute("workerPort", workerPort);

            return "bank-system";
        } else {
            return "registration-failed";
        }
    }

    @PostMapping("check-admin-credentials")
    public String checkAdminCredentials(@RequestParam String username, @RequestParam String token,
                                        HttpSession httpSession, Model model) {

        logger.info("Received request to check admin credentials");
        model.addAttribute("username", username);
        model.addAttribute("token", token);

        if (networkManager.isAuthorizedAdmin(username, token)) {
            logger.info("Admin credentials are correct for " + username);

            model.addAttribute("username", username);
            model.addAttribute("token", token);

            httpSession.setAttribute("username", username);
            httpSession.setAttribute("token", token);

            return "admin-system";
        } else {
            return "registration-failed";
        }
    }


    @GetMapping("CreateNewAccount")
    public String createNewAccount(HttpSession httpSession, Model model) {
        return "create-account-form";
    }

    @PostMapping("storeNewAccountData")
    public String storeNewAccountData(@RequestParam String customerName, @RequestParam String customerPhone,
                                      @RequestParam String customerAddress, @RequestParam float accountBalance, Model model, HttpSession httpSession) {
        logger.info("Received request to store new account data");
        logger.info("Customer name: " + customerName);
        logger.info("Phone: " + customerPhone);
        logger.info("Address: " + customerAddress);
        logger.info("Balance: " + accountBalance);

        JSONObject customer = new JSONObject();
        customer.put("name", customerName);
        customer.put("phone", customerPhone);
        customer.put("address", customerAddress);
        customer.put("accountBalance", accountBalance);

        networkManager.addNewCustomer(customer, httpSession);
        return "bank-system";
    }

}
