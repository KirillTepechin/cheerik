package com.example.cheerik.repository;

import com.example.cheerik.dto.UserDto;
import com.example.cheerik.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {
    User findOneByLoginIgnoreCase(String login);

    @Query(value =
            " select new com.example.cheerik.dto.UserDto(u, 1) " +
                    "from User u join u.subscribers us "+
                    "where us.id = :userId "
    )
    Page<UserDto> findSubscriptions(Pageable pageable, @Param("userId") Long id);
    @Query(value =
            " select new com.example.cheerik.dto.UserDto(u, 1) " +
                    "from User u join u.subscriptions us "+
                    "where us.id = :userId "
    )
    Page<UserDto> findSubscribers(Pageable pageable, @Param("userId") Long id);
}


