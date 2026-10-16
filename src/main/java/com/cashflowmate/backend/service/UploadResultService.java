
package com.cashflowmate.backend.service;

import com.cashflowmate.backend.dto.UploadResultResponse;
import com.cashflowmate.backend.entity.UploadBatch;
import com.cashflowmate.backend.entity.UploadError;
import com.cashflowmate.backend.repository.UploadBatchRepository;
import com.cashflowmate.backend.repository.UploadErrorRepository;

import org.springframework.stereotype.Service;

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

        // 1. 업로드 이력 조회
        UploadBatch batch = uploadBatchRepository.findById(batchId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 업로드 번호입니다: " + batchId
                        )
                );

        // 2. 해당 업로드의 오류 목록 조회
        List<UploadError> uploadErrors =
                uploadErrorRepository.findByUploadBatchId(batchId);

        // 3. 오류 데이터를 DTO로 변환
        List<UploadResultResponse.UploadErrorDetail> errors =
                uploadErrors.stream()
                        .map(error -> new UploadResultResponse.UploadErrorDetail(
                                error.getRowNumber(),
                                error.getErrorCode(),
                                error.getErrorMessage()
                        ))
                        .toList();

        // 4. 최종 응답 DTO 생성
        return new UploadResultResponse(
                batch.getId(),
                batch.getFileName(),
                batch.getFileType(),
                batch.getTotalRows(),
                batch.getSuccessRows(),
                batch.getErrorRows(),
                errors
        );
    }
}
