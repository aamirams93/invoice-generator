package com.invoice.repo;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.model.ProductItems;

public interface ProductItemsRepo extends JpaRepository<ProductItems, Serializable>
{
	public Optional<ProductItems> findByProductName(String productName);
}
