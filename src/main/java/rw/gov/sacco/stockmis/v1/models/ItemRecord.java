package rw.gov.sacco.stockmis.v1.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;
import rw.gov.sacco.stockmis.v1.enums.EStockStatus;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "item_records")
public class ItemRecord extends InitiatorAudit {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id")
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ItemCategory itemCategory;

    @Column(name = "price")
    private double price;

    @Column(name = "nbr_of_items_available")
    private int nbrOfItemsAvailable;

    @Column(name = "overstock_parameter")
    private int overstockParameter;

    @Column(name = "understock_parameter")
    private int understockParameter;

    @Column(name = "instock_duration")
    private int inStockDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_status")
    private EStockStatus stockStatus = EStockStatus.OUT_OF_STOCK;

    //date of record
    @Column(name = "date_of_record")
    private LocalDate dateOfRecord;

    public ItemRecord(String name, ItemCategory category, double price, int nbrOfItemsAvailable, int overstockParameter, int understockParameter, EStockStatus stockStatus) {
        this.name = name;
        this.itemCategory = category;
        this.price = price;
        this.nbrOfItemsAvailable = nbrOfItemsAvailable;
        this.overstockParameter = overstockParameter;
        this.understockParameter = understockParameter;
        this.stockStatus = stockStatus;
    }

    public ItemRecord(String name, ItemCategory itemCategory, double price, int nbrOfItemsAvailable, int overstockParameter, int understockParameter, int inStockDuration, EStockStatus status) {
        this.name = name;
        this.itemCategory = itemCategory;
        this.price = price;
        this.nbrOfItemsAvailable = nbrOfItemsAvailable;
        this.overstockParameter = overstockParameter;
        this.understockParameter = understockParameter;
        this.inStockDuration = inStockDuration;
        this.stockStatus = status;
    }

    public ItemRecord(Item item) {
        this.name = item.getName();
        this.itemCategory = item.getItemCategory();
        this.price = item.getPrice();
        this.nbrOfItemsAvailable = item.getNbrOfItemsAvailable();
        this.overstockParameter = item.getOverstockParameter();
        this.understockParameter = item.getUnderstockParameter();
        this.inStockDuration = item.getInStockDuration();
        this.stockStatus = item.getStockStatus();
        this.dateOfRecord = LocalDate.now();
    }

    public ItemRecord(Item item, LocalDate dateOfRecord) {
        this.name = item.getName();
        this.itemCategory = item.getItemCategory();
        this.price = item.getPrice();
        this.nbrOfItemsAvailable = item.getNbrOfItemsAvailable();
        this.overstockParameter = item.getOverstockParameter();
        this.understockParameter = item.getUnderstockParameter();
        this.inStockDuration = item.getInStockDuration();
        this.stockStatus = item.getStockStatus();
        this.dateOfRecord = dateOfRecord;
    }
}


