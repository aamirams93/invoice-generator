package com.invoice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.service.UpdateGst;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class update
{
	private final UpdateGst updateGst;;
	
	@GetMapping("/update")
	ResponseEntity<String> updateGstForProducts(@AuthenticationPrincipal UserDetails email)
	{
		updateGst.updateGstForProducts(email.getUsername());
		return ResponseEntity.ok("GST details updated successfully for all products.");
	}
}
