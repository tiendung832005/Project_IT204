package com.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class InvoiceViewDTO {
    private Integer id;
    private String customerName;
    private Date createdAt;
    private String status;
    private BigDecimal totalAmount;
    private List<InvoiceDetailViewDTO> details;

    @Getter
    @Setter
    public static class InvoiceDetailViewDTO {
        private Integer id;
        private String productName;
        private String brand;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}