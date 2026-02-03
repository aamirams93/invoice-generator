package com.invoice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.binding.BuyerOrderReq;
import com.invoice.service.BuyerOrderService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class BuyerOrderController
{
	private BuyerOrderService service;

	@PostMapping("/buy")
	public ResponseEntity<String> addCategory(@RequestBody BuyerOrderReq order,
			@AuthenticationPrincipal UserDetails user)
	{
		service.saveOrder(order, user.getUsername());

		return new ResponseEntity<>("Save", HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/send")
	public ResponseEntity<String> sendInvoce()
	{
		service.sendOrderReport();
		return new ResponseEntity<>("sent", HttpStatus.ACCEPTED);
	}
	
	
}
