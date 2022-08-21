package dev.procheck.capweb.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.procheck.capweb.entities.Remise;

public interface RemiseRepository extends JpaRepository<Remise, String>{

	@Query("SELECT r from Remise r WHERE r.idRemise = :x")
	public List<Remise> getRemiseById(@Param("x") String id);
	
	@Query("SELECT r from Remise r WHERE r.capturePoint = :x")
	public Page<Remise> getRemiseByCapturePointOld(@Param("x") String capturePoint, Pageable p);
	
	@Query("SELECT r from Remise r WHERE r.capturePoint = :x and  (r.ribRemettant like %:y% or nomRemettant like %:y% or r.typeValeur like %:y% or r.referenceRemise like %:y%) ")
	public Page<Remise> getRemiseByCapturePointAndMotCleOld(@Param("x") String capturePoint,@Param("y")String motCle,Pageable p);
	
	@Query("SELECT r from Remise r WHERE r.bankCode= :bq and r.capturePoint = :x and convert(date,r.dateRemise) = :z  and r.typeValeur like %:tv% and (r.ribRemettant like %:y% or nomRemettant like %:y% or r.referenceRemise like %:y%) ")
	public Page<Remise> getRemiseByCapturePointAndMotCleAndDateRemise(@Param("bq")String bankCode,@Param("tv")String typeValeur,@Param("x") String capturePoint,@Param("y")String motCle,@Param("z")Date dateReimse,Pageable p);
	
	@Query("SELECT r from Remise r WHERE r.bankCode= :bq and  r.capturePoint = :x and convert(date,r.sysDateTime) = :z  and r.typeValeur like %:tv% and (r.ribRemettant like %:y% or nomRemettant like %:y% or r.referenceRemise like %:y%) ")
	public Page<Remise> getRemiseByCapturePointAndMotCleAndDateScan(@Param("bq")String bankCode,@Param("tv")String typeValeur,@Param("x") String capturePoint,@Param("y")String motCle,@Param("z")Date dateScan,Pageable p);
	
	@Query("SELECT r from Remise r WHERE  (r.capturePoint like %:y% or r.ribRemettant like %:y% or nomRemettant like %:y% or r.typeValeur like %:y% or r.referenceRemise like %:y%) ")
	public Page<Remise> findAllSortByIdRemiseAndMotCleOld(@Param("y")String motCle,Pageable p);
	
	@Query("SELECT r from Remise r WHERE convert(date,r.dateRemise) = :z and r.typeValeur like %:tv% and (r.capturePoint like %:y% or r.ribRemettant like %:y% or nomRemettant like %:y% or r.referenceRemise like %:y%) ")
	public Page<Remise> findAllSortByIdRemiseAndMotCleAndDateRemise(@Param("tv")String typeValeur,@Param("y")String motCle,@Param("z")Date dateReimse,Pageable p);
	
	@Query("SELECT r from Remise r WHERE convert(date,r.sysDateTime) = :z and r.typeValeur like %:tv% and (r.capturePoint like %:y% or r.ribRemettant like %:y% or nomRemettant like %:y% or r.referenceRemise like %:y%) ")
	public Page<Remise> findAllSortByIdRemiseAndMotCleAndDateScan(@Param("tv")String typeValeur,@Param("y")String motCle,@Param("z")Date dateScan,Pageable p);
	
	//public boolean existsByToken(String token);
	
	public List<Remise> findAllByStatus(int status);
	
	@Query("SELECT r from Remise r WHERE convert(date,r.sysDateTime) = :z and  r.user.idUser = :u")
	public List<Remise> findAllByIdUserAndDateScanOld(@Param("u")String idUser,@Param("z")Date dateScan);
	
}
