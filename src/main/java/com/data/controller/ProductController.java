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
import java.util.HashMap;
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

    @GetMapping("/product/check-name")
    @ResponseBody
    public Map<String, Boolean> checkProductName(
            @RequestParam String name,
            @RequestParam(required = false) Integer id) {
        Map<String, Boolean> response = new HashMap<>();
        if (id != null) {
            response.put("exists", productService.isNameDuplicate(name, id));
        } else {
            response.put("exists", productService.isNameDuplicate(name));
        }
        return response;
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

    // Thêm vào ProductController.java
    @PostMapping("/product/edit")
    public String editProduct(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("brand") String brand,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        try {
            if (productService.isNameDuplicate(name)) {
                redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại!");
                return "redirect:/admin/product";
            }

            // Validate dữ liệu
            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên sản phẩm không được để trống");
                return "redirect:/admin/product";
            }

            if (brand == null || brand.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Brand không được để trống");
                return "redirect:/admin/product";
            }

            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Giá phải lớn hơn 0");
                return "redirect:/admin/product";
            }

            if (stock == null || stock < 0) {
                redirectAttributes.addFlashAttribute("error", "Số lượng phải lớn hơn 0");
                return "redirect:/admin/product";
            }

            // Kiểm tra sản phẩm tồn tại
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
                return "redirect:/admin/product";
            }

            // Kiểm tra tên trùng lặp
            if (productService.isNameDuplicate(name, id)) {
                redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại!");
                return "redirect:/admin/product";
            }

            // Cập nhật thông tin sản phẩm
            existingProduct.setName(name);
            existingProduct.setBrand(brand);
            existingProduct.setPrice(price);
            existingProduct.setStock(stock);

            // Nếu có upload ảnh mới
            if (image != null && !image.isEmpty()) {
                Map<String, Object> uploadResult = cloudinary.uploader().upload(
                        image.getBytes(),
                        ObjectUtils.emptyMap()
                );
                String imageUrl = (String) uploadResult.get("url");
                existingProduct.setImage(imageUrl);
            }

            // Lưu vào database
            boolean success = productService.updateProduct(existingProduct);

            if (success) {
                redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Cập nhật sản phẩm thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/product";
    }
}
