package com.invoice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "AUTHORITIES")
@Data
public class Authorities
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@Column(name = "CUSTOMER_EMAIL_ID")
	private String email;

	@Column(name = "CUSTOMER_ROLE")
	private String authority;
	
	
}
