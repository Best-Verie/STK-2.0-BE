package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rw.gov.sacco.stockmis.v1.dtos.CreateManyItemEntriesFromExcelDTO;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemEntryDTO;
import rw.gov.sacco.stockmis.v1.dtos.ItemEntryFromExcelDTO;
import rw.gov.sacco.stockmis.v1.enums.EEntryType;
import rw.gov.sacco.stockmis.v1.exceptions.BadRequestException;
import rw.gov.sacco.stockmis.v1.exceptions.ResourceNotFoundException;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.models.ItemEntry;
import rw.gov.sacco.stockmis.v1.models.ItemEntryTrace;
import rw.gov.sacco.stockmis.v1.models.Supplier;
import rw.gov.sacco.stockmis.v1.repositories.IItemEntryRepository;
import rw.gov.sacco.stockmis.v1.repositories.IItemEntryTraceRepository;
import rw.gov.sacco.stockmis.v1.repositories.IItemRepository;
import rw.gov.sacco.stockmis.v1.services.IItemEntryService;
import rw.gov.sacco.stockmis.v1.services.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import rw.gov.sacco.stockmis.v1.services.ISupplierService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ItemEntryServiceImpl implements IItemEntryService {
    private final IItemEntryRepository itemEntryRepository;
    private final IItemService itemService;
    private final IItemRepository itemRepository;
    private final ISupplierService supplierService;

    private final IItemEntryTraceRepository itemEntryTraceRepository;

    @Autowired
    public ItemEntryServiceImpl(IItemEntryRepository itemEntryRepository, IItemService itemService, ISupplierService supplierService, @Lazy IItemRepository itemRepository, @Lazy IItemEntryTraceRepository itemEntryTraceRepository) {
        this.itemEntryRepository = itemEntryRepository;
        this.itemService = itemService;
        this.supplierService = supplierService;
        this.itemRepository = itemRepository;
        this.itemEntryTraceRepository = itemEntryTraceRepository;
    }

    @Override
    public ItemEntry create(CreateOrUpdateItemEntryDTO itemEntryDTO) {
        Item item = itemService.findById(itemEntryDTO.getItemId());

        ItemEntry itemEntry = null;

        if (itemEntryDTO.getEntryType() == EEntryType.DIRECT_PURCHASE) {
            if (itemEntryDTO.getDirectPurchaseSupplier() == null || itemEntryDTO.getDirectPurchasePrice() == null) {
                throw new BadRequestException("Direct Purchase Supplier and Direct Purchase Price are required");
            }

            itemEntry = new ItemEntry(itemEntryDTO.getEntryType(), item, itemEntryDTO.getQuantity(), itemEntryDTO.getDirectPurchaseSupplier(), itemEntryDTO.getDirectPurchasePrice(), itemEntryDTO.getDateOfPurchase());
        } else if (itemEntryDTO.getEntryType() == EEntryType.CONTRACT) {
            if (itemEntryDTO.getSupplierId() == null) {
                throw new BadRequestException("Supplier Id is required");
            }

            Supplier supplier = supplierService.findById(itemEntryDTO.getSupplierId());

            itemEntry = new ItemEntry(itemEntryDTO.getEntryType(), item, itemEntryDTO.getQuantity(), supplier, item.getPrice(), itemEntryDTO.getDateOfPurchase());
        }
        else {
            throw new BadRequestException("Entry Type is required");
        }

        ItemEntry savedItemEntry = itemEntryRepository.save(itemEntry);

        itemService.incrementNbrOfItemsAvailable(item.getId(), itemEntryDTO.getQuantity());

        return savedItemEntry;
    }

    @Override
    public List<ItemEntry> findAllByItemAndDateOfPurchaseBetween(Item item, LocalDate startDate, LocalDate endDate) {
        return itemEntryRepository.findAllByItemAndDateOfPurchaseBetween(item, startDate, endDate);
    }

    @Override
//    @Async
//    @Transactional
    public Boolean createManyItemEntriesFromExcel(CreateManyItemEntriesFromExcelDTO itemEntryDTO) {
        for (ItemEntryFromExcelDTO itemEntryFromExcelDTO : itemEntryDTO.getItemEntries()) {
            validateItemEntryFromExcel(itemEntryFromExcelDTO);
        }

        for (ItemEntryFromExcelDTO itemEntryFromExcelDTO : itemEntryDTO.getItemEntries()) {
            CreateOrUpdateItemEntryDTO createOrUpdateItemEntryDTO = transformItemEntryFromExcelToDTO(itemEntryFromExcelDTO);
            create(createOrUpdateItemEntryDTO);
        }

        return true;
    }

    @Override
    public ItemEntry findById(UUID id) {
        return itemEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemEntry", "id", id.toString()));
    }

    @Override
    public boolean existsById(UUID id) {
        return itemEntryRepository.existsById(id);
    }

    @Override
    public Iterable<ItemEntry> findAll() {
        return itemEntryRepository.findAll();
    }

    @Override
    public Page<ItemEntry> findAll(Pageable pageable) {
        return itemEntryRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public ItemEntry update(UUID id, CreateOrUpdateItemEntryDTO itemEntryDTO, String reason) {

        Item item = itemService.findById(itemEntryDTO.getItemId());
        ItemEntry itemEntry = findById(id);
        ItemEntry oldItemEntry = new ItemEntry(itemEntry);
        Supplier newSupplier;
        Double newPrice;

        /* record update in ItemEntryTrace */

        if (itemEntryDTO.getEntryType() == EEntryType.CONTRACT) {
            if (itemEntryDTO.getSupplierId() == null) {
                throw new BadRequestException("Supplier Id is required");
            }
            newSupplier = supplierService.findById(itemEntryDTO.getSupplierId());
            newPrice = item.getPrice();
        }else {
            newSupplier = null;
            newPrice = itemEntryDTO.getDirectPurchasePrice();
        }

        ItemEntryTrace itemEntryTrace = new ItemEntryTrace(itemEntry, item, newSupplier, newPrice, itemEntryDTO, reason);
        itemEntryTraceRepository.save(itemEntryTrace);

        itemEntry.setEntryType(itemEntryDTO.getEntryType());
        itemEntry.setItem(item);

        if (itemEntryDTO.getEntryType() == EEntryType.DIRECT_PURCHASE) {
            if (itemEntryDTO.getDirectPurchaseSupplier() == null || itemEntryDTO.getDirectPurchasePrice() == null) {
                throw new BadRequestException("Direct Purchase Supplier and Direct Purchase Price are required");
            }

            itemEntry.setQuantity(itemEntryDTO.getQuantity());
            itemEntry.setDirectPurchaseSupplier(itemEntryDTO.getDirectPurchaseSupplier());
            itemEntry.setPrice(itemEntryDTO.getDirectPurchasePrice());
            itemEntry.setDateOfPurchase(itemEntryDTO.getDateOfPurchase());
        } else if (itemEntryDTO.getEntryType() == EEntryType.CONTRACT) {
            if (itemEntryDTO.getSupplierId() == null) {
                throw new BadRequestException("Supplier is required");
            }
            Supplier supplier = supplierService.findById(itemEntryDTO.getSupplierId());

            itemEntry.setQuantity(itemEntryDTO.getQuantity());
            itemEntry.setSupplier(supplier);
            itemEntry.setPrice(item.getPrice());
            itemEntry.setDateOfPurchase(itemEntryDTO.getDateOfPurchase());
        }
        else {
            throw new BadRequestException("Entry Type is required");
        }

        //if item is not changed
        if (itemEntry.getItem().getId() == oldItemEntry.getItem().getId()) {

            if (itemEntryDTO.getQuantity() > oldItemEntry.getQuantity()) {
                itemService.incrementNbrOfItemsAvailable(item.getId(), itemEntryDTO.getQuantity() - oldItemEntry.getQuantity());
            }
            //if quantity is decreased
            else if (itemEntryDTO.getQuantity() < oldItemEntry.getQuantity()) {
                itemService.decrementNbrOfItemsAvailable(item.getId(), oldItemEntry.getQuantity() - itemEntryDTO.getQuantity());
            }
        } else {
            itemService.incrementNbrOfItemsAvailable(item.getId(), itemEntryDTO.getQuantity());

            itemService.decrementNbrOfItemsAvailable(oldItemEntry.getItem().getId(), oldItemEntry.getQuantity());
        }


        return itemEntryRepository.save(itemEntry);
    }

    @Override
    public boolean delete(UUID id) {
        ItemEntry itemEntry = findById(id);
        itemEntryRepository.delete(itemEntry);
        return true;
    }
    //

    public void validateItemEntryFromExcel(ItemEntryFromExcelDTO itemEntryFromExcel) {

        itemRepository.findByName(itemEntryFromExcel.getItemName()).orElseThrow(() -> new BadRequestException("Item with name " + itemEntryFromExcel.getItemName() + " does not exist"));

        //validations for nulls and throw exception saying which item name is null
        if (itemEntryFromExcel.getItemName() == null) {
            throw new BadRequestException("Item Name is required");
        }
        if (itemEntryFromExcel.getQuantity() == null) {
            throw new BadRequestException("Quantity is required for item " + itemEntryFromExcel.getItemName());
        }
        if (itemEntryFromExcel.getEntryType() == null) {
            throw new BadRequestException("Entry Type is required for item " + itemEntryFromExcel.getItemName());
        }
        if (itemEntryFromExcel.getDateOfPurchase() == null) {
            throw new BadRequestException("Date Of Purchase is required for item " + itemEntryFromExcel.getItemName());
        }

        if (itemEntryFromExcel.getSupplierName() == null) {
            throw new BadRequestException("Supplier Name is required for item " + itemEntryFromExcel.getItemName());
        }

        if (itemEntryFromExcel.getQuantity().matches(".*[a-zA-Z]+.*")) {
            throw new BadRequestException("Quantity for item " + itemEntryFromExcel.getItemName() + " contains letters. Please check the quantity and try again");
        }

        if (itemEntryFromExcel.getEntryType().equals("DIRECT PURCHASE")) {
            if (itemEntryFromExcel.getSupplierName() == null) {
                throw new BadRequestException("Direct Purchase Supplier is required for item " + itemEntryFromExcel.getItemName());
            }
            if (itemEntryFromExcel.getDirectPurchasePrice() == null) {
                throw new BadRequestException("Direct Purchase Price is required for item " + itemEntryFromExcel.getItemName());

            }
        } else if (!itemEntryFromExcel.getEntryType().equals("CONTRACT")) {
            throw new BadRequestException(" A valid Entry Type is required for item " + itemEntryFromExcel.getItemName());
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(itemEntryFromExcel.getDateOfPurchase(), formatter);
        }catch(Exception e){
            throw new BadRequestException("You entered an invalid date/date format for item " + itemEntryFromExcel.getItemName() + ". Please check the date and try again");
        }

    }





    public CreateOrUpdateItemEntryDTO transformItemEntryFromExcelToDTO(ItemEntryFromExcelDTO itemEntryFromExcel) {

        CreateOrUpdateItemEntryDTO itemEntryDTO = new CreateOrUpdateItemEntryDTO();

        Item item = itemRepository.findByName(itemEntryFromExcel.getItemName()).orElseThrow(() -> new BadRequestException("Item with name " + itemEntryFromExcel.getItemName() + " does not exist"));

        itemEntryDTO.setItemId(item.getId());

        itemEntryDTO.setQuantity(Integer.valueOf(itemEntryFromExcel.getQuantity()));

        if (itemEntryFromExcel.getEntryType().equals("DIRECT PURCHASE")) {
            itemEntryDTO.setEntryType(EEntryType.DIRECT_PURCHASE);
            itemEntryDTO.setDirectPurchaseSupplier(itemEntryFromExcel.getSupplierName());
            itemEntryDTO.setDirectPurchasePrice(Double.valueOf(itemEntryFromExcel.getDirectPurchasePrice()));
        } else if (itemEntryFromExcel.getEntryType().equals("CONTRACT")) {
            itemEntryDTO.setEntryType(EEntryType.CONTRACT);
            Supplier supplier = supplierService.findByName(itemEntryFromExcel.getSupplierName()).orElseThrow(() -> new BadRequestException("Supplier with name " + itemEntryFromExcel.getSupplierName() + " does not exist"));
            itemEntryDTO.setSupplierId(supplier.getId());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(itemEntryFromExcel.getDateOfPurchase(), formatter);
        itemEntryDTO.setDateOfPurchase(date);

        return itemEntryDTO;
    }

}