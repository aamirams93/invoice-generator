package com.invoice.binding;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SuperUser
{
	private String fullName;

	private String emailId;
	
	@Size(min = 8)
	private String mobileNo;

	private String gender;
	
	private String authority;


}
