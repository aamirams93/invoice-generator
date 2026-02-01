package com.invoice.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRODUCT_ITEMS")
@Data
@NoArgsConstructor
public class ProductItems
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "PRODUCT_ID")
    private Integer productId;
    
    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;    
    
    @Column(name = "PRODUCT_CATEGORY_ID", nullable = false)
    private Integer productCategoryId;
    
    @Column(name ="PRODUCT_PRICE", nullable = false )
    private Long productPrice;
    
    @Column(name ="PRODUCT_QUANTITY", nullable = false )
    private Integer productQuantity;
    
    @Column(name ="ACTIVE_SW" )
    private String activeSw;
    
    @Column(name = "CREATED_BY")
    private String createdBy;
    
    @Column(name = "UPDATED_BY")
    private String updatedBy;
    
    @Column(name = "CREATED_DATE",updatable =  false)
    @CreationTimestamp
    private LocalDateTime createdDate;
    
    @Column(name = "UPDATED_DATE" ,insertable = false)
    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
