package com.sda.BusApp.repository;

import com.sda.BusApp.model.entity.User;
import com.sda.BusApp.model.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {

    Long countByLogin(String login);
    void deleteByUser(User user);
    UserCredentials findByLogin(String login);
    UserCredentials getByUser(User user);
    UserCredentials getByEmail(String email);
}
