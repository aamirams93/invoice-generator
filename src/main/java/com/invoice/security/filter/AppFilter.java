package com.invoice.security.filter;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.invoice.security.rate.RateLimitService;
import com.invoice.security.service.JwtBlacklistService;
import com.invoice.security.service.JwtService;
import com.invoice.service.MyUserDetailsService;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AppFilter extends OncePerRequestFilter
{

	private final JwtService jwtService;
	private final MyUserDetailsService userDetailsService;
	private final JwtBlacklistService blacklistService;
	private final RateLimitService rateLimitService;
	private static final Logger log = LoggerFactory.getLogger(AppFilter.class);

	private static final Set<String> RATE_LIMITED_APIS = Set.of("/api/v1/auth/login", "/api/v1/auth/add",
			"/api/v1/auth/motp"

	);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException
	{

		String uri = request.getRequestURI();

		// Rate limit only selected APIs
		isRateLimited(uri, request, response);
	

		// JWT processing for secured APIs
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer "))
		{

			String token = authHeader.substring(7);

			try
			{
				String username = jwtService.extractUsername(token);

				String jti = jwtService.extractJti(token);
				if (jti != null && blacklistService.isBlacklisted(jti))
				{
					sendUnauthorized(response, "Token revoked");
					return;
				}

				if (SecurityContextHolder.getContext().getAuthentication() == null)
				{

					UserDetails userDetails = userDetailsService.loadUserByUsername(username);

					if (!jwtService.validateToken(token, userDetails))
					{
						sendUnauthorized(response, "Invalid token");
						return;
					}

					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(auth);
				}

			} catch (Exception e)
			{
				sendUnauthorized(response, "Invalid JWT");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private boolean isRateLimited(String uri, HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{

		setSecurityHeaders(response);

		if (RATE_LIMITED_APIS.contains(uri))
		{
			String ip = request.getRemoteAddr();
			Bucket bucket = rateLimitService.resolveBucket(ip, uri);
			if (bucket != null && !bucket.tryConsume(1))
			{
				response.setStatus(429);
				response.getWriter().write("Too many requests from this IP");
				return true;
			}
			return true; // Proceed to filter chain for non-rate-limited APIs
		}
		return false;
	}

	private void setSecurityHeaders(HttpServletResponse response)
	{
		response.setHeader("X-Content-Type-Options", "nosniff");
		response.setHeader("X-Frame-Options", "DENY");
		response.setHeader("Content-Security-Policy", "default-src 'self';");
		log.info("---------- Current Response Headers ----------");
		response.getHeaderNames().forEach(headerName -> 
		log.info(headerName + ": " + response.getHeader(headerName)));
		log.info("----------------------------------------------");
	}

	private void sendUnauthorized(HttpServletResponse response, String msg) throws IOException
	{

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write("""
				{
				  "status": 401,
				  "error": "Unauthorized",
				  "message": "%s"
				}
				""".formatted(msg));
	}
}
