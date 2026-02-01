package com.invoice.repo;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.invoice.model.ProductCategory;

@Repository
@RepositoryRestResource(exported = false)
public interface ProductCategoryRepo extends JpaRepository<ProductCategory, Serializable>
{
	Optional<ProductCategory> findByCategoryName(String categoryName);
}
