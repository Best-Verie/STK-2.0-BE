package rw.gov.sacco.stockmis.v1.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;

import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "suppliers")
public class Supplier extends InitiatorAudit {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id")
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = true)
    private String name;

    @NotBlank
    @Column(name = "address")
    private String address;

    @NotBlank
    @Column(name = "email")
    private String email;

    @NotBlank
    @Column(name = "phone")
    private String phone;

    @NotBlank
    @Column(name = "tin_number")
    private String tinNumber;


    @Column(name = "delivery_terms")
    private Integer deliveryTerms = 0;

    @Column(name = "warranty_period")
    private Integer warrantyPeriod = 0;


    @ManyToMany
    @JoinTable(
            name = "supplier_items",
            joinColumns = @JoinColumn(name = "supplier_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> itemsSupplied;

    public Supplier(String name, String address, String email, String phone, String tinNumber) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.tinNumber = tinNumber;
    }

}

