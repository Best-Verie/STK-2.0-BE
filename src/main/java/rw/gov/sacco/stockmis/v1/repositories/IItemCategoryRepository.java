package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.sacco.stockmis.v1.models.ItemCategory;

import java.util.Optional;
import java.util.UUID;

public interface IItemCategoryRepository extends JpaRepository<ItemCategory, UUID> {
    boolean existsByName(String name);
    long count();
    Optional<ItemCategory> findByName(String categoryName);
}
