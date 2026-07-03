package com.mohit.faceverification.repository;

import com.mohit.faceverification.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{

}
