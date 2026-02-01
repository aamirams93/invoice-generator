package com.invoice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.invoice.security.filter.AppFilter;
import com.invoice.service.MyUserDetailsService;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class AppSecurityConfig
{

	private final AppFilter filter;
	private final MyUserDetailsService userDtlsSvc;
	
	// ---------------- PASSWORD ENCODER ----------------

	@Bean
	 PasswordEncoder passwordEncoder()
	{
		return new Argon2PasswordEncoder(
				16, 		// salt length
				32, 		// hash length
				1, 			// parallelism
				1 << 16, 	// memory (64MB)
				4 			//iterations
		);
	}

	// ----------------🪪🔗 SECURITY FILTER CHAIN ----------------

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{

		http
		
				.csrf(csrf -> csrf.disable())

				// Enforce HTTPS in production
				.requiresChannel(channel -> channel.anyRequest().requiresSecure())

				// CORS
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))

				// Stateless session (JWT)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Authorization rules
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/v1/auth/add", "/api/v1/auth/login", "/api/v1/auth/motp",
								"/api/v1/auth/welcome")
						
						.permitAll()
						.requestMatchers("/api/v1/logout").hasRole("USER")

						.anyRequest().authenticated())

				.authenticationProvider(authenticationProvider())

				// JWT filter
				.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)

				// Security headers
				.headers(headers -> headers.frameOptions(frame -> frame.deny()).contentTypeOptions(content -> {
				}).httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)));

		return http.build();
	}
	
	

	// ---------------- AUTH PROVIDER ----------------
// 
	@Bean
	AuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDtlsSvc);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	// ---------------- AUTH MANAGER ----------------

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception 
	{
		return config.getAuthenticationManager();
	}

	// ---------------- CORS CONFIG ----------------

	@Bean
	CorsConfigurationSource corsConfigurationSource()
	{

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of(
		
		 "https://localhost:5173",
		 "https://localhost:8443", 
		 "http://localhost:8080",
		  "http://localhost:5173"
		 

		));

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		// JWT in headers → no credentials needed
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		
		  // Explicitly allow Swagger endpoints
	    source.registerCorsConfiguration("/v3/api-docs/**", configuration);
	    source.registerCorsConfiguration("/swagger-ui/**", configuration);
	    source.registerCorsConfiguration("/swagger-ui.html", configuration);
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
