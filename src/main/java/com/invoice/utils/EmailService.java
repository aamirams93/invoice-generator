package com.invoice.utils;

import java.io.BufferedReader;
import java.io.FileReader;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
@Component
@AllArgsConstructor
public class EmailService
{

	private JavaMailSender mailSender;

	public void sendEmail(String to, String subject, String body)
	{
		MimeMessage message = mailSender.createMimeMessage();
		try
		{
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);
			mailSender.send(message);
		} catch (MessagingException e)
		{
			e.printStackTrace();
		}
	}
	
	// for sending report as attachment
	public void sendEmailWithAttachment(String to, String subject, String fileName, byte[] content) {
	    try {
	        MimeMessage message = mailSender.createMimeMessage();
	        
	        // 'true' indicates this is a multipart message (required for attachments)
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);
	        
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText("Please find your report attached.");

	        // Add the attachment
	        helper.addAttachment(fileName, new ByteArrayResource(content));

	        mailSender.send(message);
	    } catch (MessagingException e) {
	        // Log the error
	        e.printStackTrace();
	    }}

	public String readEmailBody(String fileName, String fullname, String tempPassword)
	{
		String mailBody = "";
		String url = "";
		try
		{
			FileReader reader = new FileReader(fileName);
			BufferedReader bf = new BufferedReader(reader);

			StringBuilder st = new StringBuilder();
			String line = bf.readLine();
			while (line != null)
			{
				st.append(line);
				line = bf.readLine();
			}
			bf.close();
			mailBody = st.toString();
			mailBody = mailBody.replace("{FULLNAME}", fullname);
			mailBody = mailBody.replace("{TEMP-PWD}", tempPassword);
			mailBody = mailBody.replace("{PWD}", tempPassword);
			mailBody = mailBody.replace("{URL}", url);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return mailBody;
	}
	
	@Async("emailExecutor")
	public void sendEmailAsync(String to, String subject, String body)
	{
		sendEmail(to, subject, body);
	}

}
