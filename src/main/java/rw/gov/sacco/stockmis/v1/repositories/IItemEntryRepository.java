package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.sacco.stockmis.v1.models.Item;
import rw.gov.sacco.stockmis.v1.models.ItemEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IItemEntryRepository extends JpaRepository<ItemEntry, UUID> {

    List<ItemEntry> findAllByItemAndDateOfPurchaseBetween(Item item, LocalDate startDate, LocalDate endDate);
}
