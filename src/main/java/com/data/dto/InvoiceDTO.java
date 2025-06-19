package com.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvoiceDTO {
    @NotNull(message = "Vui lòng chọn khách hàng!")
    private Integer customerId;

    private List<InvoiceDetailDTO> details;

    public InvoiceDTO() {
        this.details = new ArrayList<>();
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<InvoiceDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<InvoiceDetailDTO> details) {
        this.details = details;
    }

    public static class PageResult<T> {
        private List<T> content;
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int pageSize;
        private boolean hasNext;
        private boolean hasPrevious;

        public PageResult(List<T> content, int currentPage, int totalPages, long totalElements, int pageSize) {
            this.content = content;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.pageSize = pageSize;
            this.hasNext = currentPage < totalPages;
            this.hasPrevious = currentPage > 1;
        }

        // Getters and setters
        public List<T> getContent() {
            return content;
        }

        public void setContent(List<T> content) {
            this.content = content;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
    }

    public static class InvoiceResult {
        private boolean success;
        private String message;
        private String productName;

        public InvoiceResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public InvoiceResult(boolean success, String message, String productName) {
            this.success = success;
            this.message = message;
            this.productName = productName;
        }

        // Getters and setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
    }
}
