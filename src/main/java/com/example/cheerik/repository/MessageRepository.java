package com.example.cheerik.repository;

import com.example.cheerik.model.Message;
import com.example.cheerik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
    @Query(value =
            "select msg " +
                    "from Message msg "+
                    "where (msg.from = :usr and msg.to = :chatUsr) or " +
                    "      (msg.from = :chatUsr and msg.to = :usr) " +
                    "order by id"
    )
    List<Message> findChatMessages(@Param("usr") User user, @Param("chatUsr") User chatUser);
}
