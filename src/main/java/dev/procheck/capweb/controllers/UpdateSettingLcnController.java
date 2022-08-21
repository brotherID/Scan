package dev.procheck.capweb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
public class UpdateSettingLcnController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TypeRemiseRepository typeRemiseRepository;
	@Autowired
	private TypeValeurRepository typeValeurRepository;
	@Autowired
	public UserController userController;
	
	@PostMapping("/updateSettingLcn")
	public String updateSettingCheque(HttpServletRequest req, Model model) throws IOException {
		String userId = req.getParameter("userIDParametreLcn");
		List<String> idTypeRemisesLcnFromUser = getIdTypeRemisesLcnFromUser(userId);
		List<String> idTypeRemisesLcnSelected = getIdTypeRemisesLcnSelected(req);
		List<String> idTypeValeursFromUser = getIdTypeLcnFromUser(userId);
		List<String> idTypeValeursSelected = getIdTypeLcnSelected(req);
		if(isTypeValeurAndTypeRemisesLcnSelectedNotChange(idTypeRemisesLcnFromUser, idTypeRemisesLcnSelected,
				idTypeValeursFromUser, idTypeValeursSelected)) 
		  {
		      model.addAttribute("fail", true); 
		      model.addAttribute("success", false);
		  } 
		  else 
		  { // the are change in TypeValeur And TypeRemises Cheque Selected NotChange
			  User user = getUserFromDB(userId);
			  updateTypeValeurLcnToUser(req,user,idTypeValeursSelected);
			  updateTypeRemiseLcnToUser(req,user,idTypeRemisesLcnSelected);
			  model.addAttribute("success", true);
			  model.addAttribute("fail", false);
		  } 
		  userController.listUsers(req, null, model, 0, "", "GR", 20);
		return "listUsers";

	}
	
	private boolean isTypeValeurAndTypeRemisesLcnSelectedNotChange(List<String> idTypeRemisesLcnFromUser,
			List<String> idTypeRemisesLcnSelected, List<String> idTypeValeursFromUser,
			List<String> idTypeValeursSelected) {
		return idTypeRemisesLcnFromUser.equals(idTypeRemisesLcnSelected) && idTypeValeursFromUser.equals(idTypeValeursSelected);
	}
	private void updateTypeRemiseLcnToUser(HttpServletRequest req,User user,List<String> idTypeRemisesSelected) {
		log.info("begin updateTypeRemiseLcnToUser *******");
		
		List<TypeRemise> typeRemiseToUpdate= user.getTypeRemises().stream().filter(typeRemise -> {
			// garder que les types remises or que Lcn
			return !"LCN".equals(typeRemise.getTypeValeur().getType());
		}).collect(Collectors.toList());
		List<TypeRemise> typeRemisesLcn = new ArrayList<TypeRemise>();
		idTypeRemisesSelected.forEach(idTypeRemiseSelected ->{
			typeRemisesLcn.add(typeRemiseRepository.findById(idTypeRemiseSelected).get());
		});
		// add  typeRemisesCheque Selected
		typeRemiseToUpdate.addAll(typeRemisesLcn);
		user.setTypeRemises(typeRemiseToUpdate);
		userRepository.save(user);
		log.info("end updateTypeRemiseLcnToUser *******");	
	}
	
	private void updateTypeValeurLcnToUser(HttpServletRequest req,User user,List<String> idTypeValeursSelected) {
		log.info("begin updateTypeValeurLcnToUser *******");
		List<TypeValeur> typeValeursToUpdate=user.getTypeValeurs().stream().filter(typeValeur -> {
			return !"LCN".equals(typeValeur.getType());
		}).collect(Collectors.toList());
		
		List<TypeValeur> typeValeursLcnSelected = new ArrayList<TypeValeur>();
		idTypeValeursSelected.forEach(idTypeValeurSelected -> {
			typeValeursLcnSelected.add(typeValeurRepository.findById(idTypeValeurSelected).get());
		});
		// add all TypeValeur Lcn Selected
		typeValeursToUpdate.addAll(typeValeursLcnSelected);
		
		user.setTypeValeurs(typeValeursToUpdate);
		userRepository.save(user);
		log.info("end updateTypeValeurLcnToUser *******");
	}
	
	private List<String> getIdTypeLcnSelected(HttpServletRequest req) {
		  String idTypeValeurDB = typeValeurRepository.findByTypeValeur("LCN");
		  List<String> idTypeValeursOn = new ArrayList<String>();
		  if("on".equals(req.getParameter(idTypeValeurDB)))
		  {
			  idTypeValeursOn.add(idTypeValeurDB);
		  }
		  
		return idTypeValeursOn; 
	}

	private User getUserFromDB(String userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		User user = userOptional.get();
		return user;
	}

	private List<String> getIdTypeLcnFromUser(String userId) {
		User user = getUserFromDB(userId);
		List<String> idTypeValeursUser = user.getTypeValeurs().stream().filter(typeValeur -> {
			return  "LCN".equals(typeValeur.getType());  
		}).map(typeValeur ->{
			return typeValeur.getIdTypeValeur();
		}).collect(Collectors.toList());
		return idTypeValeursUser;
	}

	private List<String> getIdTypeRemisesLcnFromUser(String userId) {
		User user = getUserFromDB(userId);
		List<String> idtypeRemisesLcnBD = getIdTypeRemisesLcnDB();
		List<String> idtypeRemisesLcnUser = user.getTypeRemises().stream().map(typeRemise -> {

			return typeRemise.getIdTypeRemise();
		}).filter(idTypeRemise -> {
			return idtypeRemisesLcnBD.contains(idTypeRemise);
		}).collect(Collectors.toList());
		return idtypeRemisesLcnUser;
	}

	private List<String> getIdTypeRemisesLcnDB() {
		String idTypeValeur = typeValeurRepository.findByTypeValeur("LCN");
		List<String> idtypeRemisesLcnBD = typeRemiseRepository.findAllByIdTypeValeur(idTypeValeur).stream()
				.map(typeRemise -> {

					return typeRemise.getIdTypeRemise();
				}).collect(Collectors.toList());
		return idtypeRemisesLcnBD;
	}

	private List<String> getIdTypeRemisesLcnSelected(HttpServletRequest req) {
		List<String> idTypeRemisesLcnDB = getIdTypeRemisesLcnDB();
		List<String> idTypeRemisesLcnOn = idTypeRemisesLcnDB.stream().filter(idTypeRemise -> {
			return "on".equals(req.getParameter(idTypeRemise));
		}).collect(Collectors.toList());
		return idTypeRemisesLcnOn;
	}
	

}
