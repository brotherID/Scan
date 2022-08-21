package dev.procheck.capweb.controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import dev.procheck.capweb.common.Methodes;
import dev.procheck.capweb.dao.RemiseRepository;
import dev.procheck.capweb.dao.UserHistoRepository;
import dev.procheck.capweb.dao.UserRepository;
import dev.procheck.capweb.dao.ValeurRepository;
import dev.procheck.capweb.entities.Remise;
import dev.procheck.capweb.entities.User;
import dev.procheck.capweb.entities.UserHisto;
import dev.procheck.capweb.entities.Valeur;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ValeurController {
	
	@Autowired
	private ValeurRepository valeurRepository;
	@Autowired
	private RemiseRepository remiseRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserHistoRepository userHistoRepository;
	
	
	@GetMapping("/valeur/{idRemise}/{id}")
	public String delete(HttpServletRequest req,@PathVariable String id,@PathVariable String idRemise) throws IOException {
		
		String id_user = (String)req.getSession().getAttribute("userid");
		if(id_user!=null) {
			Remise remise = remiseRepository.findById(idRemise).get();
			User u=userRepository.findById(id_user).get();
			if(u.getRole().equals("ADMIN")||remise.getCapturePoint().equals(u.getCapturePoint())) {
				try {
					Valeur valeur= valeurRepository.getOne(id);
					valeur.getRemise().setScanNbValeur(valeur.getRemise().getScanNbValeur()-1);
					Files.deleteIfExists(Paths.get(valeur.getUrlRecto()));
					Files.deleteIfExists(Paths.get(valeur.getUrlVerso()));
					valeurRepository.deleteById(id);
					
					int randomHisto = Methodes.GenerateRandom();
					String idHisto = Methodes.getId() + (String.valueOf(randomHisto).substring(0, 2));
					Date date_action = new Date();
					UserHisto userHisto = new UserHisto(idHisto, date_action, "Suppression valeur", "Suppression de la valeur: "+id,u);
					userHistoRepository.save(userHisto);
					
					InetAddress ipHost = InetAddress.getLocalHost();
					String ip = ipHost.getHostAddress();
					log.info("Suppresion de la valeur: "+id+" par l'utilisateur: "+u.getUserName()+" depuis l'adresse: "+ip);
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				return "redirect:/continueScan?idRemise="+idRemise;
			}
			else {
				return "redirect:/remises";
			}
			
		}
		else {
			return	"redirect:/login";
		}
	}
	
	@GetMapping(value = "/scanCheque")
	public String scanCheque(HttpServletRequest req) {
		
		String id_user = (String)req.getSession().getAttribute("userid");
		if(id_user!=null) {
			return "redirect:/remises";
		}
		else {
			return	"redirect:/login";
		}
	}
	@GetMapping(value = "/scanUrl")
	public String scanUrl(HttpServletRequest req) {
		
		String id_user = (String)req.getSession().getAttribute("userid");
		if(id_user!=null) {
			return "redirect:/remises";
		}
		else {
			return	"redirect:/login";
		}
	}

}
