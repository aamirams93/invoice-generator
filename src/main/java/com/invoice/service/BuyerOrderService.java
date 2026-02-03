package com.invoice.service;

import com.invoice.binding.BuyerOrderReq;

public interface BuyerOrderService
{
	public String saveOrder(BuyerOrderReq req,String email);
	public void sendOrderReport();
}
