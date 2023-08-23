package rw.gov.sacco.stockmis.v1.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.gov.sacco.stockmis.v1.enums.EStockStatus;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemDTO;
import rw.gov.sacco.stockmis.v1.dtos.ReportRecordDTO;
import rw.gov.sacco.stockmis.v1.models.Item;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface IItemService {
    long count();
    Item create(CreateOrUpdateItemDTO itemDTO);

    Item findById(UUID id);

    boolean existsById(UUID id);

    Iterable<Item> findAll();

    Page<Item> findAll(UUID categoryId, Pageable pageable);

    Page<Item> findAll(UUID categoryId, EStockStatus status, String name, Pageable pageable);

    Page<Item> findAllActive(Pageable pageable);

    Item update(UUID id, CreateOrUpdateItemDTO itemDTO, String reason);

    boolean delete(UUID id, String reason);

    //Increment nbr of items available
    Item incrementNbrOfItemsAvailable(UUID id, int nbrOfItemsToIncrement);

    //Decrement nbr of items available
    Item decrementNbrOfItemsAvailable(UUID id, int nbrOfItemsToDecrement);

    //cron job to save item record
    void saveItemRecordStartMonth();

    void saveItemRecordEndMonth();

    List<ReportRecordDTO> getMonthlyReports(String categoryName, String startMonth, String endMonth, int year);

    boolean isValidMonth(String startMonth);

    int getLastDayOfMonth(String endMonth);

    Boolean forceSaveItemRecord(LocalDate date);

    void createManyItems(String jsonData, String itemCategoryName);
}

