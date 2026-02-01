package com.invoice.binding;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;


@Data
public class PackageBinding
{

		//private String emailId;

	    private String packLevel;

	    private Double packAmount;

	    private MultipartFile viewImage;
		


}
