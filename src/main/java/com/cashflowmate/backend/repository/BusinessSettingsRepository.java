package com.cashflowmate.backend.repository;

import com.cashflowmate.backend.entity.BusinessSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessSettingsRepository extends JpaRepository<BusinessSettings, Long> {

}