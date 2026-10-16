
package com.cashflowmate.backend.service;

import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.cashflowmate.backend.entity.UploadError;
import com.cashflowmate.backend.repository.UploadErrorRepository;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import com.cashflowmate.backend.entity.UploadBatch;
import com.cashflowmate.backend.repository.SalesRepository;
import com.cashflowmate.backend.repository.UploadBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cashflowmate.backend.entity.Sales;
import java.time.LocalDate;
import java.math.BigDecimal;

@Service
public class SalesUploadService {

    private final SalesRepository salesRepository;
    private final UploadBatchRepository uploadBatchRepository;
    private final UploadErrorRepository uploadErrorRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SalesUploadService(
            SalesRepository salesRepository,
            UploadBatchRepository uploadBatchRepository,
            UploadErrorRepository uploadErrorRepository
    ) {
        this.salesRepository = salesRepository;
        this.uploadBatchRepository = uploadBatchRepository;
        this.uploadErrorRepository = uploadErrorRepository;
    }

    @Transactional
    public void uploadSales(MultipartFile file) {

        // 1. 빈 파일인지 확인
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }

        // 2. 업로드 기록 생성
        UploadBatch batch = new UploadBatch(
                file.getOriginalFilename(),
                "SALES"
        );

        // 3. MySQL에 업로드 기록 저장
        uploadBatchRepository.save(batch);


        try (
                InputStreamReader reader = new InputStreamReader(
                        file.getInputStream(),
                        StandardCharsets.UTF_8
                );

                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .get()
                        .parse(reader)
        ) {

            int totalRows = 0;
            int successRows = 0;
            int errorRows = 0;


            for (CSVRecord record : parser) {

                totalRows++;

                try {
                    String transactionId = record.get("transaction_id").trim();

                    LocalDate saleDate = LocalDate.parse(
                            record.get("sale_date").trim()
                    );

                    BigDecimal amount = new BigDecimal(
                            record.get("amount").trim()
                    );

                    String paymentMethod = record.get("payment_method").trim();

                    // 필수값 검사
                    if (transactionId.isEmpty()
                            || amount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("거래번호 또는 금액이 올바르지 않습니다.");
                    }

                    // 중복 거래번호 검사

                    if (salesRepository.existsByTransactionId(transactionId)) {
                        errorRows++;

                        String rawData = "{\"transaction_id\":\""
                                + transactionId + "\"}";

                        UploadError uploadError = new UploadError(
                                batch.getId(),
                                (int) record.getRecordNumber() + 1,
                                "DUPLICATE_TRANSACTION",
                                "이미 존재하는 거래번호입니다.",
                                rawData
                        );

                        uploadErrorRepository.save(uploadError);

                        continue;
                    }


                    // 정상 매출 저장
                    Sales sale = new Sales(
                            transactionId,
                            saleDate,
                            amount,
                            paymentMethod,
                            batch.getId()
                    );

                    salesRepository.save(sale);
                    successRows++;


                } catch (IllegalArgumentException | java.time.DateTimeException e) {
                    errorRows++;

                    String rawData;
                    try {
                        rawData = objectMapper.writeValueAsString(record.toMap());
                    } catch (JacksonException jsonException) {
                        rawData = "{}";
                    }

                    UploadError uploadError = new UploadError(
                            batch.getId(),
                            (int) record.getRecordNumber() + 1,
                            "INVALID_DATA",
                            e.getMessage(),
                            rawData
                    );

                    uploadErrorRepository.save(uploadError);
                }

            }
            batch.updateCounts(totalRows, successRows, errorRows);
            uploadBatchRepository.save(batch);

            System.out.println("전체: " + totalRows);
            System.out.println("성공: " + successRows);
            System.out.println("실패: " + errorRows);



        } catch (IOException e) {
            throw new UncheckedIOException("CSV 파일을 읽을 수 없습니다.", e);
        }

    }

}
