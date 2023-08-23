package rw.gov.sacco.stockmis.v1.models;

import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import lombok.*;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;
import rw.gov.sacco.stockmis.v1.enums.EEntryType;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "item_entries")
public class ItemEntry extends InitiatorAudit {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "quantity")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type")
    private EEntryType entryType;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // Supplier as string
    @Column(name = "direct_purchase_supplier_name")
    private String directPurchaseSupplier = null;

    @Column(name = "price")
    private Double price;

    @Column(name = "date_of_purchase")
    private LocalDate dateOfPurchase;

    public ItemEntry(Item item, int quantity, EEntryType entryType, Supplier supplier, double directPurchasePrice, LocalDate dateOfPurchase) {
        this.item = item;
        this.quantity = quantity;
        this.entryType = entryType;
        this.supplier = supplier;
        this.price = directPurchasePrice;
        this.dateOfPurchase = dateOfPurchase;
    }

    public ItemEntry(EEntryType entryType, Item item, Integer quantity, String directPurchaseSupplier, Double directPurchasePrice, LocalDate dateOfPurchase) {
        this.entryType = entryType;
        this.item = item;
        this.quantity = quantity;
        this.directPurchaseSupplier = directPurchaseSupplier;
        this.price = directPurchasePrice;
        this.dateOfPurchase = dateOfPurchase;
    }

    public ItemEntry(EEntryType entryType, Item item, Integer quantity, Supplier supplier, Double directPurchasePrice, LocalDate dateOfPurchase) {
        this.entryType = entryType;
        this.item = item;
        this.quantity = quantity;
        this.supplier = supplier;
        this.price = directPurchasePrice;
        this.dateOfPurchase = dateOfPurchase;
    }

    public ItemEntry(ItemEntry itemEntry) {
        this.item = itemEntry.getItem();
        this.quantity = itemEntry.getQuantity();
        this.entryType = itemEntry.getEntryType();
        this.supplier = itemEntry.getSupplier();
        this.price = itemEntry.getPrice();
        this.dateOfPurchase = itemEntry.getDateOfPurchase();
    }
}

