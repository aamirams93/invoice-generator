package com.invoice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.invoice.binding.UserAddingData;
import com.invoice.binding.UserData;
import com.invoice.exception.BusinessException;
import com.invoice.model.Authorities;
import com.invoice.model.LoginUser;
import com.invoice.model.UserEntity;
import com.invoice.repo.AuthoritiesRepo;
import com.invoice.repo.LoginUserRepo;
import com.invoice.repo.UserRepo;
import com.invoice.security.service.JwtBlacklistService;
import com.invoice.security.service.JwtService;
import com.invoice.utils.EmailService;
import com.invoice.utils.LoginCredValidator;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service

@AllArgsConstructor
public class UserManagementServiceImpl implements UserService
{

	private final JwtBlacklistService jwtBlacklistService;

	private final UserRepo repo;

	private final EmailService emailutils;

	private final AuthoritiesRepo authRepo;

	private PasswordEncoder passwordEncoder;

	private final LoginUserRepo loginRepo;

	private final JwtService jwt;

	private static final Logger log = LoggerFactory.getLogger(UserManagementServiceImpl.class);

	@Override
	@Transactional
	public boolean saveUser(UserAddingData userData,String ip) {

	    if (repo.existsByEmailId(userData.getEmailId())) {
	        throw new BusinessException("EMAIL_EXISTS", "Email already exists");
	    }

	    if (repo.existsByMobileNo(userData.getMobileNo())) {
	        throw new BusinessException("MOBILE_EXISTS", "Mobile number already exists");
	    }

	    try {
	        UserEntity entity = new UserEntity();
	        BeanUtils.copyProperties(userData, entity);

	        entity.setIpAddress(ip);
	        entity.setAccStatus(true);
	        entity.setCreatedDate(new Date());

	        UserEntity savedUser = repo.save(entity);

	        Authorities auth = new Authorities();
	        auth.setEmail(userData.getEmailId());
	        auth.setAuthority("ROLE_USER");
	        authRepo.save(auth);

	        return savedUser.getUserNo() != null;

	    } catch (DataIntegrityViolationException e) {
	        log.error("Duplication error while creating account for {}", userData.getEmailId(), e);
	        throw new BusinessException("DATA_INTEGRITY_ERROR", "Duplicate data found");
	    } catch (Exception e) {
	        log.error("Unexpected error while creating account for {}", userData.getEmailId(), e);
	        throw new BusinessException("ACCOUNT_CREATION_FAILED", "Failed to create account");
	    }
	}
	
	
	@Transactional
	public void generateEmailOtp(UserData userData) {

	    if (userData.getEmailId() == null || userData.getEmailId().isBlank()) {
	        throw new BusinessException("INVALID_EMAIL", "Email must not be empty");
	    }

	    UserEntity entity = repo.findByEmailId(userData.getEmailId())
	            .orElseThrow(() -> new BusinessException("USER_NOT_FOUND",
	                    "User not found with email: " + userData.getEmailId()));

	    try {
	        String tempPassword = LoginCredValidator.randomP();

	        repo.updateOtpAndPassword(
	                entity.getEmailId(),
	                passwordEncoder.encode(tempPassword),
	                entity.getOtpCreationTime()
	        );

	        String subject = "Registration";
	        String name = entity.getFullName();
	        String body = emailutils.readEmailBody("new-account.html", name, tempPassword);

	       emailutils.sendEmailAsync(entity.getEmailId(), subject, body);

	        log.info("OTP generated and email sent for user: {}", entity.getEmailId());

	    } catch (Exception e) {
	        log.error("Failed to process OTP email for user: {}", entity.getEmailId(), e);
	        throw new BusinessException("EMAIL_SEND_FAILED", "Failed to send OTP email");
	    }
	}

	public void logLoginSuccess(String email, String ip)
	{
		String userId = repo.findUserNoByEmailId(email);
		LoginUser login = loginRepo.findByUserNo(userId);

		if (login == null)
		{
			login = new LoginUser();
			login.setUserNo(userId);
		}

		login.setIpAddress(ip);
		login.setUpdatedDate(new Date());
		login.setLockedStatus("Y");
		login.setLoggedStatus("Y");

		loginRepo.save(login);
	}

	@Override
	public boolean isLoginBlocked(String email)
	{

		String userId = repo.findUserNoByEmailId(email);
		if (userId == null)
		{
			throw new BusinessException("Email Not Found", "Email Id is Not Registered Please Registered first");
		}

		LoginUser login = loginRepo.findByUserNo(userId);
		if (login == null)
		{
			return false; // never logged in before → allow login
		}

		// ❌ If already logged in
		throw new BusinessException("Email Id Already Login","Email Id is Already Login Please Logout");
	}

	@Transactional
	public void logoutUser(String jwtToken)
	{

		// Extract JTI + expiry
		String jti = jwt.extractJti(jwtToken);
		Date exp = jwt.extractExpiration(jwtToken);
		// Blacklist by JTI
		jwtBlacklistService.addToBlacklist(jti, exp);

		// Update login table
		String username = jwt.extractUsername(jwtToken);
		String userId = repo.findUserNoByEmailId(username);

		LoginUser login = loginRepo.findByUserNo(userId);
		if (login != null)
		{
			login.setLockedStatus("N");
			login.setLoggedStatus("N");
			loginRepo.save(login);
			throw new BusinessException("Logout Succesflly","User Logout Suceessfully");
		}
	}

	@Override
	public List<UserData> getAllUsers()
	{
		List<UserEntity> findAll = repo.findAll();

		List<UserData> user = new ArrayList<>();
		for (UserEntity um : findAll)
		{
			UserData u = new UserData();
			BeanUtils.copyProperties(um, user);
			user.add(u);
		}
		return user;
	}


	@Override
	public boolean changeStatus(Integer userId, boolean accStatus)
	{
		Optional<UserEntity> chnageById = repo.findById(userId);
		if (chnageById.isPresent())
		{
			UserEntity entity = chnageById.get();
			entity.setAccStatus(accStatus);
			repo.save(entity);
			return true;
		}
		return false;
	}

//	@Override
//	@Scheduled(fixedRate = 600000000) // runs every 60 seconds
//	public void deletePassword()
//	{
//		LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(5);
//		repo.clearExpiredOtp(expiryTime);
//		System.out.println("♻ Otp Deleted.");
//	}

	@Override
	public String getUserById(String email)
	{
		return null;
	}

	@Override
	public UserData getUserByEmail(String emailId)
	{
		return null;
	}



}
