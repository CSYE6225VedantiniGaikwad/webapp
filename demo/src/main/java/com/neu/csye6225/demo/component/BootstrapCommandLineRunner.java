package com.neu.csye6225.demo.component;

import com.neu.csye6225.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BootstrapCommandLineRunner implements CommandLineRunner {
    private final UserService userService;
    @Autowired
    public BootstrapCommandLineRunner (UserService userService) {
        this.userService = userService;
    }

    @Value("${env.CSV_PATH:./opt/users.csv}")
    private String csv_path;
    @Override
    public void run(String... args) throws IOException {
        userService.loadUsersFromCSV(csv_path);
    }
}
