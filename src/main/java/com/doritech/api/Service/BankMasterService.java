package com.doritech.api.Service;

import com.doritech.api.DTO.BankMasterDTO;
import com.doritech.api.DTO.BankMasterRequestDTO;
import com.doritech.api.Entity.BankMasterEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Repository.BankMasterRepository;
import com.doritech.api.Repository.ParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankMasterService {

	@Autowired
	private BankMasterRepository bankMasterRepository;

	@Autowired
	private ParamRepository paramRepository;

	@Autowired
	private ParamService paramService;

	private String generateBankCode() {
		ParamEntity paramEntity = paramRepository.findByCode("BANK_ID");
		if (paramEntity == null) {
			throw new RuntimeException("Param configuration not found for BANK_ID");
		}
		String descp1 = paramEntity.getDescp1();
		String descp2 = paramEntity.getDescp2();
		String newBankCode = descp1 + descp2;

		// Update param after generating code
		paramService.updateBranchDescp2Value(paramEntity);

		return newBankCode;
	}

	@Transactional
	public BankMasterDTO saveBank(BankMasterRequestDTO requestDTO) {
		// Check if bank code generation is valid
		String bankCode = generateBankCode();

		// Optional: Check if bank code already exists (though it shouldn't with proper
		// generation)
		if (bankMasterRepository.existsByBankCode(bankCode)) {
			throw new RuntimeException("Bank code already exists: " + bankCode);
		}

		BankMasterEntity entity = new BankMasterEntity();
		entity.setBankCode(bankCode);
		entity.setBankName(requestDTO.getBankName());
		BankMasterEntity savedEntity = bankMasterRepository.save(entity);

		return convertToDTO(savedEntity);
	}

	@Transactional
	public BankMasterDTO updateBank(String bankCode, BankMasterRequestDTO requestDTO) {
		BankMasterEntity existingEntity = bankMasterRepository.findByBankCode(bankCode)
				.orElseThrow(() -> new RuntimeException("Bank not found with code: " + bankCode));

		existingEntity.setBankName(requestDTO.getBankName());
		BankMasterEntity updatedEntity = bankMasterRepository.save(existingEntity);

		return convertToDTO(updatedEntity);
	}

	public BankMasterDTO getBankByCode(String bankCode) {
		BankMasterEntity entity = bankMasterRepository.findByBankCode(bankCode)
				.orElseThrow(() -> new RuntimeException("Bank not found with code: " + bankCode));
		return convertToDTO(entity);
	}

	public List<BankMasterDTO> getAllBanks() {
		List<BankMasterEntity> entities = bankMasterRepository.findAll();
		return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Transactional
	public void deleteBank(String bankCode) {
		BankMasterEntity entity = bankMasterRepository.findByBankCode(bankCode)
				.orElseThrow(() -> new RuntimeException("Bank not found with code: " + bankCode));
		bankMasterRepository.delete(entity);
	}

	private BankMasterDTO convertToDTO(BankMasterEntity entity) {
		BankMasterDTO dto = new BankMasterDTO();
		dto.setBankCode(entity.getBankCode());
		dto.setBankName(entity.getBankName());
		return dto;
	}
}