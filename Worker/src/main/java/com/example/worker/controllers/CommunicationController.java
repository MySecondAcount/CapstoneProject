package com.example.worker.controllers;

import com.example.worker.model.ApiResponse;
import com.example.worker.services.affinity.AffinityManager;
import com.example.worker.services.authentication.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommunicationController {
    private AffinityManager affinityManager = AffinityManager.getInstance();
    private AuthenticationService authenticationService;
    private Logger logger = LoggerFactory.getLogger(CommunicationController.class);

    @Autowired
    public CommunicationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/addAffinityData/{id}/{workerName}")
    public ApiResponse addAffinityData(@PathVariable("id") String id,
                                       @PathVariable("workerName") String workerName,
                                       @RequestHeader(value = "X-Username") String adminUserName,
                                       @RequestHeader(value = "X-Token") String adminToken) {
        logger.info("adding the affinity data for id: " + id + " and workerName: " + workerName);
        if (!authenticationService.isAdmin(adminUserName, adminToken)) {
            logger.info("Non registered admin");
            return new ApiResponse("Non registered admin!", 500);
        }
        affinityManager.addAffinity(id, workerName);
        logger.info("added the affinity data for id: " + id + " and workerName: " + workerName);
        return new ApiResponse("Affinity data added successfully!", 200);
    }


    @GetMapping("/addAuthenticatedUser/{username}/{token}")
    public ApiResponse addAuthenticatedUser(@PathVariable("username") String username, @PathVariable("token") String token,
                                            @RequestHeader(value = "X-Username") String adminUserName,
                                            @RequestHeader(value = "X-Token") String adminToken) {
        logger.info("adding the user: " + username + " with token: " + token);
        if (!authenticationService.isAdmin(adminUserName, adminToken)) {
            logger.info("Non registered admin");
            return new ApiResponse("Non registered admin!", 500);
        }

        authenticationService.addUser(username, token);
        return new ApiResponse("User added successfully!", 200);
    }

    @GetMapping("addAdmin/{newAdminame}/{newAdminToken}")
    public ApiResponse addAdmin(@PathVariable("newAdminame") String newUsername, @PathVariable("newAdminToken") String newAdminToken
            , @RequestHeader(value = "X-Username") String adminUserName
            , @RequestHeader(value = "X-Token") String adminToken) {

        logger.info("adding the admin: " + newUsername + " with token: " + newAdminToken);
        if (!authenticationService.isAdmin(adminUserName, adminToken)) {
            return new ApiResponse("Non registered admin hasn't the ability to add new admins!", 500);
        }

        authenticationService.addNewAdmin(newUsername, newAdminToken);
        return new ApiResponse("Admin added successfully!", 200);
    }

    @DeleteMapping("/removeAuthenticatedUser/{username}/{token}")
    public ApiResponse removeAuthenticatedUser(
            @PathVariable("username") String username, @PathVariable("token") String token
            , @RequestHeader(value = "X-Username") String adminUserName
            , @RequestHeader(value = "X-Token") String adminToken) {

        logger.info("removing the user: " + username + " with token: " + token);
        if (!authenticationService.isAdmin(adminUserName, adminToken)) {
            logger.info("Non registered admin");
            return new ApiResponse("Non registered admin!", 500);
        }

        if (!authenticationService.isAuthenticatedUser(username, token)) {
            logger.info("User is not registered already!");
            return new ApiResponse("User is not registered already!", 500);
        }

        authenticationService.removeUser(username, token);
        return new ApiResponse("User removed successfully!", 200);
    }

    @GetMapping("/setAffinity")
    public ApiResponse setAffinity(
            @RequestHeader(value = "X-Username") String username,
            @RequestHeader(value = "X-Token") String token) {
        logger.info("the current worker (" + affinityManager.getCurrentWorkerName() + ")is set to be affinity!");
        if (!authenticationService.isAuthenticatedUser(username, token)) {
            logger.info("Non registered user");
            return new ApiResponse("Non registered user!", 500);
        }
        affinityManager.setCurrentWorkerAffinity();
        return new ApiResponse("Affinity set successfully!", 200);
    }

    @GetMapping("/unsetAffinity")
    public ApiResponse unsetAffinity(@RequestHeader(value = "X-Username") String username,
                                     @RequestHeader(value = "X-Token") String token) {

        logger.info("the current worker" + affinityManager.getCurrentWorkerName() + " is set to be not affinity!");
        if (!authenticationService.isAuthenticatedUser(username, token)) {
            logger.info("Non registered user");
            return new ApiResponse("Non registered user!", 500);
        }
        affinityManager.unsetCurrentWorkerAffinity();
        return new ApiResponse("Affinity unset successfully!", 200);
    }

    @GetMapping("/isAffinity")
    public boolean isAffinity(@RequestHeader(value = "X-Username") String username,
                              @RequestHeader(value = "X-Token") String token) {
        logger.info("the current worker" + affinityManager.getCurrentWorkerName() + (affinityManager.isCurrentWorkerAffinity() ? " is " : " is not ") + "affinity!");
        if (!authenticationService.isAuthenticatedUser(username, token)) {
            logger.info("Non registered user");
            return false;
        }
        return affinityManager.isCurrentWorkerAffinity();
    }

    @GetMapping("/setCurrentWorkerName/{name}")
    public ApiResponse setCurrentWorkerName(@PathVariable("name") String name,
                                            @RequestHeader(value = "X-Username") String username,
                                            @RequestHeader(value = "X-Token") String token) {
        logger.info("the current worker name is set to: " + name);
        if (!authenticationService.isAuthenticatedUser(username, token)) {
            logger.info("Non registered user");
            return new ApiResponse("Non registered user!", 500);
        }
        affinityManager.setCurrentWorkerName(name);
        return new ApiResponse("Current worker name set successfully!", 200);
    }

    @GetMapping("/getCurrentWorkerName")
    public String getCurrentWorkerName(@RequestHeader(value = "X-Username") String username,
                                       @RequestHeader(value = "X-Token") String token) {
        logger.info("the current worker name is: " + affinityManager.getCurrentWorkerName());
        if (!authenticationService.isAuthenticatedUser(username, token)) {
            logger.info("Non registered user");
            return null;
        }
        return affinityManager.getCurrentWorkerName();
    }
}
