package com.invoice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.invoice.binding.ProductItemsReq;
import com.invoice.exception.BusinessException;
import com.invoice.model.ProductCategory;
import com.invoice.model.ProductItems;
import com.invoice.model.UserEntity;
import com.invoice.repo.ProductCategoryRepo;
import com.invoice.repo.ProductItemsRepo;
import com.invoice.repo.UserRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlroductServiceImpl implements ProductService
{

	private final ProductItemsRepo prodcutRepo;

	private final ProductCategoryRepo productCategoryRepo;
	
	private final UserRepo userRepo;
	
	
	@Override
	@Transactional
	public boolean savePlan(ProductItemsReq plan, String email)
	{
	    ProductItems entity = new ProductItems();

	    BeanUtils.copyProperties(plan, entity);

	    UserEntity user = userRepo.findByEmailId(email)
	            .orElseThrow(() -> new BusinessException("User not found",""));

	    ProductCategory category = productCategoryRepo.findByCategoryName(plan.getCategoryName())
	    	    .orElseThrow(() -> new BusinessException("Category not found", "NOT_FOUND_CODE"));

	    entity.setProductCategoryId(category.getCategoryid());
	    entity.setActiveSw("ACTIVE");
	    entity.setCreatedBy(user.getFullName());

	    ProductItems saved = prodcutRepo.save(entity);

	    return saved.getProductId() != null;
	}

	
	
	@Override
	public Map<Integer, String> getProductCategory()
	{
		List<ProductCategory> categories = productCategoryRepo.findAll();
		Map<Integer, String> categoryMap = new HashMap<>();
		categories.forEach(category -> {
			categoryMap.put(category.getCategoryid(), category.getCategoryName());
		});
		return categoryMap;
	}

	@Override
	public ProductItems getProductById(Integer planId)
	{ 
		Optional<ProductItems> findByid = prodcutRepo.findById(planId);
			
		if (findByid.isPresent())
		{
			return findByid.get();

		} else
		{
			return null;
		}
	}

	@Override
	public boolean updateProduct(ProductItems product)
	{
		ProductItems saved = prodcutRepo.save(product);
		return saved.getProductId() != null;
	}

	@Override
	public boolean statusChange(Integer productId, String status)
	{
		Optional<ProductItems> findByid = prodcutRepo.findById(productId);
		if (findByid.isPresent())
		{
			findByid.get().setActiveSw(status);
			prodcutRepo.save(findByid.get());
			return true;
		}
		return false;
	}

	@Override
	public List<ProductItems> getAllProduct()
	{
		return prodcutRepo.findAll();
	}

}
