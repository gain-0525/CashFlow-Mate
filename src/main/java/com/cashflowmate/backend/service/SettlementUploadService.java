
package com.cashflowmate.backend.service;

import org.springframework.transaction.annotation.Transactional;
import com.cashflowmate.backend.entity.Sales;
import com.cashflowmate.backend.entity.Settlement;
import com.cashflowmate.backend.entity.UploadBatch;
import com.cashflowmate.backend.entity.UploadError;

import com.cashflowmate.backend.repository.SalesRepository;
import com.cashflowmate.backend.repository.SettlementRepository;
import com.cashflowmate.backend.repository.UploadBatchRepository;
import com.cashflowmate.backend.repository.UploadErrorRepository;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class SettlementUploadService {

    private final SalesRepository salesRepository;
    private final SettlementRepository settlementRepository;
    private final UploadBatchRepository uploadBatchRepository;
    private final UploadErrorRepository uploadErrorRepository;

    private final ObjectMapper objectMapper;

    public SettlementUploadService(
            SalesRepository salesRepository,
            SettlementRepository settlementRepository,
            UploadBatchRepository uploadBatchRepository,
            UploadErrorRepository uploadErrorRepository,
            ObjectMapper objectMapper
    ) {
        this.salesRepository = salesRepository;
        this.settlementRepository = settlementRepository;
        this.uploadBatchRepository = uploadBatchRepository;
        this.uploadErrorRepository = uploadErrorRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void uploadSettlements(MultipartFile file) {

        // 1. 빈 파일 확인
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // 2. 업로드 이력 생성
        UploadBatch batch = new UploadBatch(
                file.getOriginalFilename(),
                "SETTLEMENTS"
        );

        uploadBatchRepository.save(batch);

        int totalRows = 0;
        int successRows = 0;
        int errorRows = 0;

        // 3. CSV 파일 읽기
        try (
                Reader reader = new InputStreamReader(
                        file.getInputStream(),
                        StandardCharsets.UTF_8
                );

                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .get()
                        .parse(reader)
        ) {

            // 4. CSV 데이터 한 행씩 처리
            for (CSVRecord record : parser) {

                totalRows++;

                try {
                    // 거래번호
                    String transactionId =
                            record.get("transaction_id").trim();

                    // 거래번호가 비어 있는지 확인
                    if (transactionId.isEmpty()) {
                        throw new IllegalArgumentException(
                                "거래번호가 비어 있습니다."
                        );
                    }

                    // 5. 중복 거래번호 검사
                    if (settlementRepository.existsByTransactionId(
                            transactionId
                    )) {

                        errorRows++;

                        saveUploadError(
                                batch.getId(),
                                record,
                                "DUPLICATE_TRANSACTION",
                                "이미 존재하는 정산 거래번호입니다."
                        );

                        continue;
                    }

                    // 6. CSV 데이터 변환
                    LocalDate settlementDate = LocalDate.parse(
                            record.get("settlement_date").trim()
                    );

                    BigDecimal grossAmount = new BigDecimal(
                            record.get("gross_amount").trim()
                    );

                    BigDecimal feeAmount = new BigDecimal(
                            record.get("fee_amount").trim()
                    );

                    BigDecimal netAmount = new BigDecimal(
                            record.get("net_amount").trim()
                    );

                    String status =
                            record.get("status").trim();

                    // 7. 매출 데이터 조회
                    Optional<Sales> saleOptional =
                            salesRepository.findByTransactionId(
                                    transactionId
                            );

                    Long saleId = null;
                    String validationStatus;

                    // 8. 정산 데이터 검증
                    if (saleOptional.isEmpty()) {

                        validationStatus = "MISSING_SALE";

                    } else {

                        Sales sale = saleOptional.get();
                        saleId = sale.getId();

                        // 매출 원금 - 수수료 = 실제 입금액
                        if (grossAmount.subtract(feeAmount)
                                .compareTo(netAmount) != 0) {

                            validationStatus = "INVALID_DATA";

                        } else if (sale.getAmount()
                                .compareTo(grossAmount) != 0) {

                            validationStatus = "AMOUNT_MISMATCH";

                        } else {

                            validationStatus = "MATCHED";
                        }
                    }

                    // 9. 정산 객체 생성
                    Settlement settlement = new Settlement(
                            transactionId,
                            saleId,
                            settlementDate,
                            grossAmount,
                            feeAmount,
                            netAmount,
                            status,
                            validationStatus,
                            batch.getId()
                    );

                    // 10. 정산 데이터 저장
                    settlementRepository.save(settlement);
                    successRows++;

                    System.out.println(
                            "정산 저장 성공: " + transactionId
                                    + " / 검증 결과: "
                                    + validationStatus
                    );

                } catch (IllegalArgumentException | java.time.DateTimeException e) {

                    // 11. 날짜, 숫자 등 잘못된 데이터 처리
                    errorRows++;

                    saveUploadError(
                            batch.getId(),
                            record,
                            "INVALID_DATA",
                            e.getMessage()
                    );

                    System.out.println(
                            "잘못된 정산 데이터 - 행 번호: "
                                    + (record.getRecordNumber() + 1)
                                    + ", 원인: "
                                    + e.getMessage()
                    );
                }
            }

            // 12. 업로드 결과 저장
            batch.updateCounts(
                    totalRows,
                    successRows,
                    errorRows
            );

            uploadBatchRepository.save(batch);

            System.out.println("전체: " + totalRows);
            System.out.println("성공: " + successRows);
            System.out.println("실패: " + errorRows);

        } catch (IOException e) {

            throw new UncheckedIOException(
                    "CSV 파일 읽기에 실패했습니다.",
                    e
            );
        }
    }

    // 업로드 오류를 DB에 저장하는 메서드
    private void saveUploadError(
            Long batchId,
            CSVRecord record,
            String errorCode,
            String errorMessage
    ) {

        // CSV 한 행 전체를 JSON 문자열로 변환
        String rawData = objectMapper
                .valueToTree(record.toMap())
                .toString();

        UploadError uploadError = new UploadError(
                batchId,
                (int) record.getRecordNumber() + 1,
                errorCode,
                errorMessage,
                rawData
        );

        uploadErrorRepository.save(uploadError);
    }
}
