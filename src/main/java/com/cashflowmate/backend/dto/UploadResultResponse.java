
package com.cashflowmate.backend.dto;

import java.util.List;

public record UploadResultResponse(
        Long batchId,
        String fileName,
        String fileType,
        int totalRows,
        int successRows,
        int errorRows,
        List<UploadErrorDetail> errors
) {
    public record UploadErrorDetail(
            int rowNumber,
            String errorCode,
            String errorMessage
    ) {
    }
}
