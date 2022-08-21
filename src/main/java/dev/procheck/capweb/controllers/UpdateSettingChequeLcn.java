package dev.procheck.capweb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import dev.procheck.capweb.dao.TypeRemiseRepository;
import dev.procheck.capweb.dao.TypeValeurRepository;
import dev.procheck.capweb.dao.UserRepository;
import dev.procheck.capweb.entities.TypeRemise;
import dev.procheck.capweb.entities.TypeValeur;
import dev.procheck.capweb.entities.User;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UpdateSettingChequeLcn {
	
	@Autowired
	public TypeRemiseRepository typeRemiseRepository;
	
	@Autowired
	public TypeValeurRepository typeValeurRepository;
	
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public UserController userController;
	
	@PostMapping("/updateSettingChequeLcn")
	public String updateSettingChequeLcn(HttpServletRequest req, Model model) throws IOException
	{
		log.info("****************begin updateSettingChequeLcn**********************");
				  String userId = req.getParameter("userIDParametreCheque");
				  log.info("userId {}",userId);
				  List<String> idTypeRemisesFromDB  = getIdTypeRemisesFromDB(userId);
				  List<String> idTypeRemisesSelected  = getIdTypeRemisesSelected(req);
				  List<String>  idTypeValeursFromDB = getIdTypeValeursFromDB(userId);
				  List<String>  idTypeValeursSelected = getIdTypeValeursSelected(req);
				  if(idTypeRemisesFromDB.equals(idTypeRemisesSelected) && idTypeValeursFromDB.equals(idTypeValeursSelected)) 
				  {
				      model.addAttribute("fail", true); 
				      model.addAttribute("success", false);
				  } 
				  else 
				  { 
					  User user = getUserFromDB(userId);
					  updateTypeValeurToUser(req,user,idTypeValeursSelected);
					  updateTypeRemiseToUser(req,user,idTypeRemisesSelected);
					  model.addAttribute("success", true);
					  model.addAttribute("fail", false);
				  } 
				  userController.listUsers(req, null, model, 0, "", "GR", 20);
		log.info("****************end updateSettingChequeLcn**********************");
		return "listUsers";
	}
	private User getUserFromDB(String userId) {
		Optional<User> userOptional =   userRepository.findById(userId);
		User user = userOptional.get();
		return user;
	}
	private List<String> getIdTypeRemisesFromDB(String userId) {
	   User user = getUserFromDB(userId);
	   List<String> idTypeRemises = new ArrayList<String>(); 
	   for(TypeRemise typeRemise : user.getTypeRemises()) 
	   { 
		   idTypeRemises.add(typeRemise.getIdTypeRemise());
	   } 
	   return idTypeRemises;
	}
	private List<String> getIdTypeRemisesSelected(HttpServletRequest req) {
		  List<String> idTypeRemisesdb = typeRemiseRepository.findByIdTypeRemise();
		  List<String> idTypeRemisesOn = new ArrayList<String>(); 
		  for(String idTypeRemise : idTypeRemisesdb ) 
		  {
		      if("on".equals(req.getParameter(idTypeRemise))) 
		      {
		    	  idTypeRemisesOn.add(idTypeRemise);
		      }
		  }
		  return idTypeRemisesOn;
	}
	private List<String> getIdTypeValeursFromDB(String userId) {
		User user = getUserFromDB(userId);
		List<String> idTypeValeurs = new ArrayList<String>(); 
		for(TypeValeur typeValeur : user.getTypeValeurs()) 
		{ 
			idTypeValeurs.add(typeValeur.getIdTypeValeur());
		} 
	    return idTypeValeurs;
	}
	
	private List<String> getIdTypeValeursSelected(HttpServletRequest req) {
		  List<String> idTypeValeursdb = typeValeurRepository.findByIdTypeValeur();
		  List<String> idTypeValeursOn = new ArrayList<String>(); 
		  for(String idTypeValeur : idTypeValeursdb ) 
		  {
		      if("on".equals(req.getParameter(idTypeValeur))) 
		      {
		    	  idTypeValeursOn.add(idTypeValeur);
		      } 
		  }
		  return idTypeValeursOn;
	}
	private void updateTypeValeurToUser(HttpServletRequest req,User user,List<String> idTypeValeursSelected) {
		log.info("begin updateTypeValeurToUser *******");
		List<TypeValeur> typeValeurs = new ArrayList<TypeValeur>();
		idTypeValeursSelected.forEach(idTypeValeurSelected -> {
			typeValeurs.add(typeValeurRepository.findById(idTypeValeurSelected).get());
		});
		user.setTypeValeurs(typeValeurs);
		userRepository.save(user);
		log.info("end updateTypeValeurToUser *******");
	}
	private void updateTypeRemiseToUser(HttpServletRequest req,User user,List<String> idTypeRemisesSelected) {
		log.info("begin updateTypeRemiseToUser *******");
		List<TypeRemise> typeRemises = new ArrayList<TypeRemise>();
		idTypeRemisesSelected.forEach(idTypeRemiseSelected ->{
			typeRemises.add(typeRemiseRepository.findById(idTypeRemiseSelected).get());
		});
		user.setTypeRemises(typeRemises);
		userRepository.save(user);
		log.info("end updateTypeRemiseToUser *******");	
	}
}