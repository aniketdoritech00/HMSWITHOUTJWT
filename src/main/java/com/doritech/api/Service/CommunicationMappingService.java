package com.doritech.api.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.CommunicationMappingDTO;
import com.doritech.api.Entity.CommunicationMapping;
import com.doritech.api.Repository.CommunicationMappingRepository;



@Service
public class CommunicationMappingService {

	@Autowired
	private CommunicationMappingRepository
	communicationRepository;

	public CommunicationMappingDTO createCommunication(CommunicationMappingDTO dto)
	        throws InvocationTargetException, IllegalAccessException {

	    // ✅ Take only first panel serial number and clean it
	    if (dto.getPanelSerialNo() != null && !dto.getPanelSerialNo().isBlank()) {
	        String cleanedSerialNo = dto.getPanelSerialNo()
	                .split(",")[0]      // take first value only
	                .replaceAll("#", "")// remove ##
	                .trim();            // remove spaces

	        dto.setPanelSerialNo(cleanedSerialNo);
	    }

	    CommunicationMapping entity = new CommunicationMapping();
	    BeanUtils.copyProperties(entity, dto);

	    CommunicationMapping savedEntity = communicationRepository.save(entity);

	    CommunicationMappingDTO responseDto = new CommunicationMappingDTO();
	    BeanUtils.copyProperties(responseDto, savedEntity);
	    return responseDto;
	}


	public List<CommunicationMappingDTO> getAllCommunications() throws InvocationTargetException, IllegalAccessException {
		List<CommunicationMapping> entities = communicationRepository.findAll();
		List<CommunicationMappingDTO> dtoList = new ArrayList<>();

		for (CommunicationMapping entity : entities) {
			CommunicationMappingDTO dto = new CommunicationMappingDTO();
			BeanUtils.copyProperties(dto, entity);
			dtoList.add(dto);
		}

		return dtoList;
	}

	public CommunicationMappingDTO getCommunicationById(Long id) throws InvocationTargetException, IllegalAccessException {
		Optional<CommunicationMapping> optionalEntity = communicationRepository.findById(id);

		if (optionalEntity.isPresent()) {
			CommunicationMappingDTO dto = new CommunicationMappingDTO();
			BeanUtils.copyProperties(dto, optionalEntity.get());
			return dto;
		}

		return null;
	}

	public CommunicationMappingDTO updateCommunication(Long id, CommunicationMappingDTO dto)
			throws InvocationTargetException, IllegalAccessException {
		Optional<CommunicationMapping> optionalEntity = communicationRepository.findById(id);

		if (optionalEntity.isPresent()) {
			CommunicationMapping entity = optionalEntity.get();
			BeanUtils.copyProperties(entity, dto);
			entity.setId(id);

			CommunicationMapping updatedEntity = communicationRepository.save(entity);

			CommunicationMappingDTO responseDto = new CommunicationMappingDTO();
			BeanUtils.copyProperties(responseDto, updatedEntity);
			return responseDto;
		}

		return null;
	}

	public boolean deleteCommunication(Long id) {
		if (communicationRepository.existsById(id)) {
			communicationRepository.deleteById(id);
			return true;
		}
		return false;
	}
}