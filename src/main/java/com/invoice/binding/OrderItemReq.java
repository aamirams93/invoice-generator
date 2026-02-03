package com.invoice.binding;

import lombok.Data;

@Data
public class OrderItemReq {
    private String productName;
    private Integer productQuantity;
}
