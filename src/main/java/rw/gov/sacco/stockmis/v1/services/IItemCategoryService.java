package rw.gov.sacco.stockmis.v1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemCategoryDTO;
import rw.gov.sacco.stockmis.v1.models.ItemCategory;

import java.util.UUID;

public interface IItemCategoryService {

    long count();

    boolean existsByName(String name);

    ItemCategory create(CreateOrUpdateItemCategoryDTO itemCategoryDTO);

    ItemCategory findById(UUID id);

    boolean existsById(UUID id);

    Iterable<ItemCategory> findAll();

    Page<ItemCategory> findAll(Pageable pageable);

    ItemCategory update(UUID id, CreateOrUpdateItemCategoryDTO itemCategoryDTO);

    boolean delete(UUID id);

    ItemCategory findByName(String categoryName);
}
