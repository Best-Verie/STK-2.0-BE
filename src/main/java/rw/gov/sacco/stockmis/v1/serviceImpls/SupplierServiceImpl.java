package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateSupplierDTO;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.models.Supplier;
import rw.gov.sacco.stockmis.v1.repositories.ISupplierRepository;
import rw.gov.sacco.stockmis.v1.services.IItemService;
import rw.gov.sacco.stockmis.v1.services.ISupplierService;

import java.util.Optional;
import java.util.UUID;

@Service
public class SupplierServiceImpl implements ISupplierService {

    private final ISupplierRepository supplierRepository;
    private final IItemService itemService;

    @Autowired
    public SupplierServiceImpl(ISupplierRepository supplierRepository, @Lazy IItemService itemService) {
        this.supplierRepository = supplierRepository;
        this.itemService = itemService;
    }

    @Override
    public long count() {
        return supplierRepository.count();
    }

    @Override
    public Supplier create(CreateOrUpdateSupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        //if name already exists throw exception
        if (supplierRepository.existsByName(supplierDTO.getName())) {
            throw new RuntimeException("Supplier with name " + supplierDTO.getName() + " already exists");
        }
        supplier.setName(supplierDTO.getName());

        supplier.setAddress(supplierDTO.getAddress());
        supplier.setEmail(supplierDTO.getEmail());
        supplier.setPhone(supplierDTO.getPhone());
        supplier.setTinNumber(supplierDTO.getTinNumber());
        supplier.setDeliveryTerms(supplierDTO.getDeliveryTerms());
        supplier.setWarrantyPeriod(supplierDTO.getWarrantyPeriod());

        //foreach loop to add items supplied
        supplierDTO.getItemsSupplied().forEach(item -> {
            Item foundItem = itemService.findById(item);

            if (supplier.getItemsSupplied() == null) {
                supplier.setItemsSupplied(new java.util.ArrayList<>());
            }


            if (supplier.getItemsSupplied().contains(foundItem)) {
                throw new RuntimeException("Item with id " + item + " already exists in the list");
            }

            supplier.getItemsSupplied().add(foundItem);
        });

        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier findById(UUID id) {
        return supplierRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<Supplier> findByName(String supplierName) {
        //search for best match for firstname and lastname combined in repository
        return supplierRepository.findByName(supplierName);

    }

    @Override
    public boolean existsById(UUID id) {
        return supplierRepository.existsById(id);
    }

    @Override
    public Iterable<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Page<Supplier> findAll(String name, String tinNumber, String phoneNumber, Pageable pageable) {
        if(name != null && tinNumber != null && phoneNumber != null){
            return supplierRepository.findAllByNameContainingIgnoreCaseAndTinNumberContainingIgnoreCaseAndPhoneContainingIgnoreCase(name, tinNumber, phoneNumber, pageable);
        }else if(name != null && tinNumber != null){
            return supplierRepository.findAllByNameContainingIgnoreCaseAndTinNumberContainingIgnoreCase(name, tinNumber, pageable);
        }else if(name != null && phoneNumber != null){
            return supplierRepository.findAllByNameContainingIgnoreCaseAndPhoneContainingIgnoreCase(name, phoneNumber, pageable);
        }else if(tinNumber != null && phoneNumber != null){
            return supplierRepository.findAllByTinNumberContainingIgnoreCaseAndPhoneContainingIgnoreCase(tinNumber, phoneNumber, pageable);
        }else if(name != null){
            return supplierRepository.findAllByNameContainingIgnoreCase(name, pageable);
        } else if(tinNumber != null){
            return supplierRepository.findAllByTinNumberContainingIgnoreCase(tinNumber, pageable);
        }else if(phoneNumber != null){
            return supplierRepository.findAllByPhoneContainingIgnoreCase(phoneNumber, pageable);
        }else{
            return supplierRepository.findAll(pageable);
        }
    }

    @Override
    public Supplier update(UUID id, CreateOrUpdateSupplierDTO supplierDTO) {
        Supplier supplier = findById(id);
        if (supplier == null) {
            return null;
        }
        supplier.setName(supplierDTO.getName());
        supplier.setAddress(supplierDTO.getAddress());
        supplier.setEmail(supplierDTO.getEmail());
        supplier.setPhone(supplierDTO.getPhone());
        supplier.setTinNumber(supplierDTO.getTinNumber());
        supplier.setDeliveryTerms(supplierDTO.getDeliveryTerms());
        supplier.setWarrantyPeriod(supplierDTO.getWarrantyPeriod());
        return supplierRepository.save(supplier);
    }

    @Override
    public boolean delete(UUID id) {
        if (!existsById(id)) {
            return false;
        }
        supplierRepository.deleteById(id);
        return true;
    }
}
