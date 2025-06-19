package com.data.dto;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class ProductDTO {
    private Integer id;

    @NotBlank(message = "Tên sản phẩm không được để trống!")
    private String name;

    @NotBlank(message = "Brand không được để trống!")
    private String brand;

    @NotNull(message = "Giá không được để trống!")
    @Positive(message = "Giá phải lớn hơn 0!")
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống!")
    @PositiveOrZero(message = "Số lượng phải lớn hơn hoặc bằng 0!")
    private Integer stock;

    @NotNull(message = "Vui lòng chọn ảnh sản phẩm!")
    private MultipartFile image;
    private String currentImage;

    @NotBlank(message = "Trạng thái không được để trống!")
    private String status;

    // Constructor
    public ProductDTO() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(String currentImage) {
        this.currentImage = currentImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}