package com.doritech.api.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.doritech.api.Repository.DVRRepository;

@Service
public class DVRService {
    
    private final DVRRepository dvrRepository;
  
    public DVRService(DVRRepository dvrRepository) {
		super();
		this.dvrRepository = dvrRepository;
	}

	public List<String> getDistinctPanelSerials() {
        return dvrRepository.findDistinctPanelSerials();
    }
}