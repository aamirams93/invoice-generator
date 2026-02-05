package com.invoice.repo;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.model.BuyerOrder;
import com.invoice.binding.OrderReportDTO;

public interface BuyerOrderRepo extends JpaRepository<BuyerOrder, Serializable>
{
	public Optional<BuyerOrder> findByEmailStatus(char emailStatus);
	
	 @Query(value = """
		        SELECT 
		            b.order_id AS orderId,
		            b.buyer_address AS buyerAddress,
		            b.buyer_name AS buyerName,
		            CAST(b.created_date AS DATE),
		            b.total_amount AS totalAmount,
		            b.total_discount AS totalDiscount,
		            b.total_payable_amount AS totalPayableAmount,
		            i.item_id AS itemId,
		            i.product_name AS productName,
		            i.product_price AS productPrice,
		            i.product_quantity AS productQuantity,
		            i.item_amount AS itemAmount
		        FROM tbl_buyer_order b
		        INNER JOIN tbl_buyer_order_item i
		            ON b.order_id = i.order_id
		        """, nativeQuery = true)
		    List<OrderReportDTO> fetchOrderReport();
}
