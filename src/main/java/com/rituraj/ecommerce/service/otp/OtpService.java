package com.rituraj.ecommerce.service.otp;

import com.rituraj.ecommerce.model.TempUserRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, TempUserRegistration> tempUserStorage = new ConcurrentHashMap<>();
    private final long OTP_EXPIRATION_MINUTES = 5;

    @Autowired
    private JavaMailSender mailSender;

    public String generateOtp(String email, TempUserRegistration tempUser) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);
        tempUserStorage.put(email, tempUser); // Store user details temporarily
        return otp;
    }

    public void sendOtpByEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\nThis code is valid for " + OTP_EXPIRATION_MINUTES + " minutes.");
        mailSender.send(message);
    }

    public boolean verifyOtp(String email, String otp) {
        if (!otpStorage.containsKey(email) || !otp.equals(otpStorage.get(email)) || isOtpExpired(email)) {
            return false;
        }
        otpStorage.remove(email);
        return true;
    }

    private boolean isOtpExpired(String email) {
        TempUserRegistration tempUser = tempUserStorage.get(email);
        return tempUser.getOtpGeneratedTime().plusMinutes(OTP_EXPIRATION_MINUTES).isBefore(LocalDateTime.now());
    }

    public TempUserRegistration getTempUserDetails(String email) {
        return tempUserStorage.remove(email); // Remove after successful OTP verification
    }
}