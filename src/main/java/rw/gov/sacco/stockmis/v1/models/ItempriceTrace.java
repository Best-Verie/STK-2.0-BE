package rw.gov.sacco.stockmis.v1.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "items_price_trace")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItempriceTrace extends InitiatorAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id")
    private UUID itemId;

    private String activity;


    private Integer quantity;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "stockvalue")
    private Double stockValue;

    private LocalDate date;

}
