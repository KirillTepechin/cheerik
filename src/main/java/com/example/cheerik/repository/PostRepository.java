package com.example.cheerik.repository;

import com.example.cheerik.dto.PostDto;
import com.example.cheerik.model.Like;
import com.example.cheerik.model.Post;
import com.example.cheerik.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query(value =
            " select new com.example.cheerik.dto.PostDto(p, 1) " +
            "from Post p " +
            "where id IN :sp "
    )
    Page<PostDto> findByUserSubscriptions(Pageable pageable,  @Param("sp") List<Long> userSubscriptionsPosts);

    @Query(value =
            " select new com.example.cheerik.dto.PostDto(p, 1) " +
                    "from Post p " +
                    "where p.user.id = :userId"
    )
    Page<PostDto> findByUser(Pageable pageable, @Param("userId") Long userId);
}
