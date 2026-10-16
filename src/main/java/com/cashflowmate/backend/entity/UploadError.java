
package com.cashflowmate.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "upload_errors")
public class UploadError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "upload_batch_id", nullable = false)
    private Long uploadBatchId;

    @Column(name = "`row_number`", nullable = false)
    private int rowNumber;

    @Column(name = "error_code", nullable = false)
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "raw_data", columnDefinition = "json")
    private String rawData;

    protected UploadError() {
    }

    public UploadError(
            Long uploadBatchId,
            int rowNumber,
            String errorCode,
            String errorMessage,
            String rawData
    ) {
        this.uploadBatchId = uploadBatchId;
        this.rowNumber = rowNumber;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.rawData = rawData;
    }

    public Long getId() {
        return id;
    }

    public Long getUploadBatchId() {
        return uploadBatchId;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getRawData() {
        return rawData;
    }
}
