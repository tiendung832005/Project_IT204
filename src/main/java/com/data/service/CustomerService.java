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

    @Transactional
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
}