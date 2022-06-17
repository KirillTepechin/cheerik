package com.example.cheerik.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserSignupDto {
    @NotBlank
    @Size(min = 3, max = 64)
    private String login;
    @NotBlank
    @Size(min = 6, max = 64)
    private String password;
    @NotBlank
    @Size(min = 6, max = 64)
    private String passwordConfirm;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

}
