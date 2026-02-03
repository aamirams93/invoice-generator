package com.invoice.repo;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.model.BuyerOrder;

public interface BuyerOrderRepo extends JpaRepository<BuyerOrder, Serializable>
{
	public Optional<BuyerOrder> findByEmailStatus(char emailStatus);
}
