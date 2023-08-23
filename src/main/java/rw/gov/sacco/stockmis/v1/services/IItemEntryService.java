package rw.gov.sacco.stockmis.v1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.gov.sacco.stockmis.v1.dtos.CreateManyItemEntriesFromExcelDTO;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemEntryDTO;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.models.ItemEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IItemEntryService {

    ItemEntry create(CreateOrUpdateItemEntryDTO itemEntryDTO);

    //create many item entries from excel
    Boolean createManyItemEntriesFromExcel(CreateManyItemEntriesFromExcelDTO itemEntryDTO);

    ItemEntry findById(UUID id);

    boolean existsById(UUID id);

    Iterable<ItemEntry> findAll();

    Page<ItemEntry> findAll(Pageable pageable);

    ItemEntry update(UUID id, CreateOrUpdateItemEntryDTO itemEntryDTO, String reason);

    boolean delete(UUID id);

    List<ItemEntry> findAllByItemAndDateOfPurchaseBetween(Item item, LocalDate startDate, LocalDate endDate);
}

