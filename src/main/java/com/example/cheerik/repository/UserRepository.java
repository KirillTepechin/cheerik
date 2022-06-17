package com.example.cheerik.repository;

import com.example.cheerik.dto.UserDto;
import com.example.cheerik.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findOneByLoginIgnoreCase(String login);
}
