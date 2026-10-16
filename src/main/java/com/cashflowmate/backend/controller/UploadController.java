
package com.cashflowmate.backend.controller;

import com.cashflowmate.backend.dto.UploadResultResponse;
import com.cashflowmate.backend.service.SalesUploadService;
import com.cashflowmate.backend.service.SettlementUploadService;
import com.cashflowmate.backend.service.UploadResultService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final SalesUploadService salesUploadService;
    private final SettlementUploadService settlementUploadService;
    private final UploadResultService uploadResultService;

    public UploadController(
            SalesUploadService salesUploadService,
            SettlementUploadService settlementUploadService,
            UploadResultService uploadResultService
    ) {
        this.salesUploadService = salesUploadService;
        this.settlementUploadService = settlementUploadService;
        this.uploadResultService = uploadResultService;
    }

    // 매출 CSV 업로드
    @PostMapping("/sales")
    public ResponseEntity<String> uploadSales(
            @RequestParam("file") MultipartFile file
    ) {
        salesUploadService.uploadSales(file);

        return ResponseEntity.ok("매출 CSV 업로드가 완료되었습니다.");
    }

    // 정산 CSV 업로드
    @PostMapping("/settlements")
    public ResponseEntity<String> uploadSettlements(
            @RequestParam("file") MultipartFile file
    ) {
        settlementUploadService.uploadSettlements(file);

        return ResponseEntity.ok("정산 CSV 업로드가 완료되었습니다.");
    }

    // CSV 업로드 결과 조회
    @GetMapping("/{batchId}")
    public ResponseEntity<UploadResultResponse> getUploadResult(
            @PathVariable Long batchId
    ) {
        UploadResultResponse result =
                uploadResultService.getUploadResult(batchId);

        return ResponseEntity.ok(result);
    }
}
