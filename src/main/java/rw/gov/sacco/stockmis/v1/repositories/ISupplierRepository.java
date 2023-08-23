package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.sacco.stockmis.v1.models.Supplier;

import java.util.Optional;
import java.util.UUID;

public interface ISupplierRepository extends JpaRepository<Supplier, UUID> {

    long count();

    Optional<Supplier> findByName(String supplierName);

    boolean existsByName(String name);

    Page<Supplier> findAllByNameContainingIgnoreCaseAndTinNumberContainingIgnoreCaseAndPhoneContainingIgnoreCase(String name, String tinNumber, String phoneNumber, Pageable pageable);

    Page<Supplier> findAllByNameContainingIgnoreCaseAndTinNumberContainingIgnoreCase(String name, String tinNumber, Pageable pageable);

    Page<Supplier> findAllByNameContainingIgnoreCaseAndPhoneContainingIgnoreCase(String name, String phoneNumber, Pageable pageable);

    Page<Supplier> findAllByTinNumberContainingIgnoreCaseAndPhoneContainingIgnoreCase(String tinNumber, String phoneNumber, Pageable pageable);

    Page<Supplier> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Supplier> findAllByTinNumberContainingIgnoreCase(String tinNumber, Pageable pageable);

    Page<Supplier> findAllByPhoneContainingIgnoreCase(String phoneNumber, Pageable pageable);
}
