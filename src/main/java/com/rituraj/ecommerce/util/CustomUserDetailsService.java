package com.rituraj.ecommerce.util;

import com.rituraj.ecommerce.model.Admin;
import com.rituraj.ecommerce.model.Buyer;
import com.rituraj.ecommerce.model.Seller;
import com.rituraj.ecommerce.repository.AdminRepo;
import com.rituraj.ecommerce.repository.BuyerRepo;
import com.rituraj.ecommerce.repository.SellerRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private SellerRepo sellerRepository;

    private BuyerRepo buyerRepository;

    private AdminRepo adminRepository;

    public CustomUserDetailsService(SellerRepo sellerRepository, AdminRepo adminRepository, BuyerRepo buyerRepository) {
        this.sellerRepository = sellerRepository;
        this.adminRepository = adminRepository;
        this.buyerRepository = buyerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        System.out.println("custom user detail");
        System.out.println(email);
        Seller seller = sellerRepository.findByEmail(email);
        System.out.println(sellerRepository.findByEmail(email));
        System.out.println(seller);
        if (seller != null) {
            System.out.println("Enetered seller in customuserdetailservice" + seller);
            return new CustomUserDetails(seller);
        }

        Buyer buyer = buyerRepository.findByEmail(email);
        if (buyer !=null) {
            System.out.println("Enetered buyer in customuserdetailservice" + buyer);
            return new CustomUserDetails(buyer);
        }

        Admin admin = adminRepository.findByEmail(email);
        if (admin.getEmail().equals(email)) {
            System.out.println("Enetered admin in customuserdetailservice\n" + admin.getId() + admin.getEmail() + admin.getName() + admin.getRoles());
            return new CustomUserDetails(admin);
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

}
