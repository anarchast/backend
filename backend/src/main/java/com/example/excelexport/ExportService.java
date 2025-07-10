package com.example.excelexport;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExportService {

    private final Map<String, String> jobStatus = new ConcurrentHashMap<>();
    private final Map<String, byte[]> jobData = new ConcurrentHashMap<>();

    public String startExport() {
        String jobId = UUID.randomUUID().toString();
        jobStatus.put(jobId, "IN_PROGRESS");
        runExportAsync(jobId);
        return jobId;
    }

    public String getStatus(String id) {
        return jobStatus.getOrDefault(id, "UNKNOWN");
    }

    public ResponseEntity<byte[]> downloadFile(String id) {
        byte[] data = jobData.get(id);
        if (data == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @Async
    public void runExportAsync(String jobId) {
        try {
            List<Customer> customers = mockData();

            SXSSFWorkbook wb = new SXSSFWorkbook();
            Sheet sheet = wb.createSheet("Export");
            int rowIdx = 0;
            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Customer ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Order ID");
            header.createCell(3).setCellValue("Item");
            header.createCell(4).setCellValue("Date");

            for (Customer c : customers) {
                int startRow = rowIdx;
                for (Order o : c.getOrders()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(c.getId());
                    row.createCell(1).setCellValue(c.getName());
                    row.createCell(2).setCellValue(o.getId());
                    row.createCell(3).setCellValue(o.getItem());
                    row.createCell(4).setCellValue(o.getDate().toString());
                }
                if (c.getOrders().size() > 1) {
                    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                            startRow, rowIdx - 1, 0, 0));
                    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                            startRow, rowIdx - 1, 1, 1));
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            wb.close();

            jobData.put(jobId, baos.toByteArray());
            jobStatus.put(jobId, "DONE");

        } catch (Exception e) {
            jobStatus.put(jobId, "FAILED");
            e.printStackTrace();
        }
    }

    private List<Customer> mockData() {
        Customer c1 = new Customer(1, "Müller", List.of(
                new Order(1001, "Buch", new Date()),
                new Order(1002, "Stift", new Date())
        ));
        Customer c2 = new Customer(2, "Meier", List.of(
                new Order(1003, "Heft", new Date())
        ));
        return List.of(c1, c2);
    }
}
