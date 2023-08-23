package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.sacco.stockmis.v1.models.ItempriceTrace;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IItemPriceTraceRepository extends JpaRepository<ItempriceTrace, Long> {
    List<ItempriceTrace> findByItemIdAndDateBetween(UUID itemId, LocalDate startDate, LocalDate endDate);
    List<ItempriceTrace> findByItemIdAndDateBetweenOrderByIdAsc(UUID itemId, LocalDate startDate, LocalDate endDate);

}
