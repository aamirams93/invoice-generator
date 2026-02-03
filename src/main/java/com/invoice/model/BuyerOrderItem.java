package com.invoice.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TBL_BUYER_ORDER_ITEM")
@Data
public class BuyerOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private BuyerOrder order;

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;

    @Column(name = "PRODUCT_PRICE", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "PRODUCT_QUANTITY", nullable = false)
    private Integer productQuantity;

    @Column(name = "ITEM_AMOUNT", nullable = false)
    private BigDecimal itemAmount;
}
