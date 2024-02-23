package com.ayiko.backend.service.impl;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.repository.CustomerRepository;
import com.ayiko.backend.repository.entity.CustomerEntity;
import com.ayiko.backend.repository.entity.SupplierEntity;
import com.ayiko.backend.service.CustomerService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JWTTokenProvider tokenProvider;

    @Override
    public CustomerDTO createCustomer(CustomerDTO CustomerDTO) {
        CustomerDTO.setPassword(passwordEncoder.encode(CustomerDTO.getPassword()));
        CustomerEntity save = repository.save(EntityDTOConverter.convertCustomerDTOToCustomerEntity(CustomerDTO));
        return EntityDTOConverter.convertCustomerEntityToCustomerDTO(save);
    }

    @Override
    public CustomerDTO updateCustomer(UUID id, CustomerDTO CustomerDTO) {
        CustomerDTO customerDTO = repository.findById(id).map(EntityDTOConverter::convertCustomerEntityToCustomerDTO).orElse(null);
        if (customerDTO != null) {
            CustomerDTO.setId(id);
            CustomerEntity save = repository.save(EntityDTOConverter.convertCustomerDTOToCustomerEntity(CustomerDTO));
            return EntityDTOConverter.convertCustomerEntityToCustomerDTO(save);
        }
        return null;
    }

    @Override
    public boolean deleteCustomer(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public CustomerDTO getCustomerById(UUID id) {
        return repository.findById(id).map(EntityDTOConverter::convertCustomerEntityToCustomerDTO).orElse(null);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return repository.findAll().stream().map(EntityDTOConverter::convertCustomerEntityToCustomerDTO).toList();
    }

    @Override
    public boolean resetPassword(UUID CustomerId, String currentPassword, String newPassword) {
        CustomerEntity customer = repository.findById(CustomerId).orElse(null);
        if (customer != null && passwordEncoder.matches(currentPassword, customer.getPassword())) {
            customer.setPassword(passwordEncoder.encode(newPassword));
            repository.save(customer);
            return true;
        }
        return false;
    }

    @Override
    public String authenticateSupplier(LoginDTO loginDTO) {
        CustomerEntity customer = repository.findByEmailAddress(loginDTO.getUsername()).orElse(null);
        if(customer != null && passwordEncoder.matches(loginDTO.getPassword(), customer.getPassword())) {
            return tokenProvider.generateToken(customer.getEmailAddress());
        }
        return null;
    }

    @Override
    public CustomerDTO getCustomerByEmail(String email) {
        return repository.findByEmailAddress(email).map(EntityDTOConverter::convertCustomerEntityToCustomerDTO).orElse(null);
    }


}
