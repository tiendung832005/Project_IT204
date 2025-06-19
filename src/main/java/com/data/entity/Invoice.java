package com.data.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "invoice")
@Getter
@Setter
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<InvoiceDetail> invoiceDetails = new ArrayList<>();

    public enum InvoiceStatus {
        PENDING, CONFIRMED, SHIPING, COMPLETED, CANCELED
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", customerId=" + (customer != null ? customer.getId() : "null") +
                ", customerName=" + (customer != null ? customer.getName() : "null") +
                ", createdAt=" + createdAt +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", invoiceDetailsCount=" + (invoiceDetails != null ? invoiceDetails.size() : 0) +
                '}';
    }
}
