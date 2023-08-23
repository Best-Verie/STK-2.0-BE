package rw.gov.sacco.stockmis.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;

import javax.persistence.*;
import java.util.Set;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "branches")
public class Branch extends InitiatorAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    private Set<User> users;

}
