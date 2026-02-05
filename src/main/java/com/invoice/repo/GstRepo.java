
package com.invoice.repo;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.model.GstTax;

/**
 * 
 */
public interface GstRepo extends JpaRepository<GstTax, Serializable>
{
	public Optional<GstTax> findByHsnCode(String hsnCode);
}
