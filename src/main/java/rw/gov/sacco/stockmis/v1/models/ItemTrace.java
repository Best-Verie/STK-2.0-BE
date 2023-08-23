package rw.gov.sacco.stockmis.v1.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemDTO;
import rw.gov.sacco.stockmis.v1.enums.EStockStatus;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "items_trace")
public class ItemTrace extends InitiatorAudit {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "updated_item_id")
    private Item updatedItem;
    @Column(name = "oldName")
    private String oldName;
    @Column(name = "newName")
    private String newName;
    @ManyToOne
    @JoinColumn(name = "old_category_id")
    private ItemCategory oldItemCategory;

    @ManyToOne
    @JoinColumn(name = "new_category_id")
    private ItemCategory newItemCategory;
    @Column(name = "old_price")
    private Double oldPrice;

    @Column(name = "new_price")
    private Double newPrice;

    @Column(name = "old_overstock_parameter")
    private Integer oldOverstockParameter;

    @Column(name = "new_overstock_parameter")
    private Integer newOverstockParameter;

    @Column(name = "old_understock_parameter")
    private Integer oldUnderstockParameter;

    @Column(name = "new_understock_parameter")
    private Integer newUnderstockParameter;

    @Column(name = "old_instock_duration")
    private Integer oldInStockDuration;

    @Column(name = "new_instock_duration")
    private Integer newInStockDuration;

    @Column(name = "reason")
    private String reason;

    //Stock status
    @Enumerated(EnumType.STRING)
    @Column(name = "old_stock_status")
    private EStockStatus oldStockStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_stock_status")
    private EStockStatus newStockStatus;


    //constructor
    public ItemTrace(Item item, CreateOrUpdateItemDTO dto, ItemCategory newItemCategory, EStockStatus newStockStatus, String reason) {
        this.updatedItem = item;
        this.oldName = item.getName();
        this.newName = dto.getName();
        this.oldItemCategory = item.getItemCategory();
        this.newItemCategory = newItemCategory;
        this.oldPrice = item.getPrice();
        this.newPrice = dto.getPrice();
        this.oldOverstockParameter = item.getOverstockParameter();
        this.newOverstockParameter = dto.getOverstockParameter();
        this.oldUnderstockParameter = item.getUnderstockParameter();
        this.newUnderstockParameter = dto.getUnderstockParameter();
        this.oldInStockDuration = item.getInStockDuration();
        this.newInStockDuration = dto.getInStockDuration();
        this.reason = reason;
        this.oldStockStatus = item.getStockStatus();
        this.newStockStatus = newStockStatus;
    }

    public ItemTrace(Item item, EStockStatus newStockStatus, String reason) {
        this.updatedItem = item;
        this.oldName = item.getName();
        this.newName = null;
        this.oldItemCategory = item.getItemCategory();
        this.newItemCategory = null;
        this.oldPrice = item.getPrice();
        this.newPrice = null;
        this.oldOverstockParameter = item.getOverstockParameter();
        this.newOverstockParameter = null;
        this.oldUnderstockParameter = item.getUnderstockParameter();
        this.newUnderstockParameter = null;
        this.oldInStockDuration = item.getInStockDuration();
        this.newInStockDuration = null;
        this.reason = reason;
        this.oldStockStatus = item.getStockStatus();
        this.newStockStatus = newStockStatus;
    }

}
