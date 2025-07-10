package com.example.excelexport;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin
@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startExport() {
        String jobId = exportService.startExport();
        return ResponseEntity.ok(Map.of("jobId", jobId));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable("id") String id) {
        String status = exportService.getStatus(id);
        return ResponseEntity.ok(Map.of("status", status));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") String id) {
        return exportService.downloadFile(id);
    }
}