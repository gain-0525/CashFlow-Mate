package com.cashflowmate.backend.service;

import com.cashflowmate.backend.entity.BusinessSettings;
import com.cashflowmate.backend.repository.BusinessSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class BusinessSettingsService {

    private final BusinessSettingsRepository repository;
    public BusinessSettingsService(BusinessSettingsRepository repository) {
        this.repository = repository;
    }

    public BusinessSettings getSettings() {
        return repository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException(
                        "사업장 설정을 찾을 수 없습니다."
                ));
    }
}
