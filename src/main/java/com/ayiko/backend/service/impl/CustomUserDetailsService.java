package com.ayiko.backend.service.impl;

import com.ayiko.backend.repository.CustomerRepository;
import com.ayiko.backend.repository.SupplierRepository;
import com.ayiko.backend.repository.entity.CustomerEntity;
import com.ayiko.backend.repository.entity.SupplierEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SupplierEntity supplier = supplierRepository.findByEmailAddress(username)
                .orElseThrow(() -> new UsernameNotFoundException("Supplier not found with email: " + username));
        System.out.println("Supplier: " + supplier.getPassword());
        return new User(supplier.getEmailAddress(), supplier.getPassword(), Collections.emptyList());
    }
}

