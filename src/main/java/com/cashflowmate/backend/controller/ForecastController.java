
package com.cashflowmate.backend.controller;

import com.cashflowmate.backend.dto.ForecastResponse;
import com.cashflowmate.backend.service.ForecastService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/forecast")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping
    public List<ForecastResponse> getForecast() {
        return forecastService.getForecast();
    }
}
