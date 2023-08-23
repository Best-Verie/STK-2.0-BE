package rw.gov.sacco.stockmis.v1.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;
import rw.gov.sacco.stockmis.v1.enums.EItemType;
import rw.gov.sacco.stockmis.v1.enums.EStockStatus;

import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item extends InitiatorAudit {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id")
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = true)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type")
    private EItemType itemType = EItemType.STOCK_ITEM;

    @Transient
    private Integer sharableQuantity = null;


    @Column(name = "item_code", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemCode;


    public Item(String name, ItemCategory category, double price, int nbrOfItemsAvailable, int overstockParameter, int understockParameter, EItemType itemType, EStockStatus stockStatus) {
        this.name = name;
        this.itemCategory = category;
        this.price = price;
        this.nbrOfItemsAvailable = nbrOfItemsAvailable;
        this.overstockParameter = overstockParameter;
        this.understockParameter = understockParameter;
        this.stockStatus = stockStatus;
        this.itemType = itemType;

    }

    public Item(String name, ItemCategory itemCategory, double price, int nbrOfItemsAvailable, int overstockParameter, int understockParameter, int inStockDuration, EStockStatus status) {
        this.name = name;
        this.itemCategory = itemCategory;
        this.price = price;
        this.nbrOfItemsAvailable = nbrOfItemsAvailable;
        this.overstockParameter = overstockParameter;
        this.understockParameter = understockParameter;
        this.inStockDuration = inStockDuration;
        this.stockStatus = status;

    }

    public Item(String name, ItemCategory itemCategory, double price, int i, int overstockParameter, int understockParameter, int inStockDuration, EStockStatus eStockStatus, EItemType itemType) {
        this.name = name;
        this.itemCategory = itemCategory;
        this.price = price;
        this.nbrOfItemsAvailable = i;
        this.overstockParameter = overstockParameter;
        this.understockParameter=understockParameter;
        this.inStockDuration = inStockDuration;
        this.stockStatus=eStockStatus;
        this.itemType = itemType;
    }
}
