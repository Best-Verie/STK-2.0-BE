package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.sacco.stockmis.v1.models.ItemEntryTrace;

import java.util.UUID;

public interface IItemEntryTraceRepository extends JpaRepository<ItemEntryTrace, UUID> {

}
