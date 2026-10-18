package com.cashflowmate.backend.dto;

import com.cashflowmate.backend.entity.UploadError;

public record UploadErrorResponse(
        int rowNumber,
        String errorCode,
        String errorMessage,
        String rawData
) {
    public static UploadErrorResponse from(UploadError error) {
        return new UploadErrorResponse(
                error.getRowNumber(),
                error.getErrorCode(),
                error.getErrorMessage(),
                error.getRawData()
        );
    }
}