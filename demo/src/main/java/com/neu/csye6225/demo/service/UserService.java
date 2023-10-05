package com.neu.csye6225.demo.service;

import com.neu.csye6225.demo.entities.Users;
import com.neu.csye6225.demo.repositories.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserRespository userRespository;
    @Autowired
    public UserService (UserRespository userRespository) {
        this.userRespository = userRespository;
    }

    public void loadUsersFromCSV (String csvFilePath) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                String firstName = data[0];
                String lastName = data[1];
                String emailID = data[2];
                String password = data[3];

                Optional<Users> userExists = userRespository.findUsersByEmailID(emailID);

                if (userExists.isEmpty()) {

                    Users addNewUser = new Users();

                    addNewUser.setFirstName(firstName);
                    addNewUser.setLastName(lastName);
                    addNewUser.setEmailID(emailID);
                    addNewUser.setAccountCreated(LocalDateTime.now());
                    addNewUser.setAccountUpdated(LocalDateTime.now());

                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    addNewUser.setPassword(passwordEncoder.encode(password));

                    userRespository.save(addNewUser);
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } /*catch (CsvValidationException csvValidationException) {
            throw new RuntimeException(csvValidationException);
        }*/
    }
}
