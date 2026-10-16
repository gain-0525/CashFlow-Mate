
package com.cashflowmate.backend.repository;

import com.cashflowmate.backend.entity.UploadError;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadErrorRepository
        extends JpaRepository<UploadError, Long> {

    List<UploadError> findByUploadBatchId(Long uploadBatchId);

}
