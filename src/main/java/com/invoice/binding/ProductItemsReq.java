package com.invoice.binding;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductItemsReq
{

    private String productName;    
    
    private BigDecimal productPrice;

    private Integer productQuantity;
    
	private String categoryName;
	
	private String hsnCode;
	    


}
