package com.ayiko.backend.service;

import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.LoginDTO;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO CustomerDTO);
    CustomerDTO updateCustomer(UUID id, CustomerDTO CustomerDTO);
    boolean deleteCustomer(UUID id);
    CustomerDTO getCustomerById(UUID id);
    List<CustomerDTO> getAllCustomers();
    boolean resetPassword(UUID id, String currentPassword, String newPassword);
    String authenticateSupplier(LoginDTO loginDTO);
    CustomerDTO getCustomerByEmail(String email);
}
