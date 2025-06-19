package com.data.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import com.data.service.ProductService;
import com.data.service.CustomerService;
import com.data.service.InvoiceService;
import com.data.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Tổng doanh thu
        java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO;
        java.util.List<Invoice> invoices = invoiceService.getAllInvoices();
        for (Invoice invoice : invoices) {
            if (invoice.getTotalAmount() != null) {
                totalRevenue = totalRevenue.add(invoice.getTotalAmount());
            }
        }
        // Tổng sản phẩm
        long totalProduct = productService.getTotalProducts();
        // Tổng khách hàng
        long totalUser = customerService.getTotalCustomers();
        // Tổng hóa đơn
        long totalInvoice = invoices.size();
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalProduct", totalProduct);
        model.addAttribute("totalUser", totalUser);
        model.addAttribute("totalInvoice", totalInvoice);
        List<Invoice> invoicesNew = invoiceService.getAllInvoices().subList(0, Math.min(4, invoices.size()));
        model.addAttribute("invoiceNews", invoicesNew);
        return "admin/dashboard";
    }
}
