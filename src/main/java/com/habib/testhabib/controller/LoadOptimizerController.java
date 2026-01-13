package com.habib.testhabib.controller;

import com.habib.testhabib.model.*;
import com.habib.testhabib.service.LoadOptimizerService;
import com.habib.testhabib.validator.RequestValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/load-optimizer")
public class LoadOptimizerController {

    private final LoadOptimizerService service;
    private final RequestValidator validator;

    public LoadOptimizerController(
            LoadOptimizerService service,
            RequestValidator validator
    ) {
        this.service = service;
        this.validator = validator;
    }

    @PostMapping("/optimize")
    public ResponseEntity<OptimizeResponse> optimize(
            @RequestBody OptimizeRequest request
    ) {
        validator.validate(request);
        return ResponseEntity.ok(service.optimize(request));
    }
}