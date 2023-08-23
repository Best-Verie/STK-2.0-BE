package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.sacco.stockmis.v1.models.ItemTrace;

import java.util.UUID;

public interface IItemTraceRepository  extends JpaRepository<ItemTrace, UUID> {

}
