package com.data.service;

import com.data.entity.Customer;
import com.data.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getCustomers(int page, int pageSize) {
        return customerRepository.getCustomers(page, pageSize);
    }

    public long getTotalCustomers() {
        return customerRepository.countTotalCustomers();
    }

    public boolean addCustomer(Customer customer) {
        try {
            return customerRepository.save(customer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEmailDuplicate(String email) {
        return customerRepository.existsByEmail(email);
    }

    public boolean isEmailDuplicate(String email, Integer id) {
        return customerRepository.existsByEmailAndIdNot(email, id);
    }

    public boolean isPhoneDuplicate(String phone) {
        return customerRepository.existsByPhone(phone);
    }

    public boolean isPhoneDuplicate(String phone, Integer id) {
        return customerRepository.existsByPhoneAndIdNot(phone, id);
    }

    public boolean updateCustomerStatus(Integer id, String newStatus) {
        return customerRepository.updateStatus(id, newStatus);
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    public boolean updateCustomer(Customer customer) {
        try {
            return customerRepository.updateCustomer(customer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Customer> getAllCustomers() {
        try {
            return customerRepository.getAllCustomers();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
}