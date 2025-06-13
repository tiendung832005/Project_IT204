package com.data.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.data.entity.Product;
import com.data.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private Cloudinary cloudinary;

    private static final int PAGE_SIZE = 8;

    @GetMapping("/product")
    public String listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String search,
            Model model) {

        List<Product> products;
        long totalProducts;

        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProductsByBrand(search, page, PAGE_SIZE);
            totalProducts = productService.getTotalProductsByBrand(search);
            model.addAttribute("search", search);
        } else {
            products = productService.getProducts(page, PAGE_SIZE);
            totalProducts = productService.getTotalProducts();
        }

        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/product";
    }

    @PostMapping("/product/add")
    public String addProduct(
            @RequestParam("name") String name,
            @RequestParam("brand") String brand,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stock") Integer stock,
            @RequestParam("image") MultipartFile image,
            RedirectAttributes redirectAttributes) {
        try {
            // Upload ảnh lên Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.emptyMap()
            );
            String imageUrl = (String) uploadResult.get("url");

            // Tạo sản phẩm mới
            Product product = new Product();
            product.setName(name);
            product.setBrand(brand);
            product.setPrice(price);
            product.setStock(stock);
            product.setImage(imageUrl);

            // Lưu vào database
            boolean success = productService.addProduct(product);

            if (success) {
                redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Thêm sản phẩm thất bại");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/product";
    }

    @PostMapping("/product/delete/{id}")
    public String deleteProduct(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            boolean success = productService.deleteProduct(id);
            if (success) {
                redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Xóa sản phẩm thất bại");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/product";
    }
}
