package rw.gov.sacco.stockmis.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Service;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "atomic_po_requests")
@Service
public class AtomicPORequest extends InitiatorAudit {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "direct_purchase_item")
    private String directPurchaseItem = null;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private Double price;

    @Column(name = "description")
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "po_request_id")
    private PORequest poRequest;

    public AtomicPORequest(Item item, int quantity, String description, PORequest poRequest, Double price) {
        this.item = item;
        this.quantity = quantity;
        this.description = description;
        this.poRequest = poRequest;
        this.price = price;
    }

    public AtomicPORequest(String directPurchaseItem, int quantity, String description, PORequest poRequest, Double price) {
        this.directPurchaseItem = directPurchaseItem;
        this.quantity = quantity;
        this.description = description;
        this.poRequest = poRequest;
        this.price = price;
    }
}
