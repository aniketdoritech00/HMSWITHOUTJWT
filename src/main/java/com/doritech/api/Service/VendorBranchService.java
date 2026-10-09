package com.doritech.api.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doritech.api.DTO.ProductChecklistDTO;
import com.doritech.api.DTO.VendorBranchMappedDTO;
import com.doritech.api.Entity.ProductChecklistEntity;
import com.doritech.api.Entity.VendorBranchMappedEntity;
import com.doritech.api.Repository.VendorBranchRepository;

@Service
public class VendorBranchService {

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private VendorBranchRepository vendorBranchRepo;

	public VendorBranchMappedEntity saveVendorBranch(VendorBranchMappedDTO dto) throws IOException {
		VendorBranchMappedEntity entity = new VendorBranchMappedEntity();
		String uploadDir = servletContext.getRealPath("/uploads/");
		Files.createDirectories(Paths.get(uploadDir));

		entity.setId(dto.getId());
		entity.setBranchCode(dto.getBranchCode());
		entity.setBranchName(dto.getBranchName());
		entity.setBranchContact(dto.getBranchContact());
		entity.setSystemIntegrator(dto.getSystemIntegrator());
		entity.setProductType(dto.getProductType());
		List<ProductChecklistEntity> productEntities = dto.getProducts().stream().map(productDTO -> {
			ProductChecklistEntity product = new ProductChecklistEntity();
			product.setId(productDTO.getId());
			product.setQuestion(productDTO.getQuestion());
			product.setAnswer(productDTO.getAnswer());
			product.setQuestionType(productDTO.getQuestionType());
			product.setVendorBranch(entity);

			String file = productDTO.getImagePath(); 
			if (file != null && !file.isEmpty()) {
				byte[] bytes = file.getBytes();
				String base64String = Base64.getEncoder().encodeToString(bytes);
				product.setImagePath(base64String);
			}

			return product;
		}).collect(Collectors.toList());

		entity.setProduct(productEntities);
		return vendorBranchRepo.save(entity);
	}

	

	public List<VendorBranchMappedDTO> getAllVendorBranches() {
		return vendorBranchRepo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public VendorBranchMappedDTO getVendorBranchById(int id) {
		return vendorBranchRepo.findById(id).map(this::convertToDTO).orElse(null);
	}

	private VendorBranchMappedDTO convertToDTO(VendorBranchMappedEntity entity) {
		VendorBranchMappedDTO dto = new VendorBranchMappedDTO();
		dto.setId(entity.getId());
		dto.setBranchCode(entity.getBranchCode());
		dto.setBranchName(entity.getBranchName());
		dto.setBranchContact(entity.getBranchContact());
		dto.setSystemIntegrator(entity.getSystemIntegrator());
		dto.setProductType(entity.getProductType());
		List<ProductChecklistDTO> products = entity.getProduct().stream().map(productEntity -> {
			ProductChecklistDTO productDTO = new ProductChecklistDTO();
			productDTO.setId(productEntity.getId());
			productDTO.setQuestion(productEntity.getQuestion());
			productDTO.setAnswer(productEntity.getAnswer());
			productDTO.setImagePath(productEntity.getImagePath()); 
			productDTO.setQuestionType(productEntity.getQuestionType());
			return productDTO;
		}).collect(Collectors.toList());

		dto.setProducts(products);
		return dto;
	}

}
