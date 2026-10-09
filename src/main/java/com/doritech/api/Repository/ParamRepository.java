package com.doritech.api.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ParamEntityPk;

@Repository
@EnableJpaRepositories
public interface ParamRepository extends JpaRepository<ParamEntity, String>{
	
	@Query(value = "SELECT * FROM Param_Entity where code = :code and serial = :serial", nativeQuery = true)
	List<ParamEntity> getParamDataByCodeAndSerial(String code, String serial);
	
	ParamEntity findByCode(String code);
	List<ParamEntity> findAllByCode(String code);
	@Query("SELECT p FROM ParamEntity p WHERE p.code = :code AND p.serial IN :serials")
	List<ParamEntity> getParamsByCodeAndSerials(
	        @Param("code") String code,
	        @Param("serials") List<String> serials);
	ParamEntity getParamByCodeAndSerial(String code,String serial);
	
	List<ParamEntity> findBySerial(String serial);

	ParamEntity findByCodeAndSerial(String code, String serialNo);
	
	ParamEntity getParamByCodeAndSerialAndDescp1(String code,String serial,String descp1);

	List<ParamEntity> findByDescp1(String descp1);

	Optional<ParamEntity> findFirstBySerial(String serial);

}
