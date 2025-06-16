package com.data.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class InvoiceController {
    @GetMapping("/invoice")
    public String listInvoice() {
        return "admin/invoice";
    }
}
