package com.invoice.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.invoice.exception.BusinessException;
import com.invoice.model.GstTax;
import com.invoice.model.ProductItems;
import com.invoice.model.UserEntity;
import com.invoice.repo.GstRepo;
import com.invoice.repo.ProductItemsRepo;
import com.invoice.repo.UserRepo;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UpdateGst
{
	private final ProductItemsRepo productRepo;

	private final UserRepo userRepo;

	private final GstRepo gstRepo;
	private static Logger log = org.slf4j.LoggerFactory.getLogger(UpdateGst.class);

	@Transactional

	public void updateGstForProducts(String email)
	{
		UserEntity user = userRepo.findByEmailId(email)
				.orElseThrow(() -> new BusinessException("User not found", "Not Found"));

		List<ProductItems> products = productRepo.findAll();

		for (ProductItems item : products)
		{
			// Find GST by HSN Code
			Optional<GstTax> gst = gstRepo.findByHsnCode(item.getHsnCode());

			if (gst.isPresent())
			{
				// If found, update the data
				double total = gst.get().getCGst() + gst.get().getSGst() + gst.get().getIGst();
				item.setTotalGst(Math.round(total));
				item.setUpdatedBy(user.getFullName());
			}
			// If not found, just log a warning and skip to next iteration
			log.warn("Skipping Product ID: {}. GST details not found for HSN: {}", item.getProductId(),
					item.getHsnCode());

		}
		productRepo.saveAll(products);
	}

}
