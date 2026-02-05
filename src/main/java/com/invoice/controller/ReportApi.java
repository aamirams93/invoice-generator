package com.invoice.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;

import com.invoice.binding.ReportTypeEnum;
import com.invoice.report.InovoiceReport;
import com.invoice.utils.EmailService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ReportApi
{
    private InovoiceReport jasperReportService;
    
    private EmailService emailService;

  
    @GetMapping("/jasper")
    public ResponseEntity<Resource> employeeJasperReport24(@RequestParam String fileType) throws Exception {

        ReportTypeEnum report = ReportTypeEnum.getReportTypeByCode(fileType);
       // log.info("Report type: {}", report);

        // Call service
        byte[] bytes = jasperReportService.reportDetailsJasperReportInBytes(fileType);

        if (bytes != null && bytes.length > 0) {
            ByteArrayResource resource = new ByteArrayResource(bytes);

            // Safer file name (no colons)
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "ReportDetails_" + timestamp + report.getExtension();

            emailService.sendEmailWithAttachment("aamirkhn035@gmail.com", "Your Report", fileName, bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
            
        } else {
            throw new IllegalArgumentException("File Download Failed");
        }
    }
    
}
