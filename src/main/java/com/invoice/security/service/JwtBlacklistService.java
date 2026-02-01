package com.invoice.security.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.invoice.model.BlacklistedToken;
import com.invoice.repo.BlacklistedTokenRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JwtBlacklistService
{

	private BlacklistedTokenRepository repo;

	@Transactional
	    public void addToBlacklist(String jti, Date expiry) 
	{
	        BlacklistedToken entity = new BlacklistedToken();
	        entity.setJti(jti);
	        entity.setExpiryDate(expiry);
	        repo.save(entity);
	    }

	public boolean isBlacklisted(String jti)
	{
		return repo.existsByJti(jti);
	}

	// Auto delete expired tokens
//    @Scheduled(cron = "0 0 * * * *")  // Every 1 hour
//    public void removeExpiredTokens() {
//        List<BlacklistedToken> all = repo.findAll();
//
//        Date now = new Date();
//        all.stream()
//           .filter(t -> t.getExpiryDate().before(now))
//           .forEach(t -> repo.delete(t));
//    }
}
