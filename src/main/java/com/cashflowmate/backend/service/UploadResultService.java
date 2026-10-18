
package com.cashflowmate.backend.service;

import com.cashflowmate.backend.dto.UploadResultResponse;
import com.cashflowmate.backend.entity.UploadBatch;
import com.cashflowmate.backend.entity.UploadError;
import com.cashflowmate.backend.repository.UploadBatchRepository;
import com.cashflowmate.backend.repository.UploadErrorRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UploadResultService {

    private final UploadBatchRepository uploadBatchRepository;
    private final UploadErrorRepository uploadErrorRepository;

    public UploadResultService(
            UploadBatchRepository uploadBatchRepository,
            UploadErrorRepository uploadErrorRepository
    ) {
        this.uploadBatchRepository = uploadBatchRepository;
        this.uploadErrorRepository = uploadErrorRepository;
    }

    public UploadResultResponse getUploadResult(Long batchId) {

        UploadBatch batch = uploadBatchRepository.findById(batchId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "업로드 기록을 찾을 수 없습니다."
                ));

        List<UploadError> errors =
                uploadErrorRepository.findByUploadBatchId(batchId);

        return UploadResultResponse.from(batch, errors);
    }
}
