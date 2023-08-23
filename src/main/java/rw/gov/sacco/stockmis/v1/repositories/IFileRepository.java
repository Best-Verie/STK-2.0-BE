package rw.gov.sacco.stockmis.v1.repositories;

import rw.gov.sacco.stockmis.v1.enums.EFileStatus;
import rw.gov.sacco.stockmis.v1.fileHandling.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IFileRepository extends JpaRepository<File, UUID> {
    Page<File> findAllByStatus(Pageable pageable, EFileStatus status);

    List<File> findByPathContainsAndNameContains(String path, String name);

}
