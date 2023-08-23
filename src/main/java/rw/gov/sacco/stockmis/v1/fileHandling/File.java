package rw.gov.sacco.stockmis.v1.fileHandling;


import rw.gov.sacco.stockmis.v1.audits.InitiatorAudit;
import rw.gov.sacco.stockmis.v1.enums.EFileSizeType;
import rw.gov.sacco.stockmis.v1.enums.EFileStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "files", uniqueConstraints = {@UniqueConstraint(columnNames = "path")})
public class File extends InitiatorAudit {
    @Id
    @GeneratedValue(generator = "fileUUID")
    @GenericGenerator(name = "fileUUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @Transient
    private String url;

    @Column(name = "size")
    private int size;

    @Column(name = "size_type")
    @Enumerated(EnumType.STRING)
    private EFileSizeType sizeType;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EFileStatus status;

    public File(String directory, String fileName, String extension, String fileBaseName) {
        super();
    }

    public String getUrl() {
        return "/files/load-file/" + name;
    }
}

