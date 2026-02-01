package com.invoice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.binding.ProductCategoryReq;
import com.invoice.service.ProductCategoryService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/cat")
@AllArgsConstructor
public class ProductCategoryController
{
	private ProductCategoryService service;
	
	
	
	@PostMapping("/add")
	public ResponseEntity<String> addCategory(@RequestBody  ProductCategoryReq category, @AuthenticationPrincipal UserDetails user)
	{
		service.saveProductCategory(category, user.getUsername());
		
		return new ResponseEntity<>("Save",HttpStatus.ACCEPTED);
	}
}
