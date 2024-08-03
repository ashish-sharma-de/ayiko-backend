package com.ayiko.backend.service.core;
import com.ayiko.backend.dto.ImageDTO;
import com.ayiko.backend.dto.LoginDTO;
import com.ayiko.backend.dto.SupplierDTO;
import java.util.List;
import java.util.UUID;

public interface SupplierService {
    SupplierDTO createSupplier(SupplierDTO supplierDTO);
    SupplierDTO updateSupplier(UUID id, SupplierDTO supplierDTO);
    boolean deleteSupplier(UUID id);
    SupplierDTO getSupplierById(UUID id);
    List<SupplierDTO> getAllSuppliers();

    SupplierDTO getSupplierByEmail(String email);

    boolean resetPassword(UUID id, String currentPassword, String newPassword);

    String authenticateSupplier(LoginDTO loginDTO);

    void addBusinessImage(UUID supplierId, ImageDTO imageDTO);

    void deleteBusinessImage(UUID id, ImageDTO imageDTO);

    List<SupplierDTO> searchSupplier(String searchQuery);

    List<SupplierDTO> findNearbySuppliers(double lat, double lon, double distance);

}
