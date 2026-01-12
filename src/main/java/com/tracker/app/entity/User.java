package com.tracker.app.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity

public class User {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(unique=true)
    private String email;
    private String password;
    @Transient
    private String confirmPassword;
    private boolean verified = false;
    private String otp;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime otpExpiry ;


    public User() {

    }

    public User( String name, String email, String password,String confirmPassword, boolean verified, String otp, LocalDateTime createdAt,LocalDateTime otpExpiry) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.confirmPassword=confirmPassword;
        this.verified = verified;
        this.otp = otp;
        this.createdAt = createdAt;
        this.otpExpiry= otpExpiry;
    }

    public Integer getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

}
