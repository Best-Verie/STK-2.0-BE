package rw.gov.sacco.stockmis.v1.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;
import rw.gov.sacco.stockmis.v1.enums.EEntryType;
import rw.gov.sacco.stockmis.v1.enums.EPORequestStatus;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "po_requests")
public class PORequest extends InitiatorAudit {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id")
    private UUID id;

    @Column(name = "po_number", nullable = false)
    private Long poNumber;

    //list of atomic po requests
    @OneToMany(mappedBy = "poRequest", cascade = CascadeType.ALL)
    @Column(name = "atomic_po_requests")
    List<AtomicPORequest> atomicPORequests;

    //status of the po request
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EPORequestStatus status = EPORequestStatus.PENDING;

    //comment on the po request
    @Column(name = "rejection_comment")
    private String rejectionComment = "";

    //date of HoHR status change
    @Column(name = "date_of_status_change_hohr", nullable = true)
    private LocalDate dateOfStatusChangeHoHR = null;

    //date of Approver status change
    @Column(name = "date_of_status_change_approver", nullable = true)
    private LocalDate dateOfStatusChangeApprover = null;

    @Column(name = "date_of_status_change_director_general", nullable = true)
    private LocalDate dateOfStatusChangeDirectorGeneral = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type")
    private EEntryType entryType;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "direct_purchase_supplier_name")
    private String directPurchaseSupplier = null;

    //HoHR who changed the status
    @ManyToOne
    @JoinColumn(name = "hohr_id")
    private User procManager = null;

    //Approver who changed the status

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private User approver = null;




    //Director General who changed the status

    @ManyToOne
    @JoinColumn(name = "director_general_id")
    private User directorGeneral = null;
}
