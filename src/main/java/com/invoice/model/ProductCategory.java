package com.invoice.model;

import java.time.LocalDateTime;
import java.util.TimeZone;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table( name = "PRODUCT_CATEGORY")
public class ProductCategory
{   
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "CATEGORY_ID")
    private Integer categoryid;
    
    @Column(name = "CATEGORY_NAME" ,nullable = false)
    private String categoryName;
        
    @Column(name = "CATEGORY_TYPE" ,nullable = false)
    private String categorytype;
    
    @Column(name ="ACTIVE_SW" )
    private String activeSw;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
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
    
    
    @PostConstruct
    public void init(){
      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }
    
}
