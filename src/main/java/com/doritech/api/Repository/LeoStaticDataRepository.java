package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.LeoStaticDataEntity;

@Repository
public interface LeoStaticDataRepository extends JpaRepository<LeoStaticDataEntity, Integer> {

	@Query(value = "SELECT * FROM LEO_STATIC_DATA WHERE message_code IN (:messageCodes)", nativeQuery = true)
	List<LeoStaticDataEntity> getByMessageCodes(@Param("messageCodes") List<String> messageCodes);


	
}
