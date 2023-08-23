package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.sacco.stockmis.v1.models.ItemCategory;
import rw.gov.sacco.stockmis.v1.models.ItemRecord;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IItemRecordRepository extends JpaRepository<ItemRecord,UUID> {

    List<ItemRecord> findAllByDateOfRecordBetween(LocalDate startDate, LocalDate endDate);

    List<ItemRecord> findAllByItemCategoryAndDateOfRecordBetween(ItemCategory itemCategory, LocalDate startDate, LocalDate endDate);

    Optional<ItemRecord> findByNameAndDateOfRecord(String name, LocalDate startDate);
}
