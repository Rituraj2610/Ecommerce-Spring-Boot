package com.rituraj.ecommerce.util;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Lazy
public class EmailValidator {

    private  final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    private  final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public  boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}