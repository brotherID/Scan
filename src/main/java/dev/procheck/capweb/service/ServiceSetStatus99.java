package dev.procheck.capweb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.common.Methodes;
import dev.procheck.capweb.dao.RemiseRepository;
import dev.procheck.capweb.entities.Remise;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServiceSetStatus99 {
	
	@Autowired
	RemiseRepository remiseRepository;
	
	@Value("${nombreJour}")
    private int nombreJour;

	@Scheduled(cron ="0 0 23 * * ?")
	public void lancerService(){
		try {
			Date date=new Date();
			log.info("[SERVICE SET STATUS 99][LANCED AT]["+date+"]");
			List<Remise> remises= remiseRepository.findAllByStatus(0);
			for (Remise remise : remises) {
				Date dScan=remise.getSysDateTime();//date scan
				long diffDay=Methodes.getDifferenceDays(dScan,date);
				if(diffDay>=nombreJour) {
					remise.setStatus(99);
					remiseRepository.save(remise);
					log.info("[REMISE]["+remise.getIdRemise()+"]["+dScan+"]["+date+"]["+diffDay+"]");
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("[SET STATUS 99]",e);
		}
	}
}
