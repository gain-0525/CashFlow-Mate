package com.cashflowmate.backend.dto;

import com.cashflowmate.backend.entity.UploadBatch;
import java.time.LocalDateTime;
import java.util.List;
import com.cashflowmate.backend.entity.UploadError;

public record UploadResultResponse(
        Long batchId,
        String fileName,
        String fileType,
        LocalDateTime uploadedAt,
        int totalRows,
        int successRows,
        int errorRows,
        List<UploadErrorResponse> errors
) {
    public static UploadResultResponse from(
            UploadBatch batch,
            List<UploadError> errors
    ) {
        return new UploadResultResponse(
                batch.getId(),
                batch.getFileName(),
                batch.getFileType(),
                batch.getUploadedAt(),
                batch.getTotalRows(),
                batch.getSuccessRows(),
                batch.getErrorRows(),
                errors.stream()
                        .map(UploadErrorResponse::from)
                        .toList()
        );
    }
}