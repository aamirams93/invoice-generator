package com.invoice.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.model.ProductItems;

public interface ProductItemsRepo extends JpaRepository<ProductItems, Serializable>
{

}
