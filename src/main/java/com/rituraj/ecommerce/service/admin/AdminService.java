package com.rituraj.ecommerce.service.admin;

import com.rituraj.ecommerce.model.Admin;
import com.rituraj.ecommerce.model.Roles;

import java.security.NoSuchAlgorithmException;

public interface AdminService {
    public Admin login(String email, String password, Roles role) throws NoSuchAlgorithmException;
}
