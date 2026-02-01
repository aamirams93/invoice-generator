package com.invoice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.invoice.model.LoginUser;

@RepositoryRestResource(exported = false)
public interface LoginUserRepo extends JpaRepository<LoginUser, Integer>
{

	@Query("SELECT u FROM LoginUser u WHERE u.userNo = :userNo")
	LoginUser findByUserNo(String userNo);

}
