package com.invoice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.invoice.binding.ProductCategoryReq;
import com.invoice.model.ProductCategory;
import com.invoice.model.UserEntity;
import com.invoice.repo.ProductCategoryRepo;
import com.invoice.repo.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductCategoryImpl implements ProductCategoryService
{
    private final ProductCategoryRepo repo;
    
	private final UserRepo userRepo;
    
  
    @Override
    public boolean saveProductCategory(ProductCategoryReq category, String email)
    {
        ProductCategory enity = new ProductCategory();
        BeanUtils.copyProperties(category, enity);
        UserEntity entity =  userRepo.findByEmailId(email).orElseThrow(() -> new RuntimeException("not fouund"));
        enity.setActiveSw("ACTIVE");
        String name  = entity.getFullName();
        enity.setCreatedBy(name);
        ProductCategory saved = repo.save(enity);
        return saved.getCategoryid() != null;
    }

    @Override
    public List<ProductCategory> getAllCategory()
    {
        return repo.findAll();
    }

    @Override
    public ProductCategory getProductCategoryById(Integer categoryid)
    {
        Optional<ProductCategory> findById = repo.findById(categoryid);
        if (findById.isPresent())
        {
            return findById.get();
        } else
        {
            return null;
        }
    }

    @Override
    public boolean updateProduct(ProductCategory category)
    {
        ProductCategory enity = new ProductCategory();
        BeanUtils.copyProperties(category, enity);
        ProductCategory saved = repo.save(enity);
        return saved.getCategoryid() != null;
    }

    @Override
    public boolean statusChange(Integer categoryid, String activeSw)
    {

        Optional<ProductCategory> findByid = repo.findById(categoryid);
        if (findByid.isPresent())
        {
            ProductCategory product = findByid.get();
            product.setActiveSw(activeSw);
            repo.save(product);
            return true;
        }
        return false;
    }

}
