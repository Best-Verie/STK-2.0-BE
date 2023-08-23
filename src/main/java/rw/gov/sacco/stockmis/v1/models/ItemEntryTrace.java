package rw.gov.sacco.stockmis.v1.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemEntryDTO;
import rw.gov.sacco.stockmis.v1.enums.EEntryType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "item_entries_trace")
public class ItemEntryTrace extends InitiatorAudit {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "updated_item_entry_id")
    private ItemEntry itemEntry;

    @ManyToOne
    @JoinColumn(name = "old_item_id")
    private Item oldItem;

    @ManyToOne
    @JoinColumn(name = "new_item_id")
    private Item newItem;

    @Column(name = "old_quantity")
    private Integer oldQuantity;

    @Column(name = "new_quantity")
    private Integer newQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_entry_type")
    private EEntryType oldEntryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_entry_type")
    private EEntryType newEntryType;

    @ManyToOne
    @JoinColumn(name = "old_supplier_id")
    private Supplier oldSupplier;

    @ManyToOne
    @JoinColumn(name = "new_supplier_id")
    private Supplier newSupplier;

    // Supplier as string
    @Column(name = "old_direct_purchase_supplier_name")
    private String oldDirectPurchaseSupplier = null;

    @Column(name = "new_direct_purchase_supplier_name")
    private String newDirectPurchaseSupplier = null;

    @Column(name = "old_price")
    private Double oldPrice;

    @Column(name = "new_price")
    private Double newPrice;

    @Column(name = "old_date_of_purchase")
    private LocalDate oldDateOfPurchase;

    @Column(name = "new_date_of_purchase")
    private LocalDate newDateOfPurchase;

    @Column(name = "reason")
    private String reason;

    public ItemEntryTrace(ItemEntry itemEntry, Item newItem, Supplier newSupplier, Double newPrice, CreateOrUpdateItemEntryDTO dto, String reason) {
        this.itemEntry = itemEntry;
        this.oldItem = itemEntry.getItem();
        this.newItem = newItem;
        this.oldQuantity = itemEntry.getQuantity();
        this.newQuantity = dto.getQuantity();
        this.oldEntryType = itemEntry.getEntryType();
        this.newEntryType = dto.getEntryType();
        this.oldSupplier = itemEntry.getSupplier();
        this.newSupplier = newSupplier;
        this.oldDirectPurchaseSupplier = itemEntry.getDirectPurchaseSupplier();
        this.newDirectPurchaseSupplier = dto.getDirectPurchaseSupplier();
        this.oldPrice = itemEntry.getPrice();
        this.newPrice = newPrice;
        this.oldDateOfPurchase = itemEntry.getDateOfPurchase();
        this.newDateOfPurchase = dto.getDateOfPurchase();
        this.reason = reason;
    }
}
