package rw.gov.sacco.stockmis.v1.services;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

public interface IItemPriceTraceService {
        void generatePriceReport(UUID itemId, LocalDate startDate, LocalDate endDate) throws IOException;
}
