package com.rituraj.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TempUserRegistration {
    private String email;
    private String encodedPassword;
    private String fullName;
    private LocalDateTime otpGeneratedTime;
}
