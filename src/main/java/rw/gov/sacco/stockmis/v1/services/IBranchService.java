package rw.gov.sacco.stockmis.v1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.gov.sacco.stockmis.v1.dtos.BranchesDataEntryDTO;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateBranchDTO;
import rw.gov.sacco.stockmis.v1.models.Branch;


public interface IBranchService {

    Branch create(CreateOrUpdateBranchDTO branchDTO);

    void createManyBranches(BranchesDataEntryDTO branchesData);

    Branch findById(Long id);

    boolean existsById(Long id);

    Iterable<Branch> findAll();

    Page<Branch> findAll(Pageable pageable);


    Branch update(Long id, CreateOrUpdateBranchDTO branchDTO);

    boolean delete(Long id);
}
