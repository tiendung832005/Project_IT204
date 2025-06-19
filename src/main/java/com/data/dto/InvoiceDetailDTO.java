package com.data.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InvoiceDetailDTO {
    @NotNull(message = "Vui lòng chọn sản phẩm!")
    private Integer productId;

    @NotNull(message = "Vui lòng nhập số lượng!")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0!")
    private Integer quantity;

    private BigDecimal unitPrice;
    private String productName;
    private BigDecimal subtotal;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
