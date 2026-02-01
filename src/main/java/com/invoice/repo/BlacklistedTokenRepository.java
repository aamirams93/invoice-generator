package com.invoice.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.invoice.model.BlacklistedToken;

@Repository
@RepositoryRestResource(exported = false)
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String>
{
	boolean existsByJti(String jti);
}
