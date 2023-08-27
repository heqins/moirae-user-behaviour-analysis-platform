package com.report.sink.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "test")
@Tag(name = "test-controller", description = "test")
public class TestController {

    @Operation(summary = "testQuery")
    @GetMapping("/test/query")
    public void test() {
    }


}
