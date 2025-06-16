package com.data.controller;

import com.data.dto.CustomerDTO;
import com.data.entity.Customer;
import com.data.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    private static final int PAGE_SIZE = 8;

    @GetMapping("/customer")
    public String listCustomers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String search,
            Model model) {

        List<Customer> customers = customerService.getCustomers(page, PAGE_SIZE);
        long totalCustomers = customerService.getTotalCustomers();
        int totalPages = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);

        model.addAttribute("customers", customers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);

        if (!model.containsAttribute("customerDTO")) {
            model.addAttribute("customerDTO", new CustomerDTO());
        }

        return "admin/customer";
    }

    @PostMapping("/customer/add")
    public String addCustomer(
            @Valid @ModelAttribute CustomerDTO customerDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Lấy lại danh sách khách hàng
            List<Customer> customers = customerService.getCustomers(1, PAGE_SIZE);
            model.addAttribute("customers", customers);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", (int) Math.ceil((double) customerService.getTotalCustomers() / PAGE_SIZE));
            // Giữ lại dữ liệu đã nhập
            model.addAttribute("customerDTO", customerDTO);
            return "admin/customer";
        }

        try {
            // Kiểm tra email trùng lặp
            if (customerDTO.getEmail() != null && !customerDTO.getEmail().trim().isEmpty()) {
                if (customerService.isEmailDuplicate(customerDTO.getEmail())) {
                    redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
                    return "redirect:/admin/customer";
                }
            }

            // Tạo khách hàng mới
            Customer customer = new Customer();
            customer.setName(customerDTO.getName());
            customer.setPhone(customerDTO.getPhone());
            customer.setEmail(customerDTO.getEmail());
            customer.setAddress(customerDTO.getAddress());
            customer.setStatus(customerDTO.getStatus());

            // Lưu vào database
            boolean success = customerService.addCustomer(customer);

            if (success) {
                redirectAttributes.addFlashAttribute("message", "Thêm khách hàng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Thêm khách hàng thất bại");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/customer";
    }

    @GetMapping("/customer/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(
            @RequestParam String email,
            @RequestParam(required = false) Integer id) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists;

        if (id != null) {
            exists = customerService.isEmailDuplicate(email, id);
        } else {
            exists = customerService.isEmailDuplicate(email);
        }

        response.put("exists", exists);
        return response;
    }
}