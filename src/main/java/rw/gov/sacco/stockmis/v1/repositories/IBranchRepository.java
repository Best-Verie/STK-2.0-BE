package rw.gov.sacco.stockmis.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.sacco.stockmis.v1.models.Branch;

import java.util.Optional;

public interface IBranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByName(String branchName);
}

