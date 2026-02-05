package com.invoice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.binding.LoginUserRequest;
import com.invoice.binding.SuperUser;
import com.invoice.binding.UserAddingData;
import com.invoice.binding.UserData;
import com.invoice.config.UserSessionService;
import com.invoice.security.service.JwtService;
import com.invoice.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerRestController 
{

	private final AuthenticationManager authManager;

	private final JwtService jwt;

	private final UserService userService;

	private final UserSessionService session;
	
	private static final Logger log = LoggerFactory.getLogger(CustomerRestController.class);


	@GetMapping("/welcome")
	@PreAuthorize("hasRole('USER')")
	public String welcome()
	{
		return "welcome user";
	}
	
	
	
	

//	@PostMapping("/login")
//	public ResponseEntity<Object> login(@RequestBody LoginUserRequest c,HttpServletRequest request,HttpServletResponse response)
//	{
//		try
//		{
//			Authentication auth = authManager
//					.authenticate(new UsernamePasswordAuthenticationToken(c.getEmailId(), c.getPassword()));
//
//			if (auth.isAuthenticated())
//			{
//
//				// Generate JWT
//				String accesToken = jwt.generateAccesToken(c.getEmailId());
//	            String refreshToken = jwt.generateRefreshToken(c.getEmailId());
//	            
//	            ResponseCookie refrehCokkie = ResponseCookie.from("refreshToken",refreshToken)
//	            		.httpOnly(true)
//	            		.secure(true)
//	            		.path("/api/v1/auth/refresh")
//	            		.maxAge(7 * 24 * 60 * 60)
//	            		.sameSite("Strict")
//	            		.build();
//	            response.addHeader(HttpHeaders.SET_COOKIE, refrehCokkie.toString());
//				// Log login info correctly
//				String clientIp = session.getClientIp(request);
//				userService.logLoginSuccess(c.getEmailId(),clientIp);
//
//				return new ResponseEntity<>(accesToken, HttpStatus.OK);
//			}
//
//		} catch (BadCredentialsException ex)
//		{
//			userService.isLoginBlocked(c.getEmailId());
//			return new ResponseEntity<> ("Invalid credentials",HttpStatus.UNAUTHORIZED);
//		}
//
//		return new ResponseEntity<>("Login failed", HttpStatus.BAD_REQUEST);
//	}
	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginUserRequest c, HttpServletRequest request, HttpServletResponse response) {
	    try {
	        Authentication auth = authManager.authenticate(
	            new UsernamePasswordAuthenticationToken(c.getEmailId(), c.getPassword())
	        );
	        List<String> roles = auth.getAuthorities()
	                .stream()
	                .map(GrantedAuthority::getAuthority)
	                .toList();

	        if (auth.isAuthenticated()) {
	            String accessToken = jwt.generateAccesToken2(c.getEmailId(),roles);
	            String refreshToken = jwt.generateRefreshToken(c.getEmailId());

	            // httpOnly refresh cookie
	            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
	                    .httpOnly(true)
	                    .secure(true)
	                    .path("/api/v1/auth/refresh")
	                    .maxAge(7 * 24 * 60 * 60)
	                    .sameSite("Strict")
	                    .build();
	            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

	            // log success
	            String clientIp = session.getClientIp(request);
	            userService.logLoginSuccess(c.getEmailId(), clientIp);

	            // return JSON containing token
	            Map<String, Object> resp = new HashMap<>();
	            resp.put("accessToken", accessToken);
	            resp.put("message", "Login successful");
	            return ResponseEntity.ok(resp);
	        }

	    } catch (BadCredentialsException ex) {
	        userService.isLoginBlocked(c.getEmailId());
		    log.info("Invalid credentials", HttpStatus.UNAUTHORIZED);
	        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
	    }
	    log.info("Login failed");
	    return new ResponseEntity<>("Login failed", HttpStatus.BAD_REQUEST);
	}




	@PostMapping("/add")
	public ResponseEntity<String> addUser(@RequestBody UserAddingData user,HttpServletRequest request)
	{
		    String clientIp = session.getClientIp(request);
			 userService.saveUser(user,clientIp);
		return new ResponseEntity<>("Account Created Successfully",HttpStatus.ACCEPTED);

	}
	
	
	
	@PostMapping("/super")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> addUser(@RequestBody SuperUser user,HttpServletRequest request)
	{
		    String clientIp = session.getClientIp(request);
			 userService.saveSuperUser (user,clientIp);
		return new ResponseEntity<>("Account Created Successfully",HttpStatus.ACCEPTED);

	}


	@GetMapping("/alluser")
	public ResponseEntity<List<UserData>> gettAllUser()
	{
		List<UserData> allUser = userService.getAllUsers();

		return new ResponseEntity<>(allUser, HttpStatus.OK);
	}

	
	@PutMapping("/motp")
	public ResponseEntity<Void> otpEmailGen(@RequestBody UserData email) {
	    userService.generateEmailOtp(email);
	   // return ResponseEntity.ok().header("X-Success-Message", "OTP sent successfully").build();
	    
	    return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	@PostMapping("/refresh")
	public ResponseEntity<Object> refresh(@CookieValue("refreshToken") String refreshToken) {
	    String email = jwt.extractUsername(refreshToken);
	    jwt.generateAccesToken(email);

	    return new ResponseEntity<Object>("", HttpStatus.OK);
	}
	




}
