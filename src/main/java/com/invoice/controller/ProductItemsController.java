package com.invoice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.binding.ProductItemsReq;
import com.invoice.service.ProductService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/product")
@AllArgsConstructor
public class ProductItemsController
{
	private ProductService service;
	
	
	
	@PostMapping("/add")
	public ResponseEntity<String> addCategory(@RequestBody  ProductItemsReq plan, @AuthenticationPrincipal UserDetails user)
	{
		service.savePlan(plan, user.getUsername());
		
		return new ResponseEntity<>("Save",HttpStatus.ACCEPTED);
	}
}
