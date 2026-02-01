package com.invoice.model;

import java.util.Date;
import java.util.TimeZone;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "TBL_BLACK_LISTED_TOKEN")
public class BlacklistedToken
{

	@Id
	@Column(name = "TOKEN",length = 100)
	private String jti;

	@Column(name = "TOKEN_EXPIRY_DATE", nullable = false)
	private Date expiryDate;
	
	

    @PostConstruct
    public void init(){
      // Setting Spring Boot SetTimeZone
      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

}
