
package com.cashflowmate.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "upload_batches")
public class UploadBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "total_rows", nullable = false)
    private int totalRows;

    @Column(name = "success_rows", nullable = false)
    private int successRows;

    @Column(name = "error_rows", nullable = false)
    private int errorRows;

    protected UploadBatch() {
    }

    public UploadBatch(String fileName, String fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.uploadedAt = LocalDateTime.now();
        this.totalRows = 0;
        this.successRows = 0;
        this.errorRows = 0;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getSuccessRows() {
        return successRows;
    }

    public int getErrorRows() {
        return errorRows;
    }

    public void updateCounts(int totalRows, int successRows, int errorRows) {
        this.totalRows = totalRows;
        this.successRows = successRows;
        this.errorRows = errorRows;
    }
}
