package com.invoice.binding;

import lombok.Data;

@Data
public class ProductItemsReq
{

    private String productName;    
    
    private Long productPrice;

    private Integer productQuantity;
    
	private String categoryName;
    


}
