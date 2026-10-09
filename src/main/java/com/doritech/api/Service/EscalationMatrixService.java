package com.doritech.api.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.EscalationMatrixDTO;
import com.doritech.api.Entity.EscalationMatrix;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.EscalationMatrixRepository;

@Service
public class EscalationMatrixService {

	@Autowired
	private EscalationMatrixRepository repository;

	// SAVE
	public ResponseEntity save(EscalationMatrixDTO dto) {

		ResponseEntity response = new ResponseEntity();

		try {

			EscalationMatrix matrix = mapToEntity(dto);

			EscalationMatrix saved = repository.save(matrix);

			response.setMessage("Escalation Matrix Saved Successfully");
			response.setStatusCode(200);
			response.setPayload(saved);

		} catch (Exception e) {
			response.setMessage("Failed To Save");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

	// UPDATE
	public ResponseEntity update(Long id, EscalationMatrixDTO dto) {

		ResponseEntity response = new ResponseEntity();

		try {

			Optional<EscalationMatrix> optional = repository.findById(id);

			if (!optional.isPresent()) {
				response.setMessage("Record Not Found");
				response.setStatusCode(404);
				return response;
			}

			EscalationMatrix matrix = optional.get();
			matrix.setEscLevel(dto.getEscLevel());
			matrix.setEscTimeRange(dto.getEscTimeRange());
			matrix.setRecipient(dto.getRecipient());

			matrix.setWhatsapp(dto.getWhatsapp());
			matrix.setSms(dto.getSms());
			matrix.setCallAlert(dto.getCallAlert());
			matrix.setMail(dto.getMail());

			matrix.setCriticalFlag(dto.getCriticalFlag());

			matrix.setCcMail1(dto.getCcMail1());
			matrix.setCcMail2(dto.getCcMail2());
			matrix.setCcMail3(dto.getCcMail3());
			matrix.setCcMail4(dto.getCcMail4());

			matrix.setNoOfReminder(dto.getNoOfReminder());
			matrix.setReminderTime(dto.getReminderTime());

			EscalationMatrix updated = repository.save(matrix);

			response.setMessage("Updated Successfully");
			response.setStatusCode(200);
			response.setPayload(updated);

		} catch (Exception e) {
			response.setMessage("Update Failed");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

	// GET BY ID
	public ResponseEntity getById(Long id) {

		ResponseEntity response = new ResponseEntity();

		try {

			Optional<EscalationMatrix> optional = repository.findById(id);

			if (!optional.isPresent()) {
				response.setMessage("Record Not Found");
				response.setStatusCode(404);
				return response;
			}

			response.setMessage("Data Found");
			response.setStatusCode(200);
			response.setPayload(optional.get());

		} catch (Exception e) {
			response.setMessage("Failed");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

	// GET ALL
	public ResponseEntity getAll(int page, int size) {

		ResponseEntity response = new ResponseEntity();

		try {

			Page<EscalationMatrix> result = repository.findAll(PageRequest.of(page, size));

			response.setMessage("Data Found");
			response.setStatusCode(200);
			response.setPayload(result.getContent());

			response.setTotalElements(result.getTotalElements());
			response.setTotalPages(result.getTotalPages());
			response.setCurrentPage(result.getNumber());

		} catch (Exception e) {
			response.setMessage("Failed");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

	// =========================
	// MAPPER METHOD
	// =========================
	private EscalationMatrix mapToEntity(EscalationMatrixDTO dto) {

		EscalationMatrix matrix = new EscalationMatrix();

		matrix.setEscLevel(dto.getEscLevel());
		matrix.setEscTimeRange(dto.getEscTimeRange());
		matrix.setRecipient(dto.getRecipient());

		matrix.setWhatsapp(dto.getWhatsapp());
		matrix.setSms(dto.getSms());
		matrix.setCallAlert(dto.getCallAlert());
		matrix.setMail(dto.getMail());

		matrix.setCriticalFlag(dto.getCriticalFlag());

		matrix.setCcMail1(dto.getCcMail1());
		matrix.setCcMail2(dto.getCcMail2());
		matrix.setCcMail3(dto.getCcMail3());
		matrix.setCcMail4(dto.getCcMail4());

		matrix.setNoOfReminder(dto.getNoOfReminder());
		matrix.setReminderTime(dto.getReminderTime());

		return matrix;
	}
}