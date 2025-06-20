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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

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

    @GetMapping("/dashboard/sales-data")
    @ResponseBody
    public Map<String, Object> getMonthlySalesData(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "filter", required = false, defaultValue = "12months") String filter) {
        Map<String, Object> result = new HashMap<>();
        if (filter.equals("12months")) {
            if (year == null)
                year = Year.now().getValue();
            List<Object[]> rawData = invoiceService.getMonthlyRevenueOfYear(year);
            double[] sales = new double[12];
            for (Object[] row : rawData) {
                int month = ((Integer) row[0]) - 1;
                double revenue = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                sales[month] = revenue;
            }
            result.put("labels", new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
                    "Nov", "Dec" });
            result.put("data", sales);
        } else if (filter.equals("6months")) {
            int nowYear = LocalDate.now().getYear();
            int nowMonth = LocalDate.now().getMonthValue();
            List<Object[]> rawData = invoiceService.getMonthlyRevenueOfYear(nowYear);
            double[] sales = new double[6];
            String[] labels = new String[6];
            for (int i = 0; i < 6; i++) {
                int month = (nowMonth - 5 + i);
                int yearToQuery = nowYear;
                if (month <= 0) {
                    month += 12;
                    yearToQuery--;
                }
                labels[i] = java.time.Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                sales[i] = 0;
                for (Object[] row : rawData) {
                    int m = ((Integer) row[0]);
                    if (m == month) {
                        sales[i] = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                        break;
                    }
                }
            }
            result.put("labels", labels);
            result.put("data", sales);
        } else if (filter.equals("30days")) {
            List<Object[]> rawData = invoiceService.getRevenueByDay(LocalDate.now().minusDays(29), LocalDate.now());
            double[] sales = new double[30];
            String[] labels = new String[30];
            for (int i = 0; i < 30; i++) {
                LocalDate day = LocalDate.now().minusDays(29 - i);
                labels[i] = day.getMonthValue() + "/" + day.getDayOfMonth();
                sales[i] = 0;
                for (Object[] row : rawData) {
                    LocalDate d = ((java.sql.Date) row[0]).toLocalDate();
                    if (d.equals(day)) {
                        sales[i] = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                        break;
                    }
                }
            }
            result.put("labels", labels);
            result.put("data", sales);
        } else if (filter.equals("7days")) {
            List<Object[]> rawData = invoiceService.getRevenueByDay(LocalDate.now().minusDays(6), LocalDate.now());
            double[] sales = new double[7];
            String[] labels = new String[7];
            for (int i = 0; i < 7; i++) {
                LocalDate day = LocalDate.now().minusDays(6 - i);
                labels[i] = day.getMonthValue() + "/" + day.getDayOfMonth();
                sales[i] = 0;
                for (Object[] row : rawData) {
                    LocalDate d = ((java.sql.Date) row[0]).toLocalDate();
                    if (d.equals(day)) {
                        sales[i] = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                        break;
                    }
                }
            }
            result.put("labels", labels);
            result.put("data", sales);
        }
        return result;
    }
}
