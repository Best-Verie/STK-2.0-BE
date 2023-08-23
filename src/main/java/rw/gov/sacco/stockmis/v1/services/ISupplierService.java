package rw.gov.sacco.stockmis.v1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateSupplierDTO;
import rw.gov.sacco.stockmis.v1.models.Supplier;

import java.util.Optional;
import java.util.UUID;

public interface ISupplierService {

    long count();
    Supplier create(CreateOrUpdateSupplierDTO supplierDTO);

    Supplier findById(UUID id);

    boolean existsById(UUID id);

    Iterable<Supplier> findAll();
    Page<Supplier> findAll(String name, String tinNumber, String phoneNumber, Pageable pageable);

    Supplier update(UUID id, CreateOrUpdateSupplierDTO supplierDTO);

    boolean delete(UUID id);

    Optional<Supplier> findByName(String supplierName);
}
