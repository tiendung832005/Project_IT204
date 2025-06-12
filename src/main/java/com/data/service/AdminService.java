package com.data.service;

import com.data.entity.Admin;
import com.data.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    public boolean authenticate(String username, String password) {
        try {
            System.out.println("Authenticating user: " + username);
            Admin admin = adminRepository.findByUsername(username);
            if (admin != null) {
                System.out.println("Found admin, checking password");
                boolean matches = admin.getPassword().equals(password);
                System.out.println("Password matches: " + matches);
                return matches;
            }
            System.out.println("Admin not found");
            return false;
        } catch (Exception e) {
            System.out.println("Error in authentication: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}