package com.invoice.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.invoice.binding.BuyerOrderReq;
import com.invoice.binding.OrderItemReq;
import com.invoice.exception.BusinessException;
import com.invoice.model.BuyerOrder;
import com.invoice.model.BuyerOrderItem;
import com.invoice.model.ProductItems;
import com.invoice.model.UserEntity;
import com.invoice.repo.BuyerOrderRepo;
import com.invoice.repo.ProductItemsRepo;
import com.invoice.repo.UserRepo;
import com.invoice.utils.EmailService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BuyerOrderServiceImpl implements BuyerOrderService
{
	private final ProductItemsRepo productRepo;

	private final BuyerOrderRepo buyerRepo;

	private final UserRepo userRepo;

	private final EmailService emailService;

	@Override
	@Transactional
	public String saveOrder(BuyerOrderReq req, String email)
	{

		UserEntity user = userRepo.findByEmailId(email)
				.orElseThrow(() -> new BusinessException("User not found", "Invalid user"));

		BuyerOrder order = new BuyerOrder();
		order.setBuyerName(req.getBuyerName());
		order.setBuyerAddress(req.getBuyerAddress());
		order.setBuyerEmailId(req.getBuyerEmailId());
		order.setEmailStatus('N');
		order.setCreatedBy(user.getFullName());

		BigDecimal totalAmount = BigDecimal.ZERO;

		List<BuyerOrderItem> listOrderItems = new ArrayList<>();

		for (OrderItemReq itemReq : req.getItems())
		{

			ProductItems product = productRepo.findByProductName(itemReq.getProductName());
			if (product == null)
			{
				throw new BusinessException("Product not found", itemReq.getProductName());
			}

			if (product.getProductQuantity() < itemReq.getProductQuantity())
			{
				throw new BusinessException("Insufficient stock",
						"Not enough quantity for " + product.getProductName());
			}

			// price * quantity
			BigDecimal itemTotal = product.getProductPrice().multiply(BigDecimal.valueOf(itemReq.getProductQuantity()))
					.setScale(2, RoundingMode.HALF_UP);

			BuyerOrderItem buyerOrderItem = new BuyerOrderItem();

			buyerOrderItem.setProductName(product.getProductName());
			buyerOrderItem.setProductPrice(product.getProductPrice());
			buyerOrderItem.setProductQuantity(itemReq.getProductQuantity());
			buyerOrderItem.setItemAmount(itemTotal);
			buyerOrderItem.setOrder(order); // FK mapping

			listOrderItems.add(buyerOrderItem);
			totalAmount = totalAmount.add(itemTotal);

			// reduce stock
			product.setProductQuantity(product.getProductQuantity() - itemReq.getProductQuantity());
		}

		order.setTotalAmount(totalAmount);

		BigDecimal discount = req.getTotalDiscount() == null ? BigDecimal.ZERO : req.getTotalDiscount();

		order.setTotalDiscount(discount);

		BigDecimal payableAmount = totalAmount.subtract(discount).setScale(2, RoundingMode.HALF_UP);

		order.setTotalPayableAmount(payableAmount);

		order.setItems(listOrderItems);
		buyerRepo.save(order); // CASCADE saves items

		return "Order saved successfully";
	}

	@Override
	@Transactional
	public void sendOrderReport()
	{

		String html = loadTemplate("templates/order-invoice.html");

		String subject = "Order Confirmation";
		BuyerOrder order = buyerRepo.findByEmailStatus('N')
				.orElseThrow(() -> new BusinessException("Order not found", "Invalid order ID"));

		if (order.getEmailStatus() == 'N')
		{
			// build order items rows
			StringBuilder rows = new StringBuilder();
			int i = 1;
			for (BuyerOrderItem item : order.getItems())
			{
				rows.append("<tr>").append("<td>").append(i++).append("</td>").append("<td>")
						.append(item.getProductName()).append("</td>").append("<td>₹ ").append(item.getProductPrice())
						.append("</td>").append("<td>").append(item.getProductQuantity()).append("</td>")
						.append("<td>₹ ").append(item.getItemAmount()).append("</td>").append("</tr>");
			}

			// replace placeholders
			html = html.replace("{{orderId}}", String.valueOf(order.getOrderId()));
			html = html.replace("{{buyerName}}", order.getBuyerName());
			html = html.replace("{{buyerEmail}}", order.getBuyerEmailId());
			html = html.replace("{{orderDate}}", order.getCreatedDate().toString());
			html = html.replace("{{orderItems}}", rows.toString());
			html = html.replace("{{totalAmount}}", order.getTotalAmount().toString());
			html = html.replace("{{totalDiscount}}", order.getTotalDiscount().toString());
			html = html.replace("{{payableAmount}}", order.getTotalPayableAmount().toString());

			emailService.sendEmailAsync(order.getBuyerEmailId(), subject, html);
		}

		html = html.replace("{ORDER_ID}", String.valueOf(order.getOrderId()));
		html = html.replace("{BUYER_NAME}", order.getBuyerName());
		html = html.replace("{BUYER_ADDRESS}", order.getBuyerAddress());
		html = html.replace("{TOTAL_AMOUNT}", String.valueOf(order.getTotalAmount()));
		html = html.replace("{TOTAL_DISCOUNT}", String.valueOf(order.getTotalDiscount()));
		html = html.replace("{TOTAL_PAYABLE_AMOUNT}", String.valueOf(order.getTotalPayableAmount()));

		StringBuilder itemsHtml = new StringBuilder();
		for (BuyerOrderItem item : order.getItems())
		{
			itemsHtml.append("<tr>").append("<td>").append(item.getProductName()).append("</td>").append("<td>")
					.append(item.getProductPrice()).append("</td>").append("<td>").append(item.getProductQuantity())
					.append("</td>").append("<td>").append(item.getItemAmount()).append("</td>").append("</tr>");
		}
		html = html.replace("{ORDER_ITEMS}", itemsHtml.toString());

		emailService.sendEmailAsync(order.getBuyerEmailId(), subject, html);

		order.setEmailStatus('Y');
		buyerRepo.save(order);
	}

	private String loadTemplate(String path)
	{
		try (InputStream is = new ClassPathResource(path).getInputStream())
		{
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e)
		{
			throw new RuntimeException("Unable to load template");
		}
	}

}
