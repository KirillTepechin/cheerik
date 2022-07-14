package com.example.cheerik;

import com.example.cheerik.dto.MessageDto;
import com.example.cheerik.model.Message;
import com.example.cheerik.model.User;
import com.example.cheerik.repository.UserRepository;
import com.example.cheerik.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CheerikApplicationTests {
	@Autowired
	UserRepository userRepository;
	@Test
	void filenameTest() {
		MessageDto messageDto = new MessageDto();
		User user = new User();

		user.setFilename("filename.jpg");
		userRepository.save(user);
		messageDto.setFrom(user.getUsername());

		Assertions.assertEquals("filename.jpg", messageDto.getUserFilename());
	}

}
