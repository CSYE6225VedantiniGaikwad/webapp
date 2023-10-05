package com.neu.csye6225.demo.repositories;

import com.neu.csye6225.demo.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRespository extends JpaRepository<Users, Long> {
    Optional<Users> findUsersByEmailID(String emailID);
}
