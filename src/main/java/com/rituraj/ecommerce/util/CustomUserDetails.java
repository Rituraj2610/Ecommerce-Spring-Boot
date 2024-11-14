package com.rituraj.ecommerce.util;

import com.rituraj.ecommerce.model.Admin;
import com.rituraj.ecommerce.model.Buyer;
import com.rituraj.ecommerce.model.Seller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

//@Component
public class CustomUserDetails implements UserDetails {
    private final Object user;

    public CustomUserDetails(Object user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role;
        if (user instanceof Seller) {
            role = ((Seller) user).getRoles().name();
        } else if (user instanceof Buyer) {
            role = ((Buyer) user).getRoles().name();
        } else if (user instanceof Admin) {
            role = ((Admin) user).getRoles().name();
        } else {
            role = "ROLE_USER"; // Default role if none is set
        }
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        if (user instanceof Seller) {
            return ((Seller) user).getPassword();
        } else if (user instanceof Buyer) {
            return ((Buyer) user).getPassword();
        } else if (user instanceof Admin) {
            return ((Admin) user).getPassword();
        }
        return null; // Handle null appropriately
    }

    @Override
    public String getUsername() {
        if (user instanceof Seller) {
            return ((Seller) user).getEmail();
        } else if (user instanceof Buyer) {
            return ((Buyer) user).getEmail();
        } else if (user instanceof Admin) {
            return ((Admin) user).getEmail();
        }
        return null; // Handle null appropriately
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
