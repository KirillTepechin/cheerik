package com.example.cheerik.repository;

import com.example.cheerik.dto.PostDto;
import com.example.cheerik.dto.ReportStatsDto;
import com.example.cheerik.dto.UserDto;
import com.example.cheerik.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findOneByLoginIgnoreCase(String login);

    @Query(value =
            " select new com.example.cheerik.dto.ReportStatsDto(p.login,p.likes.size,p.posts.size,p.subscribers.size,p.subscriptions.size) " +
                    "from User p "
    )
    Page<ReportStatsDto> findReport(Pageable pageable);
}
