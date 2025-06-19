package com.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_details")
@Getter
@Setter
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonBackReference
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Override
    public String toString() {
        return "InvoiceDetail{" +
                "id=" + id +
                ", invoiceId=" + (invoice != null ? invoice.getId() : "null") +
                ", productId=" + (product != null ? product.getId() : "null") +
                ", productName=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
