package com.invoice.service;

import java.util.List;


import com.invoice.binding.ProductCategoryReq;
import com.invoice.model.ProductCategory;




public interface ProductCategoryService
{

    public boolean saveProductCategory(ProductCategoryReq category,String email);

    public List<ProductCategory> getAllCategory();

    public ProductCategory getProductCategoryById(Integer categoryid);

    public boolean updateProduct(ProductCategory category);

    public boolean statusChange(Integer categoryid, String activeSw);
}
