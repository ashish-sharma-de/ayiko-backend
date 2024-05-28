package com.ayiko.backend.service.impl;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.CustomerDTO;
import com.ayiko.backend.dto.ImageDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.dto.cart.AddressDTO;
import com.ayiko.backend.repository.CustomerRepository;
import com.ayiko.backend.repository.entity.CustomerEntity;
import com.ayiko.backend.repository.entity.CustomerImageEntity;
import com.ayiko.backend.repository.entity.SupplierEntity;
import com.ayiko.backend.service.CustomerService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;


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
        Optional<CustomerEntity> byId = repository.findById(id);
        if(byId.isPresent()){
            CustomerEntity CustomerEntity = byId.get();
            CustomerEntity.setEmailAddress(CustomerDTO.getEmailAddress() != null ? CustomerDTO.getEmailAddress() : CustomerEntity.getEmailAddress());
            CustomerEntity.setPhoneNumber(CustomerDTO.getPhoneNumber() != null ? CustomerDTO.getPhoneNumber() : CustomerEntity.getPhoneNumber());
            CustomerEntity.setFullName(CustomerDTO.getFullName() != null ? CustomerDTO.getFullName() : CustomerEntity.getFullName());
            return EntityDTOConverter.convertCustomerEntityToCustomerDTO(repository.save(CustomerEntity));
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
    @Override
    public void addAddress(UUID customerId, AddressDTO addressDTO) {
        CustomerEntity customer = repository.findById(customerId).orElse(null);
        if (customer != null) {
            customer.getDeliveryAddresses().add(EntityDTOConverter.convertAddressDTOToAddressEntity(addressDTO));
            repository.save(customer);
        }
    }

    @Override
    public void deleteAddress(UUID customerId, UUID addressId) {
        CustomerEntity customer = repository.findById(customerId).orElse(null);
        if (customer != null) {
            customer.getDeliveryAddresses().removeIf(addressEntity -> addressEntity.getId().equals(addressId));
            repository.save(customer);
        }
    }

    @Override
    public void uploadProfilePicture(UUID customerId, ImageDTO profilePictureDTO) {
        CustomerEntity customer = repository.findById(customerId).orElse(null);
        if (customer != null) {
            CustomerImageEntity customerImageEntity = EntityDTOConverter.convertImageDTOToCustomerImageEntity(profilePictureDTO);
            customerImageEntity.setCustomer(customer);
            customer.setProfileImage(customerImageEntity);
            repository.save(customer);
        }
    }

}
