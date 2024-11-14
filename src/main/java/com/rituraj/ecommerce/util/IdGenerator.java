package com.rituraj.ecommerce.util;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Lazy
public class IdGenerator {
    public String generateId(){
        String id = ObjectId.get().toHexString();
        return id;
    }

    public String generateOrderId() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = (int) (Math.random() * 9000) + 1000; // Random 4-digit number
        return "ORD-" + datePart + "-" + randomPart;
    }
}
