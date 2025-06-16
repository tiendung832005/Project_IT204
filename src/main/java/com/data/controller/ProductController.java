package com.data.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.data.dto.ProductDTO;
import com.data.entity.Product;
import com.data.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
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
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer stock,
            Model model) {

        int pageSize = PAGE_SIZE;

        List<Product> products = productService.searchProducts(search, minPrice, maxPrice, stock, page, pageSize);
        long totalProducts = productService.countProducts(search, minPrice, maxPrice, stock);
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("stock", stock);

        if (!model.containsAttribute("productDTO")) {
            model.addAttribute("productDTO", new ProductDTO());
        }

        return "admin/product";
    }


    @PostMapping("/product/add")
    public String addProduct(
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Lấy lại danh sách sản phẩm
            List<Product> products = productService.getProducts(1, PAGE_SIZE);
            model.addAttribute("products", products);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", (int) Math.ceil((double) productService.getTotalProducts() / PAGE_SIZE));
            // Giữ lại dữ liệu đã nhập
            model.addAttribute("productDTO", productDTO);
            return "admin/product";
        }

        try {
            // Upload ảnh lên Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    productDTO.getImage().getBytes(),
                    ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("url");

            // Tạo sản phẩm mới
            Product product = new Product();
            product.setName(productDTO.getName());
            product.setBrand(productDTO.getBrand());
            product.setPrice(productDTO.getPrice());
            product.setStock(productDTO.getStock());
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

    @PostMapping("/product/edit")
    public String editProduct(
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Lấy lại danh sách sản phẩm
            List<Product> products = productService.getProducts(1, PAGE_SIZE);
            model.addAttribute("products", products);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", (int) Math.ceil((double) productService.getTotalProducts() / PAGE_SIZE));
            // Giữ lại dữ liệu đã nhập
            model.addAttribute("productDTO", productDTO);
            return "admin/product";
        }

        try {
            // Kiểm tra sản phẩm tồn tại
            Product existingProduct = productService.getProductById(productDTO.getId());
            if (existingProduct == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
                return "redirect:/admin/product";
            }

            // Kiểm tra tên trùng lặp
            if (productService.isNameDuplicate(productDTO.getName(), productDTO.getId())) {
                redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại!");
                return "redirect:/admin/product";
            }

            // Cập nhật thông tin sản phẩm
            existingProduct.setName(productDTO.getName());
            existingProduct.setBrand(productDTO.getBrand());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setStock(productDTO.getStock());

            // Nếu có upload ảnh mới
            if (productDTO.getImage() != null && !productDTO.getImage().isEmpty()) {
                Map<String, Object> uploadResult = cloudinary.uploader().upload(
                        productDTO.getImage().getBytes(),
                        ObjectUtils.emptyMap());
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

    @GetMapping("/product/check-name")
    @ResponseBody
    public Map<String, Boolean> checkName(
            @RequestParam String name,
            @RequestParam(required = false) Integer id) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists;

        if (id != null) {
            exists = productService.isNameDuplicate(name, id);
        } else {
            exists = productService.isNameDuplicate(name);
        }

        response.put("exists", exists);
        return response;
    }
}