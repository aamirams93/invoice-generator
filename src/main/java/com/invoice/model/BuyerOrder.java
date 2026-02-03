package com.invoice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "TBL_BUYER_ORDER")
@Data
public class BuyerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(
        name = "order_seq",
        sequenceName = "ORDER_SEQ",
        allocationSize = 1
    )
    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "BUYER_NAME")
    private String buyerName;

    @Column(name = "BUYER_ADDRESS")
    private String buyerAddress;

    @NotNull
    @Email
    @Column(name = "BUYER_EMAIL_ID")
    private String buyerEmailId;
    
    @Column(name = "EMAIL_STATUS")
    private char emailStatus;
    
    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "TOTAL_DISCOUNT")
    private BigDecimal totalDiscount;

    @Column(name = "TOTAL_PAYABLE_AMOUNT", nullable = false)
    private BigDecimal totalPayableAmount;

    @Column(name = "CREATED_BY", updatable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "CREATED_DATE", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "UPDATED_DATE", insertable = false)
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuyerOrderItem> items = new ArrayList<>();
    
    
}
