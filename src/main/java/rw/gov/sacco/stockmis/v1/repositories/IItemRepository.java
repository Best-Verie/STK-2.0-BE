package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.sacco.stockmis.v1.enums.EStockStatus;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.models.ItemCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IItemRepository extends JpaRepository<Item, UUID> {
    boolean existsByName(String name);

    Optional<Item> findByName(String name);

    Page<Item> findAllByStockStatus(EStockStatus stockStatus, Pageable pageable);

    long count();

    Page<Item> findAllByItemCategory_Id(UUID categoryId, Pageable pageable);

    List<Item> findAllByItemCategory(ItemCategory itemCategory);

    Page<Item> findAllByItemCategory_IdAndStockStatus(UUID categoryId, EStockStatus status, Pageable pageable);

    Page<Item> findAllByStockStatusAndNameContainingIgnoreCase(EStockStatus status, String name, Pageable pageable);

    Page<Item> findAllByItemCategory_IdAndStockStatusAndNameContainingIgnoreCase(UUID categoryId, EStockStatus status, String name, Pageable pageable);

    Page<Item> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Item> findAllByItemCategory_IdAndNameContainingIgnoreCase(UUID categoryId, String name, Pageable pageable);

    Page<Item> findAllByStockStatusNot(EStockStatus deleted, Pageable pageable);
}

