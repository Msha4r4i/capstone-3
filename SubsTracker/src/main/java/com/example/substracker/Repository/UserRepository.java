package com.example.substracker.Repository;

import com.example.substracker.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserById(Integer id);
    List<User> findUsersByIsSubscribed(Boolean isSubscribed);


    Boolean findUserByEmail(String email);

    Boolean existsByEmail(String email);


}
