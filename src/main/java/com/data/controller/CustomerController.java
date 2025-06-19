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
        model.addAttribute("pageSize", PAGE_SIZE);

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

        System.out.println("Received customer data: " + customerDTO.toString());

        try {
            // Kiểm tra phone trùng lặp
            if (customerDTO.getPhone() == null || customerDTO.getPhone().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Số điện thoại không được để trống!");
                return "redirect:/admin/customer";
            }

            if (customerService.isPhoneDuplicate(customerDTO.getPhone())) {
                redirectAttributes.addFlashAttribute("error", "Số điện thoại đã tồn tại!");
                return "redirect:/admin/customer";
            }

            // Kiểm tra email trùng lặp
            if (customerDTO.getEmail() == null || customerDTO.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email không được để trống!");
                return "redirect:/admin/customer";
            }

            if (customerService.isEmailDuplicate(customerDTO.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
                return "redirect:/admin/customer";
            }

            if (result.hasErrors()) {
                System.out.println("Validation errors: " + result.getAllErrors());
                // Lấy lại danh sách khách hàng
                List<Customer> customers = customerService.getCustomers(1, PAGE_SIZE);
                model.addAttribute("customers", customers);
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages",
                        (int) Math.ceil((double) customerService.getTotalCustomers() / PAGE_SIZE));
                // Giữ lại dữ liệu đã nhập
                model.addAttribute("customerDTO", customerDTO);
                return "admin/customer";
            }

            // Tạo khách hàng mới
            Customer customer = new Customer();
            customer.setName(customerDTO.getName().trim());
            customer.setPhone(customerDTO.getPhone() != null ? customerDTO.getPhone().trim() : null);
            customer.setEmail(customerDTO.getEmail().trim());
            customer.setAddress(customerDTO.getAddress() != null ? customerDTO.getAddress().trim() : null);
            customer.setStatus(customerDTO.getStatus());

            System.out.println("Attempting to save customer: " + customer.toString());

            // Lưu vào database
            boolean success = customerService.addCustomer(customer);

            if (success) {
                System.out.println("Customer saved successfully");
                redirectAttributes.addFlashAttribute("message", "Thêm khách hàng thành công!");
            } else {
                System.out.println("Failed to save customer");
                redirectAttributes.addFlashAttribute("error", "Thêm khách hàng thất bại");
            }
        } catch (Exception e) {
            System.err.println("Error in addCustomer: " + e.getMessage());
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

    @GetMapping("/customer/check-phone")
    @ResponseBody
    public Map<String, Boolean> checkPhone(
            @RequestParam String phone,
            @RequestParam(required = false) Integer id) {
        Map<String, Boolean> reponse = new HashMap<>();
        boolean exists;

        if (id != null) {
            exists = customerService.isPhoneDuplicate(phone, id);
        } else {
            exists = customerService.isPhoneDuplicate(phone);
        }
        reponse.put("exists", exists);
        return reponse;
    }

    @PostMapping("/customer/update-status")
    @ResponseBody
    public Map<String, Object> updateStatus(
            @RequestParam Integer id,
            @RequestParam String currentStatus) {
        Map<String, Object> response = new HashMap<>();

        String newStatus = currentStatus.equals("Active") ? "Deactivate" : "Active";
        boolean success = customerService.updateCustomerStatus(id, newStatus);

        response.put("success", success);
        response.put("newStatus", newStatus);
        return response;
    }

    @GetMapping("/customer/{id}")
    @ResponseBody
    public CustomerDTO getCustomer(@PathVariable Integer id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer != null) {
            CustomerDTO dto = new CustomerDTO();
            dto.setId(customer.getId());
            dto.setName(customer.getName());
            dto.setEmail(customer.getEmail());
            dto.setPhone(customer.getPhone());
            dto.setAddress(customer.getAddress());
            dto.setStatus(customer.getStatus());
            return dto;
        }
        return null;
    }

    @PostMapping("/customer/update")
    public String updateCustomer(
            @Valid @ModelAttribute CustomerDTO customerDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ!");
            return "redirect:/admin/customer";
        }

        try {
            // Kiểm tra email trùng lặp
            if (customerService.isEmailDuplicate(customerDTO.getEmail(), customerDTO.getId())) {
                redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
                return "redirect:/admin/customer";
            }

            // Kiểm tra phone trùng lặp
            if (customerService.isPhoneDuplicate(customerDTO.getPhone(), customerDTO.getId())) {
                redirectAttributes.addFlashAttribute("error", "Số điện thoại đã tồn tại!");
                return "redirect:/admin/customer";
            }

            Customer customer = customerService.getCustomerById(customerDTO.getId());
            if (customer != null) {
                customer.setName(customerDTO.getName().trim());
                customer.setPhone(customerDTO.getPhone() != null ? customerDTO.getPhone().trim() : null);
                customer.setEmail(customerDTO.getEmail().trim());
                customer.setAddress(customerDTO.getAddress() != null ? customerDTO.getAddress().trim() : null);
                customer.setStatus(customerDTO.getStatus());

                boolean success = customerService.updateCustomer(customer);
                if (success) {
                    redirectAttributes.addFlashAttribute("message", "Cập nhật khách hàng thành công!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Cập nhật khách hàng thất bại!");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/customer";
    }
}