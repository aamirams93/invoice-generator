package com.invoice.binding;

public interface OrderReportDTO
{
	public Long getOrderId();

	public String getBuyerAddress();

	public String getBuyerName();

	public String getCreatedDate();

	public Double getTotalAmount();

	public Double getTotalDiscount();

	public Double getTotalPayableAmount();

	public Long getItemId();

	public String getProductName();

	public Double getProductPrice();

	public Integer getProductQuantity();

	public Double getItemAmount();
}
