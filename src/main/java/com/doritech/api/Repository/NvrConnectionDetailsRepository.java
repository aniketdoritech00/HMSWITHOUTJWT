package com.doritech.api.Repository;

import com.doritech.api.Entity.NvrConnectionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NvrConnectionDetailsRepository
        extends JpaRepository<NvrConnectionDetails, Long> {

	NvrConnectionDetails findByNvr(String nvrId);

	boolean existsByNvr(String nvrId);

	void deleteByNvr(String nvrId);
}