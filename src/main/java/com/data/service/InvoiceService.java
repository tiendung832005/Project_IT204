package com.data.service;

import com.data.dto.InvoiceDTO;
import com.data.dto.InvoiceDetailDTO;
import com.data.entity.Customer;
import com.data.entity.Invoice;
import com.data.entity.InvoiceDetail;
import com.data.entity.Product;
import com.data.repository.CustomerRepository;
import com.data.repository.InvoiceDetailRepository;
import com.data.repository.InvoiceRepository;
import com.data.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public boolean createInvoice(InvoiceDTO invoiceDTO) {
        try {
            System.out.println("=== Starting invoice creation ===");
            System.out.println("Customer ID: " + invoiceDTO.getCustomerId());
            System.out.println(
                    "Details count: " + (invoiceDTO.getDetails() != null ? invoiceDTO.getDetails().size() : 0));

            // 1. Lấy thông tin khách hàng
            Customer customer = customerRepository.findById(invoiceDTO.getCustomerId());
            if (customer == null) {
                System.out.println("Customer not found with ID: " + invoiceDTO.getCustomerId());
                return false;
            }
            System.out.println("Found customer: " + customer.getName());

            // 2. Tạo hóa đơn mới
            Invoice invoice = new Invoice();
            invoice.setCustomer(customer);
            invoice.setCreatedAt(new Date());
            invoice.setStatus(Invoice.InvoiceStatus.PENDING);
            System.out.println("Created invoice object: " + invoice);

            // 3. Tính tổng tiền và tạo chi tiết hóa đơn
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<InvoiceDetail> details = new ArrayList<>();

            for (InvoiceDetailDTO detailDTO : invoiceDTO.getDetails()) {
                System.out.println("Processing detail - Product ID: " + detailDTO.getProductId() + ", Quantity: "
                        + detailDTO.getQuantity());

                // Lấy thông tin sản phẩm
                Product product = productRepository.findById(detailDTO.getProductId());
                if (product == null) {
                    System.out.println("Product not found with ID: " + detailDTO.getProductId());
                    return false;
                }
                System.out.println("Found product: " + product.getName() + ", Stock: " + product.getStock());

                if (product.getStock() < detailDTO.getQuantity()) {
                    System.out.println("Insufficient stock for product: " + product.getName() + ". Available: "
                            + product.getStock() + ", Requested: " + detailDTO.getQuantity());
                    return false;
                }

                // Tạo chi tiết hóa đơn
                InvoiceDetail detail = new InvoiceDetail();
                detail.setInvoice(invoice);
                detail.setProduct(product);
                detail.setQuantity(detailDTO.getQuantity());
                detail.setUnitPrice(product.getPrice());
                System.out.println("Created detail: " + detail);

                // Cập nhật tổng tiền
                BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(detailDTO.getQuantity()));
                totalAmount = totalAmount.add(subtotal);

                // Cập nhật số lượng tồn kho
                int oldStock = product.getStock();
                product.setStock(product.getStock() - detailDTO.getQuantity());
                boolean updateSuccess = productRepository.update(product);
                if (!updateSuccess) {
                    System.out.println("Failed to update product stock for: " + product.getName());
                    return false;
                }
                System.out.println("Updated product stock: " + oldStock + " -> " + product.getStock());

                details.add(detail);
                System.out.println("Added detail for product: " + product.getName() + ", Subtotal: " + subtotal);
            }

            // 4. Lưu hóa đơn trước
            invoice.setTotalAmount(totalAmount);
            System.out.println("Total amount: " + totalAmount);
            System.out.println("Invoice details count: " + details.size());
            System.out.println("Final invoice object: " + invoice);

            System.out.println("Attempting to save invoice...");
            boolean invoiceSaved = invoiceRepository.save(invoice);
            if (!invoiceSaved) {
                System.out.println("Failed to save invoice");
                return false;
            }
            System.out.println("Invoice saved successfully with ID: " + invoice.getId());

            // 5. Lưu chi tiết hóa đơn sau khi có ID của invoice
            System.out.println("Attempting to save invoice details...");
            boolean detailsSaved = invoiceDetailRepository.saveAll(details);
            if (!detailsSaved) {
                System.out.println("Failed to save invoice details");
                return false;
            }
            System.out.println("Invoice details saved successfully");

            System.out.println("=== Invoice creation completed successfully ===");
            return true;

        } catch (Exception e) {
            System.out.println("=== Error in invoice creation ===");
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public InvoiceDTO.InvoiceResult createInvoiceWithResult(InvoiceDTO invoiceDTO) {
        try {
            System.out.println("=== Starting invoice creation with detailed result ===");
            System.out.println("Customer ID: " + invoiceDTO.getCustomerId());
            System.out.println(
                    "Details count: " + (invoiceDTO.getDetails() != null ? invoiceDTO.getDetails().size() : 0));

            // 1. Lấy thông tin khách hàng
            Customer customer = customerRepository.findById(invoiceDTO.getCustomerId());
            if (customer == null) {
                System.out.println("Customer not found with ID: " + invoiceDTO.getCustomerId());
                return new InvoiceDTO.InvoiceResult(false, "Không tìm thấy khách hàng!");
            }
            System.out.println("Found customer: " + customer.getName());

            // 2. Tạo hóa đơn mới
            Invoice invoice = new Invoice();
            invoice.setCustomer(customer);
            invoice.setCreatedAt(new Date());
            invoice.setStatus(Invoice.InvoiceStatus.PENDING);
            System.out.println("Created invoice object: " + invoice);

            // 3. Tính tổng tiền và tạo chi tiết hóa đơn
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<InvoiceDetail> details = new ArrayList<>();

            for (InvoiceDetailDTO detailDTO : invoiceDTO.getDetails()) {
                System.out.println("Processing detail - Product ID: " + detailDTO.getProductId() + ", Quantity: "
                        + detailDTO.getQuantity());

                // Lấy thông tin sản phẩm
                Product product = productRepository.findById(detailDTO.getProductId());
                if (product == null) {
                    System.out.println("Product not found with ID: " + detailDTO.getProductId());
                    return new InvoiceDTO.InvoiceResult(false, "Không tìm thấy sản phẩm!");
                }
                System.out.println("Found product: " + product.getName() + ", Stock: " + product.getStock());

                // Kiểm tra stock
                if (product.getStock() == 0) {
                    System.out.println("Product out of stock: " + product.getName());
                    return new InvoiceDTO.InvoiceResult(false, "Failed, san pham khong con hang", product.getName());
                }

                if (product.getStock() < detailDTO.getQuantity()) {
                    System.out.println("Insufficient stock for product: " + product.getName() + ". Available: "
                            + product.getStock() + ", Requested: " + detailDTO.getQuantity());
                    return new InvoiceDTO.InvoiceResult(false,
                            "Sản phẩm '" + product.getName() + "' chỉ còn " + product.getStock()
                                    + " trong kho, không đủ cho yêu cầu " + detailDTO.getQuantity(),
                            product.getName());
                }

                // Tạo chi tiết hóa đơn
                InvoiceDetail detail = new InvoiceDetail();
                detail.setInvoice(invoice);
                detail.setProduct(product);
                detail.setQuantity(detailDTO.getQuantity());
                detail.setUnitPrice(product.getPrice());
                System.out.println("Created detail: " + detail);

                // Cập nhật tổng tiền
                BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(detailDTO.getQuantity()));
                totalAmount = totalAmount.add(subtotal);

                // Cập nhật số lượng tồn kho
                int oldStock = product.getStock();
                product.setStock(product.getStock() - detailDTO.getQuantity());
                boolean updateSuccess = productRepository.update(product);
                if (!updateSuccess) {
                    System.out.println("Failed to update product stock for: " + product.getName());
                    return new InvoiceDTO.InvoiceResult(false,
                            "Lỗi cập nhật số lượng tồn kho cho sản phẩm: " + product.getName());
                }
                System.out.println("Updated product stock: " + oldStock + " -> " + product.getStock());

                details.add(detail);
                System.out.println("Added detail for product: " + product.getName() + ", Subtotal: " + subtotal);
            }

            // 4. Lưu hóa đơn trước
            invoice.setTotalAmount(totalAmount);
            System.out.println("Total amount: " + totalAmount);
            System.out.println("Invoice details count: " + details.size());
            System.out.println("Final invoice object: " + invoice);

            System.out.println("Attempting to save invoice...");
            boolean invoiceSaved = invoiceRepository.save(invoice);
            if (!invoiceSaved) {
                System.out.println("Failed to save invoice");
                return new InvoiceDTO.InvoiceResult(false, "Lỗi lưu hóa đơn!");
            }
            System.out.println("Invoice saved successfully with ID: " + invoice.getId());

            // 5. Lưu chi tiết hóa đơn sau khi có ID của invoice
            System.out.println("Attempting to save invoice details...");
            boolean detailsSaved = invoiceDetailRepository.saveAll(details);
            if (!detailsSaved) {
                System.out.println("Failed to save invoice details");
                return new InvoiceDTO.InvoiceResult(false, "Lỗi lưu chi tiết hóa đơn!");
            }
            System.out.println("Invoice details saved successfully");

            System.out.println("=== Invoice creation completed successfully ===");
            return new InvoiceDTO.InvoiceResult(true, "Tạo hóa đơn thành công!");

        } catch (Exception e) {
            System.out.println("=== Error in invoice creation ===");
            e.printStackTrace();
            return new InvoiceDTO.InvoiceResult(false, "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        System.out.println("Retrieved " + (invoices != null ? invoices.size() : 0) + " invoices");
        return invoices;
    }

    public List<Invoice> searchInvoices(String search, String dateFrom, String dateTo) {
        List<Invoice> invoices = invoiceRepository.searchInvoices(search, dateFrom, dateTo);
        System.out.println("Search results: " + (invoices != null ? invoices.size() : 0) + " invoices");
        return invoices;
    }

    public Invoice getInvoiceById(Integer id) {
        return invoiceRepository.findById(id);
    }

    public List<InvoiceDetail> getInvoiceDetails(Integer invoiceId) {
        return invoiceDetailRepository.findByInvoiceId(invoiceId);
    }

    public boolean updateInvoiceStatus(Integer invoiceId, Invoice.InvoiceStatus newStatus) {
        try {
            return invoiceRepository.updateStatus(invoiceId, newStatus);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public InvoiceDTO.PageResult<Invoice> searchInvoicesWithPagination(String search, String dateFrom, String dateTo,
            int page, int pageSize) {
        // Validate page and pageSize
        if (page < 1)
            page = 1;
        if (pageSize < 1)
            pageSize = 8;

        // Get total count
        long totalElements = invoiceRepository.countInvoices(search, dateFrom, dateTo);

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        // Get paginated results
        List<Invoice> invoices = invoiceRepository.searchInvoicesWithPagination(search, dateFrom, dateTo, page,
                pageSize);

        System.out.println("Pagination results: page " + page + ", size " + pageSize + ", total " + totalElements
                + ", pages " + totalPages);

        return new InvoiceDTO.PageResult<>(invoices, page, totalPages, totalElements, pageSize);
    }
}
