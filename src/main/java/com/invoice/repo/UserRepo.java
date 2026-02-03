package com.invoice.repo;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.invoice.model.UserEntity;

import jakarta.transaction.Transactional;

@Repository
@RepositoryRestResource(exported = false)
public interface UserRepo extends JpaRepository<UserEntity, Integer>
{
	public UserEntity findByEmailIdAndPassword(String emailId, String password);
	
	Optional<UserEntity> findByEmailId(String email);
	Optional<UserEntity> findByMobileNo(String mobileNo);

	@Query("SELECT u.userNo FROM UserEntity u WHERE u.emailId = :email")
	String findUserNoByEmailId(String email);

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u SET u.password = NULL WHERE u.otpCreationTime <= :expiryTime")
	void clearExpiredOtp(@Param("expiryTime") Date expiryTime);
	
	@Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.password = :password, u.otpCreationTime = :otpTime WHERE u.emailId = :email")
    int updateOtpAndPassword(@Param("email") String email,
                             @Param("password") String password,
                             @Param("otpTime") Date otpTime);

	boolean existsByEmailId(String emailId);

	boolean existsByMobileNo(String mobileNo);

}
