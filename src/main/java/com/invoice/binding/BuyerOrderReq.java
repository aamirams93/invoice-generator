package com.invoice.binding;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class BuyerOrderReq
{
	private String buyerName;
    private String buyerAddress;
    private String buyerEmailId;
    private BigDecimal totalDiscount;

    private List<OrderItemReq> items;

}
