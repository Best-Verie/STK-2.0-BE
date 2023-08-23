package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rw.gov.sacco.stockmis.v1.dtos.BranchesDataEntryDTO;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateBranchDTO;
import rw.gov.sacco.stockmis.v1.models.Branch;
import rw.gov.sacco.stockmis.v1.repositories.IBranchRepository;
import rw.gov.sacco.stockmis.v1.services.IBranchService;

import java.util.Map;

@Service
public class BranchServiceImpl implements IBranchService {
    private final IBranchRepository branchRepository;

    @Autowired
    public BranchServiceImpl(IBranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public Branch create(CreateOrUpdateBranchDTO branchDTO) {
        Branch branch = new Branch();
        branch.setName(branchDTO.getName());
        branch.setAddress(branchDTO.getAddress());
        return branchRepository.save(branch);
    }

    @Override
    public void createManyBranches(BranchesDataEntryDTO branchesData) {
        for (Map<String, String> branchData : branchesData.getData()) {
            CreateOrUpdateBranchDTO dto = new CreateOrUpdateBranchDTO();
            dto.setName(branchData.get("name"));
            dto.setAddress("Rwanda");
            create(dto);
        }
    }

    @Override
    public Branch findById(Long id) {
        return branchRepository.findById(id).orElse(null);
    }

    @Override
    public boolean existsById(Long id) {
        return branchRepository.existsById(id);
    }

    @Override
    public Iterable<Branch> findAll() {
        return branchRepository.findAll();
    }


    @Override
    public Page<Branch> findAll(Pageable pageable) {
        return branchRepository.findAll(pageable);
    }

    @Override
    public Branch update(Long id, CreateOrUpdateBranchDTO branchDTO) {
        Branch branch = findById(id);
        if (branch == null) {
            return null;
        }
        branch.setName(branchDTO.getName());
        branch.setAddress(branchDTO.getAddress());
        return branchRepository.save(branch);
    }

    @Override
    public boolean delete(Long id) {
        if (!existsById(id)) {
            return false;
        }
        branchRepository.deleteById(id);
        return true;
    }
}
