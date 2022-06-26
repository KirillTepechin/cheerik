package com.example.cheerik.service;

import com.example.cheerik.dto.ReportStatsDto;
import com.example.cheerik.dto.UserDto;
import com.example.cheerik.model.User;
import com.example.cheerik.repository.UserRepository;
import com.example.cheerik.util.validation.ValidationException;
import com.example.cheerik.util.validation.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidatorUtil validatorUtil;
    @Value("${upload.path}")
    private String uploadPath;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ValidatorUtil validatorUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validatorUtil = validatorUtil;
    }
    public Page<ReportStatsDto> findReport(Pageable pageable) {
        return userRepository.findReport(pageable);
    }

    public User findByLogin(String login) {
        return userRepository.findOneByLoginIgnoreCase(login);
    }

    public User createUser(String login, String password, String passwordConfirm) {
        if (findByLogin(login) != null) {
            throw new ValidationException(String.format("Пользователь '%s' уже существует", login));
        }
        final User user = new User(login, passwordEncoder.encode(password));
        validatorUtil.validate(user);
        if (!Objects.equals(password, passwordConfirm)) {
            throw new ValidationException("Пароли не совпадают");
        }
        return userRepository.save(user);
    }

    public User updateUser(User user, String password, MultipartFile file) throws IOException {
        if (!StringUtils.isEmpty(password)) {
            user.setPassword(password);
        }
        if(file!=null  && !file.getOriginalFilename().isEmpty()){

            Path destinationFile = Path.of(uploadPath).resolve(Paths.get(file.getOriginalFilename())).normalize().toAbsolutePath();

            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            File newFile = new File(String.valueOf(destinationFile));
            newFile.renameTo(new File(uploadPath + "/" + resultFilename));

            user.setFilename(resultFilename);
        }
        return userRepository.save(user);

    }

    public Page<User> findAllPages(int page, int size) {
        return userRepository.findAll(PageRequest.of(page - 1, size, Sort.by("id").ascending()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User userEntity = findByLogin(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(
                userEntity.getUsername(), userEntity.getPassword(), Collections.singleton(new SimpleGrantedAuthority("none")));
    }

    public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);
        currentUser.getSubscriptions().add(user);
        userRepository.save(user);
        userRepository.save(currentUser);
    }

    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);
        currentUser.getSubscriptions().remove(user);
        userRepository.save(user);
        userRepository.save(currentUser);
    }

    public Page<UserDto> findSubscriptions(int page, int size, Long id) {
        Pageable pageable = PageRequest.of(page-1,size);
        return userRepository.findSubscriptions(pageable, id);

    }

    public Page<UserDto> findSubscribers(int page, int size, Long id) {
        Pageable pageable = PageRequest.of(page-1,size);
        return userRepository.findSubscribers(pageable, id);
    }
}
