package com.invoice.model;

import java.util.Date;
import java.util.TimeZone;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "TBL_USERS")
public class UserEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_no_seq")
	@SequenceGenerator(name = "user_no_seq", sequenceName = "user_no_sequence", allocationSize = 1)
	@Column(name = "USER_NO", nullable = false, unique = true)
	private Long userNo; // 6-digit sequential user number

	@Column(name = "CUSTOMER_NAME", nullable = false)
	private String fullName;

	@NotNull
	@Email
	@Size(min = 8)
	@Column(name = "CUSTOMER_EMAIL_ID")
	private String emailId;
	

	@Column(name = "COMPANY_MOBILE_NO", nullable = false)
	@Size(min = 10)
	private String mobileNo;

	@Column(name = "GENDER", nullable = false)
	private String gender;

	@Column(name = "ACCOUNT_STATUS")
	private boolean accStatus;

	@Column(name = "CUSTOMER_PASSWORD")
	private String password;

	@Column(name = "OTP_CREATION_TIME", nullable = true)
	private Date otpCreationTime;

	@Column(name = "CREATED_DATE", updatable = false)
	private Date createdDate;

	@Column(name = "UPDATED_DATE", insertable = false)
	private Date updatedDate;

	@Column(name = "CUSTOMER_IP_ADDRESS")
	private String ipAddress;

	@Column(name = "COMPANY_NAME")
	private String companyName;

	@Column(name = "COMPANY_ADDRESS")
	private String companyAddress;

	public String getUserNo()
	{
		return String.format("IRF%03d", this.userNo);
	}

	@PostConstruct
	public void init()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	}

}