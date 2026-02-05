package com.invoice.report;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.invoice.binding.OrderReportDTO;
import com.invoice.repo.BuyerOrderRepo;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;


@Service
@AllArgsConstructor
public class InovoiceReport
{


	private final BuyerOrderRepo buyerRepo;


	public byte[] reportDetailsJasperReportInBytes(String fileType) throws Exception
	{
		// 1. Load template
		String template = "templates/order.jrxml";
		String path = ResourceUtils.getFile("classpath:" + template).getAbsolutePath();

		// 2. Fetch Data
		List<OrderReportDTO> reportDetailsList = buyerRepo.fetchOrderReport();
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(reportDetailsList);

		// 3. Parameters
		Map<String, Object> parameters = new HashMap<>();
		try (InputStream logoStream = new ClassPathResource("templates/logo.jpg").getInputStream())
		{
			parameters.put("logo", logoStream);
		}
		parameters.put("companyName", "BLACK STAR TECHNOLOGIES");
		parameters.put("address", "Address: Raheja Mind Space Entrance Gate, HITEC City, Hyderabad -500081");
		parameters.put("header", "Citizens Plan Report");
		parameters.put("createdBy", "Satya Kaveti");

		// 4. Compile and Fill
		JasperReport jasperReport = JasperCompileManager.compileReport(path);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanCollectionDataSource);

		// 5. Export Report Directly (no util class needed)
		return exportJasperReportBytes(jasperPrint, fileType);
	}

	private byte[] exportJasperReportBytes(JasperPrint jasperPrint, String fileType) throws JRException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		switch (fileType.toUpperCase())
		{
		case "CSV":
			JRCsvExporter csvExporter = new JRCsvExporter();
			csvExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			csvExporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
			csvExporter.exportReport();
			break;

		case "XLSX":
			JRXlsxExporter xlsxExporter = new JRXlsxExporter();
			xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			xlsxExporter.exportReport();
			break;

		case "HTML":
			HtmlExporter htmlExporter = new HtmlExporter();
			htmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			htmlExporter.setExporterOutput(new SimpleHtmlExporterOutput(outputStream));
			htmlExporter.exportReport();
			break;

		case "XML":
			JRXmlExporter xmlExporter = new JRXmlExporter();
			xmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			xmlExporter.setExporterOutput(new SimpleXmlExporterOutput(outputStream));
			xmlExporter.exportReport();
			break;

		case "DOC":
			JRRtfExporter docExporter = new JRRtfExporter();
			docExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			docExporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
			docExporter.exportReport();
			break;

		default: // PDF as default
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			break;
		}

		return outputStream.toByteArray();
	}
}
