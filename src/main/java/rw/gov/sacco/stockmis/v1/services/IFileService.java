package rw.gov.sacco.stockmis.v1.services;

import rw.gov.sacco.stockmis.v1.enums.EFileStatus;
import rw.gov.sacco.stockmis.v1.exceptions.InvalidFileException;
import rw.gov.sacco.stockmis.v1.fileHandling.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface IFileService {

        public List<File> getAll();

        public Page<File> getAll(Pageable pageable);

        public File getById(UUID id);

        public File create(MultipartFile document, String directory);

        public boolean delete(UUID id);

        public Page<File> getAllByStatus(Pageable pageable, EFileStatus status);

        public File uploadFile(MultipartFile file, String directory, UUID appointeeID) throws InvalidFileException, IOException;

        public String getFileExtension(String fileName);

        public String handleFileName(String fileName, UUID id) throws InvalidFileException;

        public boolean isValidExtension(String fileName) throws InvalidFileException;
}
