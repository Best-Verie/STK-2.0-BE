package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemCategoryDTO;
import rw.gov.sacco.stockmis.v1.exceptions.BadRequestException;
import rw.gov.sacco.stockmis.v1.exceptions.ResourceNotFoundException;
import rw.gov.sacco.stockmis.v1.models.ItemCategory;
import rw.gov.sacco.stockmis.v1.repositories.IItemCategoryRepository;
import rw.gov.sacco.stockmis.v1.services.IItemCategoryService;

import java.util.UUID;

@Service
public class ItemCategoryServiceImpl implements IItemCategoryService {

    private final IItemCategoryRepository itemCategoryRepository;

    @Autowired
    public ItemCategoryServiceImpl(IItemCategoryRepository itemCategoryRepository) {
        this.itemCategoryRepository = itemCategoryRepository;
    }

    @Override
    public long count() {
        return itemCategoryRepository.count();
    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }

    @Override
    public ItemCategory create(CreateOrUpdateItemCategoryDTO itemCategoryDTO) {
        ItemCategory itemCategory = new ItemCategory(itemCategoryDTO.getName());

        if (existsByName(itemCategoryDTO.getName())) {
            throw new BadRequestException(String.format("Item category with name %s already exists", itemCategoryDTO.getName()));
        }

        return itemCategoryRepository.save(itemCategory);
    }

    @Override
    public ItemCategory findById(UUID id) {
        return itemCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemCategory", "id", id.toString()));
    }

    @Override
    public boolean existsById(UUID id) {
        return itemCategoryRepository.existsById(id);
    }

    @Override
    public Iterable<ItemCategory> findAll() {
        return null;
    }

    @Override
    public Page<ItemCategory> findAll(Pageable pageable) {
        return itemCategoryRepository.findAll(pageable);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ItemCategory update(UUID itemCategoryId, CreateOrUpdateItemCategoryDTO itemCategoryDTO) {
        ItemCategory existingItemCategory = findById(itemCategoryId);

        if (itemCategoryRepository.existsByName(itemCategoryDTO.getName()) && (!existingItemCategory.getName().equals(itemCategoryDTO.getName()))) {
            throw new BadRequestException(String.format("ItemCategory with name %s already exists", itemCategoryDTO.getName()));
        }

        existingItemCategory.setName(itemCategoryDTO.getName());

        return itemCategoryRepository.save(existingItemCategory);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public boolean delete(UUID itemCategoryId) {
        if (!existsById(itemCategoryId)) {
            throw new ResourceNotFoundException("ItemCategory", "id", itemCategoryId.toString());
        }
        itemCategoryRepository.deleteById(itemCategoryId);
        return true;
    }

    @Override
    public ItemCategory findByName(String categoryName) {
        return itemCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("ItemCategory", "name", categoryName));
    }
}
