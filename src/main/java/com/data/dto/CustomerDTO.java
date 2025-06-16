package com.data.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CustomerDTO {
    private Integer id;

    @NotBlank(message = "Tên không được để trống!")
    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự!")
    @Pattern(regexp = "^[^\\s]*[a-zA-Z0-9À-ỹ][^\\s]*$", message = "Tên không được chứa ký tự đặc biệt và không được toàn khoảng trắng!")
    private String name;

    @Pattern(regexp = "^(0[0-9]{9}|\\s*)$", message = "Số điện thoại không đúng định dạng!")
    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự!")
    private String phone;

    @Email(message = "Email không đúng định dạng!")
    @Pattern(regexp = "^[^\\s]*[a-zA-Z0-9@._-][^\\s]*$", message = "Email không được chứa khoảng trắng!")
    private String email;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự!")
    private String address;

    private String status;

    public CustomerDTO() {
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
