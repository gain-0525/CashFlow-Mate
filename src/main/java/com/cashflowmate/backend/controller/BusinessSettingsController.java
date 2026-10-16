package com.cashflowmate.backend.controller;

import com.cashflowmate.backend.entity.BusinessSettings;
import com.cashflowmate.backend.service.BusinessSettingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class BusinessSettingsController {

    private final BusinessSettingsService service;
    public BusinessSettingsController(BusinessSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public BusinessSettings getSettings(){
        return service.getSettings();
    }
}
