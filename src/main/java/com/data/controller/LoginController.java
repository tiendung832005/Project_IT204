package com.data.controller;

import com.data.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        // Nếu đã đăng nhập thì chuyển về dashboard
        if (session.getAttribute("admin") != null) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        // Validate empty fields
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Username không được để trống");
            return "admin/login";
        }
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Password không được để trống");
            return "admin/login";
        }

        // Authenticate
        if (adminService.authenticate(username, password)) {
            session.setAttribute("admin", username);
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "Username hoặc password không đúng");
            return "admin/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
