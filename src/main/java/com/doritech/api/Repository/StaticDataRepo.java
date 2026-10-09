package com.doritech.api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.StaticDataEntity;

@Repository
@EnableJpaRepositories
public interface StaticDataRepo extends JpaRepository<StaticDataEntity, Long> {

	@Query(value = "select descp1, descp2, descp3 from static_data where code = :code and serial = :serial", nativeQuery = true)
	List<Object[]> getMessageTemplates(String code, String serial);

	@Query(value = "select descp1 from static_data where code = :code", nativeQuery = true)
	List<Object[]> getStaticDataByCode(String code);

	@Query(value = "select serial from static_data where code = :code and descp1 = :descp1", nativeQuery = true)
	String getStaticDataByDescp1(String code, String descp1);

	@Query(value = "select descp1 from static_data where code = :code and serial=:serial", nativeQuery = true)
	String getDescp1ByCode(String code, String serial);

}
