package com.invoice.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.invoice.model.Authorities;

@RepositoryRestResource(exported = false)
public interface AuthoritiesRepo extends JpaRepository<Authorities, Integer>
{
	public List<Authorities> findAuthorityByEmailId(String emailId);
}
