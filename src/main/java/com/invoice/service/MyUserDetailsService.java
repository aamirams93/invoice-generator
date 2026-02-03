package com.invoice.service;



import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.invoice.model.Authorities;
import com.invoice.model.UserEntity;
import com.invoice.model.UserPrincipal;
import com.invoice.repo.AuthoritiesRepo;
import com.invoice.repo.UserRepo;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService
{

	private final UserRepo repo;
	private final AuthoritiesRepo authRepo;
 

	@Transactional
	@Override	
	public UserDetails loadUserByUsername(String emailId) {

	    UserEntity user = repo.findByEmailId(emailId)
	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

	    List<Authorities> roles = authRepo.findAuthorityByEmailId(emailId);

	    return new UserPrincipal(user, roles);
	}
//	 @Transactional
//	    public UserDetails loadUserByPhone(Long mobileNo) {
//		 UserEntity user = repo.findByMobileNo(mobileNo)
//	                .orElseThrow(() -> new UsernameNotFoundException("User not found, phone and password: " + mobileNo));
//
//		 return User.withUsername(user.getMobileNo()).password(user.getPassword()).roles("USER").build();
//	    }


}