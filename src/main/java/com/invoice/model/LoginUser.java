package com.invoice.model;

import java.util.Date;
import java.util.TimeZone;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TBL_LOGGED_USER")
@Data
public class LoginUser
{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "CUSTOMER_USER_ID")
	private String userNo;

	@Column(name = "FIRST_LOGIN_DATE_TIME", updatable = false)
	private Date createdDate;

	@Column(name = "LAST_LOGIN_DATE_TIME", insertable = false)
	private Date updatedDate;

	@Column(name = "CUSTOMER_IP_ADDRESS")
	private String ipAddress;
	
	@Column(name = "LOGGED_STATUS")
	private String loggedStatus;

	@Column(name = "LOCKED_STATUS")
	private String lockedStatus;
	
	  @PostConstruct
	    public void init(){
	      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	    }
}
