package com.data.controller;

import com.data.dto.InvoiceDTO;
import com.data.dto.InvoiceViewDTO;
import com.data.entity.Customer;
import com.data.entity.Invoice;
import com.data.entity.InvoiceDetail;
import com.data.entity.Product;
import com.data.service.CustomerService;
import com.data.service.InvoiceService;
import com.data.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @GetMapping("/invoice")
    public String listInvoice(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "dateFrom", required = false) String dateFrom,
            @RequestParam(value = "dateTo", required = false) String dateTo,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        // Set page size to 8 as requested
        int pageSize = 8;

        InvoiceDTO.PageResult<Invoice> pageResult = invoiceService.searchInvoicesWithPagination(search, dateFrom,
                dateTo, page, pageSize);

        model.addAttribute("invoices", pageResult.getContent());
        model.addAttribute("currentPage", pageResult.getCurrentPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalElements", pageResult.getTotalElements());
        model.addAttribute("hasNext", pageResult.isHasNext());
        model.addAttribute("hasPrevious", pageResult.isHasPrevious());
        model.addAttribute("pageSize", pageSize);

        return "admin/invoice";
    }

    @GetMapping("/invoiceDetail")
    public String showInvoiceDetailForm(Model model) {
        // Lấy danh sách khách hàng và sản phẩm cho form, chỉ lấy ACTIVE
        List<Customer> customers = customerService.getAllCustomers().stream()
                .filter(c -> "ACTIVE".equalsIgnoreCase(c.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        List<Product> products = productService.getAllProducts().stream()
                .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus()))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("customers", customers);
        model.addAttribute("products", products);
        model.addAttribute("invoiceDTO", new InvoiceDTO());

        return "admin/invoiceDetail";
    }

    @PostMapping("/invoice/create")
    @ResponseBody
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        try {
            System.out.println("=== Controller: Received invoice creation request ===");
            System.out.println("Customer ID: " + invoiceDTO.getCustomerId());
            System.out.println("Details: " + (invoiceDTO.getDetails() != null ? invoiceDTO.getDetails().size() : 0));

            // Validate dữ liệu
            if (invoiceDTO.getCustomerId() == null) {
                System.out.println("Validation failed: Customer ID is null");
                return ResponseEntity.badRequest().body("Vui lòng chọn khách hàng!");
            }

            if (invoiceDTO.getDetails() == null || invoiceDTO.getDetails().isEmpty()) {
                System.out.println("Validation failed: No details provided");
                return ResponseEntity.badRequest().body("Vui lòng thêm ít nhất một sản phẩm!");
            }

            // Tạo hóa đơn với thông báo chi tiết
            InvoiceDTO.InvoiceResult result = invoiceService.createInvoiceWithResult(invoiceDTO);

            if (result.isSuccess()) {
                System.out.println("Invoice created successfully");
                return ResponseEntity.ok().body("successfully!");
            } else {
                System.out.println("Failed to create invoice: " + result.getMessage());
                return ResponseEntity.badRequest().body(result.getMessage());
            }
        } catch (Exception e) {
            System.out.println("=== Controller: Exception occurred ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @GetMapping("/invoice/{id}/details")
    @ResponseBody
    public ResponseEntity<?> getInvoiceDetails(@PathVariable Integer id) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(id);
            if (invoice == null) {
                return ResponseEntity.notFound().build();
            }

            List<InvoiceDetail> details = invoiceService.getInvoiceDetails(id);

            // Chuyển đổi sang DTO
            InvoiceViewDTO invoiceViewDTO = new InvoiceViewDTO();
            invoiceViewDTO.setId(invoice.getId());
            invoiceViewDTO.setCustomerName(invoice.getCustomer().getName());
            invoiceViewDTO.setCreatedAt(invoice.getCreatedAt());
            invoiceViewDTO.setStatus(invoice.getStatus().toString());
            invoiceViewDTO.setTotalAmount(invoice.getTotalAmount());

            List<InvoiceViewDTO.InvoiceDetailViewDTO> detailDTOs = details.stream()
                    .map(detail -> {
                        InvoiceViewDTO.InvoiceDetailViewDTO detailDTO = new InvoiceViewDTO.InvoiceDetailViewDTO();
                        detailDTO.setId(detail.getId());
                        detailDTO.setProductName(detail.getProduct().getName());
                        detailDTO.setBrand(detail.getProduct().getBrand());
                        detailDTO.setQuantity(detail.getQuantity());
                        detailDTO.setUnitPrice(detail.getUnitPrice());
                        detailDTO.setSubtotal(detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity())));
                        return detailDTO;
                    })
                    .collect(Collectors.toList());

            invoiceViewDTO.setDetails(detailDTOs);

            return ResponseEntity.ok(invoiceViewDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @PostMapping("/invoice/update-status")
    @ResponseBody
    public ResponseEntity<?> updateInvoiceStatus(
            @RequestParam Integer invoiceId,
            @RequestParam String status) {
        try {
            Invoice.InvoiceStatus newStatus = Invoice.InvoiceStatus.valueOf(status.toUpperCase());
            boolean success = invoiceService.updateInvoiceStatus(invoiceId, newStatus);

            if (success) {
                return ResponseEntity.ok().body("Cập nhật trạng thái thành công!");
            } else {
                return ResponseEntity.badRequest().body("Cập nhật trạng thái thất bại!");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Có lỗi xảy ra: " + e.getMessage());
        }
    }
}
