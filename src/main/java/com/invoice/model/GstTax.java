package com.invoice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TBL_GST_TAX")
@Data
public class GstTax
{
	@Id
	@Column(name = "HSN_CODE")
	private String hsnCode;

	@Column(name = "HSN_DESCRIPTION")
	private String hsnDescription;

	@Column(name = "STATE_GST")
	private Double sGst;
	
	@Column(name = "CENTRAL_GST")
	private Double cGst;
	
	@Column(name = "INTER_STATE_GST")
	private Double iGst;

}
