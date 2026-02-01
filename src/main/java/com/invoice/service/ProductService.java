package com.invoice.service;

import java.util.List;
import java.util.Map;

import com.invoice.binding.ProductItemsReq;
import com.invoice.model.ProductItems;



public interface ProductService
{
    public Map<Integer, String> getProductCategory();
    
    public boolean savePlan(ProductItemsReq plan,String email);
    
    public List<ProductItems>getAllProduct();
    
    public ProductItems getProductById(Integer planId);
    
    public boolean updateProduct(ProductItems plan);
        
    public boolean statusChange(Integer planId,String status);

	

}
