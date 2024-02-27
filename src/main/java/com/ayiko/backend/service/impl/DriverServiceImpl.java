package com.ayiko.backend.service.impl;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.DriverDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.repository.DriverEntityRepository;
import com.ayiko.backend.repository.entity.DriverEntity;
import com.ayiko.backend.service.DriverService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverEntityRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JWTTokenProvider tokenProvider;


    @Override
    public DriverDTO createDriver(DriverDTO driverDTO) {
        driverDTO.setPassword(passwordEncoder.encode(driverDTO.getPassword()));
        DriverEntity driverEntity = EntityDTOConverter.convertDriverDTOToEntity(driverDTO);
        return EntityDTOConverter.convertDriverEntityToDTO(repository.save(driverEntity));
    }

    @Override
    public DriverDTO updateDriver(UUID id, DriverDTO driverDTO) {
        repository.findById(id).ifPresent(driverEntity -> {
            driverEntity.setName(driverDTO.getName());
            driverEntity.setEmail(driverDTO.getEmail());
            driverEntity.setPhone(driverDTO.getPhone());
            driverEntity.setVehicleNumber(driverDTO.getVehicleNumber());
            driverEntity.setPassword(driverDTO.getPassword());
            repository.save(driverEntity);
        });
        return driverDTO;
    }

    @Override
    public boolean deleteDriver(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public DriverDTO getDriverById(UUID id) {
        DriverEntity driverEntity = repository.findById(id).orElse(null);
        if (driverEntity == null) return null;
        return EntityDTOConverter.convertDriverEntityToDTO(driverEntity);
    }

    @Override
    public List<DriverDTO> getAllDrivers() {
        return repository.findAll().stream().collect(ArrayList::new, (list, driverEntity) -> list.add(EntityDTOConverter.convertDriverEntityToDTO(driverEntity)), ArrayList::addAll);
    }

    @Override
    public DriverDTO getDriverByEmail(String email) {
        return repository.findByEmail(email).map(EntityDTOConverter::convertDriverEntityToDTO).orElse(null);
    }

    @Override
    public boolean resetPassword(UUID id, String currentPassword, String newPassword) {
        repository.findById(id).ifPresent(driverEntity -> {
            if (passwordEncoder.matches(currentPassword, driverEntity.getPassword())) {
                driverEntity.setPassword(passwordEncoder.encode(newPassword));
                repository.save(driverEntity);
            }
        });
        return false;
    }

    @Override
    public String authenticateDriver(LoginDTO loginDTO) {
        DriverEntity driverEntity = repository.findByEmail(loginDTO.getUsername()).orElse(null);
        if (driverEntity != null && passwordEncoder.matches(loginDTO.getPassword(), driverEntity.getPassword())) {
            return tokenProvider.generateToken(driverEntity.getEmail());
        }
        return null;
    }
}
