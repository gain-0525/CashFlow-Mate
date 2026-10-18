
package com.cashflowmate.backend.controller;

import com.cashflowmate.backend.dto.UploadResultResponse;
import com.cashflowmate.backend.service.SalesUploadService;
import com.cashflowmate.backend.service.SettlementUploadService;
import com.cashflowmate.backend.service.UploadResultService;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.util.Map;

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
    @PostMapping(value = "/sales", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Long>> uploadSales(
            @RequestParam("file") MultipartFile file
    ) {
        Long batchId = salesUploadService.uploadSales(file);
        return ResponseEntity.ok(Map.of("batchId", batchId));
    }

    // 정산 CSV 업로드
    @PostMapping(value = "/settlements", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Long>> uploadSettlements(
            @RequestParam("file") MultipartFile file
    ) {
        Long batchId = settlementUploadService.uploadSettlements(file);
        return ResponseEntity.ok(Map.of("batchId", batchId));
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
