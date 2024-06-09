package com.ayiko.backend.service.core.impl;

import com.ayiko.backend.config.JWTTokenProvider;
import com.ayiko.backend.dto.DriverDTO;
import com.ayiko.backend.dto.ImageDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.repository.DriverEntityRepository;
import com.ayiko.backend.repository.entity.DriverEntity;
import com.ayiko.backend.repository.entity.DriverImageEntity;
import com.ayiko.backend.service.core.DriverService;
import com.ayiko.backend.util.converter.EntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<DriverEntity> byId = repository.findById(id);
        if(byId.isPresent()){
            DriverEntity driverEntity = byId.get();
            driverEntity.setName(driverDTO.getName() != null ? driverDTO.getName() : driverEntity.getName());
            driverEntity.setEmail(driverDTO.getEmail() != null ? driverDTO.getEmail() : driverEntity.getEmail());
            driverEntity.setPhone(driverDTO.getPhone() != null ? driverDTO.getPhone() : driverEntity.getPhone());
            driverEntity.setVehicleNumber(driverDTO.getVehicleNumber() != null ? driverDTO.getVehicleNumber() : driverEntity.getVehicleNumber());
            driverEntity.setPassword(driverDTO.getPassword() != null ? driverDTO.getPassword() : driverEntity.getPassword());
            driverEntity.setSupplierId(driverDTO.getSupplierId() != null ? driverDTO.getSupplierId() : driverEntity.getSupplierId());
            driverEntity.setActive(driverDTO.isActive());
            driverEntity.setStatus(driverDTO.getStatus() != null ? driverDTO.getStatus() : driverEntity.getStatus());
            return EntityDTOConverter.convertDriverEntityToDTO(repository.save(driverEntity));
        }
        return null;
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
    public boolean deactivateDriver(UUID id){
        if(repository.existsById(id)){
            DriverEntity driverEntity = repository.findById(id).get();
            driverEntity.setActive(false);
            repository.save(driverEntity);
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
    public List<DriverDTO> getAllDriversForSupplier(UUID supplierId) {
        Optional<List<DriverEntity>> queryResult = repository.findAllBySupplierId(supplierId);
        if(queryResult.isPresent()){
            return queryResult.get().stream().map(EntityDTOConverter::convertDriverEntityToDTO).collect(Collectors.toList());
        }else{
            return new ArrayList<>();
        }
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

    @Override
    public void uploadProfilePicture(UUID id, ImageDTO profilePictureDTO) {
        repository.findById(id).ifPresent(driverEntity -> {
            DriverImageEntity driverImageEntity = EntityDTOConverter.convertImageDTOToDriverImageEntity(profilePictureDTO);
            driverImageEntity.setDriver(driverEntity);
            driverEntity.setProfileImage(driverImageEntity);
            repository.save(driverEntity);
        });
    }
}
