package rw.gov.sacco.stockmis.v1.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;

import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import rw.gov.sacco.stockmis.v1.enums.ERequestStatus;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request extends InitiatorAudit {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ERequestStatus status;

    @Column(name = "rejection_comment")
    private String rejectionComment;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "available_quantity")
    private int availableQuantity;

    @Column(name = "suggested_quantity")
    private int suggestedQuantity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_status_change_approver", nullable = true)
    private LocalDate dateOfStatusChangeApprover = null;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_status_change_store_keeper", nullable = true)
    private LocalDate dateOfStatusChangeStoreKeeper = null;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_status_change_granted", nullable = true)
    private LocalDate dateOfStatusChangeStoreKeeperGranted = null;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_status_change_hohr", nullable = true)
    private LocalDate dateOfStatusChangeHoHR = null;

    @ManyToOne
    @JoinColumn(name = "approver_id", nullable = true)
    private User approver = null;

    @Transient
    private User creator;
    @ManyToOne
    @JoinColumn(name = "store_keeper_id", nullable = true)
    private User storeKeeper = null;

    @ManyToOne
    @JoinColumn(name = "store_keeper_granter_id", nullable = true)
    private User storeKeeperWhoGranted = null;

    @ManyToOne
    @JoinColumn(name = "hohr_id", nullable = true)
    private User hoHR = null;

    //constructor that accepts status, item and quantity

    public Request(ERequestStatus status, Item item, int quantity, int availableQuantity) {
        this.status = status;
        this.item = item;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
    }

    public Request(ERequestStatus status, String rejectionComment, Item item, int quantity, LocalDate dateOfStatusChangeApprover, LocalDate dateOfStatusChangeStoreKeeper, LocalDate dateOfStatusChangeHoHR) {
        this.status = status;
        this.rejectionComment = rejectionComment;
        this.item = item;
        this.quantity = quantity;
        this.dateOfStatusChangeApprover = dateOfStatusChangeApprover;
        this.dateOfStatusChangeStoreKeeper = dateOfStatusChangeStoreKeeper;
        this.dateOfStatusChangeHoHR = dateOfStatusChangeHoHR;
    }
}


