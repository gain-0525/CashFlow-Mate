
package com.cashflowmate.backend.repository;

import com.cashflowmate.backend.entity.UploadBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadBatchRepository
        extends JpaRepository<UploadBatch, Long> {
}
