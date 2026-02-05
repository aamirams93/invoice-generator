package com.invoice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.invoice.binding.ProductItemsReq;
import com.invoice.exception.BusinessException;
import com.invoice.model.GstTax;
import com.invoice.model.ProductCategory;
import com.invoice.model.ProductItems;
import com.invoice.model.UserEntity;
import com.invoice.repo.GstRepo;
import com.invoice.repo.ProductCategoryRepo;
import com.invoice.repo.ProductItemsRepo;
import com.invoice.repo.UserRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlroductServiceImpl implements ProductService
{

	private final ProductItemsRepo productRepo;

	private final ProductCategoryRepo productCategoryRepo;

	private final UserRepo userRepo;

	private final GstRepo gstRepo;

	@Override
	@Transactional
	public boolean savePlan(ProductItemsReq plan, String email)
	{
		ProductItems entity = new ProductItems();

		BeanUtils.copyProperties(plan, entity);

		UserEntity user = userRepo.findByEmailId(email).orElseThrow(() -> new BusinessException("User not found", ""));

		ProductCategory category = productCategoryRepo.findByCategoryName(plan.getCategoryName())
				.orElseThrow(() -> new BusinessException("Category not found", "NOT_FOUND_CODE"));

		GstTax gst = gstRepo.findById(plan.getHsnCode())
				.orElseThrow(() -> new BusinessException("GST details not found for HSN Code: " + plan.getHsnCode(),
						"NOT_FOUND_CODE"));

		entity.setProductCategoryId(category.getCategoryid());
		entity.setTotalGst(Math.round(gst.getCGst() + gst.getSGst() + gst.getIGst()));
		entity.setActiveSw("ACTIVE");
		entity.setCreatedBy(user.getFullName());

		ProductItems saved = productRepo.save(entity);

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
		Optional<ProductItems> findByid = productRepo.findById(planId);

		if (findByid.isPresent())
		{
			return findByid.get();

		} else
		{
			return null;
		}
	}

	@Override
	@Transactional
	public boolean updateProduct(ProductItemsReq productReq, String email)
	{

		UserEntity user = userRepo.findByEmailId(email)
				.orElseThrow(() -> new BusinessException("User not found", "Not Found"));

		ProductItems product = productRepo.findByProductName(productReq.getProductName())
				.orElseThrow(() -> new BusinessException("Product not found", "NOT_FOUND_CODE"));
		GstTax gst = gstRepo.findById(productReq.getHsnCode())
				.orElseThrow(() -> new BusinessException("GST details not found for HSN Code: " + productReq.getHsnCode(),
						"NOT_FOUND_CODE"));
		product.setProductName(productReq.getProductName());
		product.setProductQuantity(productReq.getProductQuantity());
		product.setHsnCode(productReq.getHsnCode());
		product.setTotalGst(Math.round(gst.getCGst() + gst.getSGst() + gst.getIGst()));
		product.setProductPrice(productReq.getProductPrice());
		product.setUpdatedBy(user.getFullName());
		productRepo.save(product);

		return true;
	}

	@Override
	public boolean statusChange(Integer productId, String status)
	{
		Optional<ProductItems> findByid = productRepo.findById(productId);
		if (findByid.isPresent())
		{
			findByid.get().setActiveSw(status);
			productRepo.save(findByid.get());
			return true;
		}
		return false;
	}

	@Override
	public List<ProductItems> getAllProduct()
	{
		return productRepo.findAll();
	}

}
