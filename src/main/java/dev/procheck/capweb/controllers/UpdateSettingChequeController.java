package dev.procheck.capweb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
public class UpdateSettingChequeController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TypeRemiseRepository typeRemiseRepository;
	@Autowired
	private TypeValeurRepository typeValeurRepository;
	@Autowired
	public UserController userController;

	@PostMapping("/updateSettingCheque")
	public String updateSettingCheque(HttpServletRequest req, Model model) throws IOException {
		String userId = req.getParameter("userIDParametreCheque");
		List<String> idTypeRemisesChequeFromUser = getIdTypeRemisesChequeFromUser(userId);
		List<String> idTypeRemisesChequeSelected = getIdTypeRemisesChequeSelected(req);
		List<String> idTypeValeursFromUser = getIdTypeChequeFromUser(userId);
		List<String> idTypeValeursSelected = getIdTypeChequeSelected(req);
		if(isTypeValeurAndTypeRemisesChequeSelectedNotChange(idTypeRemisesChequeFromUser, idTypeRemisesChequeSelected,
				idTypeValeursFromUser, idTypeValeursSelected)) 
		  {
		      model.addAttribute("fail", true); 
		      model.addAttribute("success", false);
		  } 
		  else 
		  { // the are change in TypeValeur And TypeRemises Cheque Selected NotChange
			  User user = getUserFromDB(userId);
			  updateTypeValeurChequeToUser(req,user,idTypeValeursSelected);
			  updateTypeRemiseChequeToUser(req,user,idTypeRemisesChequeSelected);
			  model.addAttribute("success", true);
			  model.addAttribute("fail", false);
		  } 
		  userController.listUsers(req, null, model, 0, "", "GR", 20);
		return "listUsers";

	}
	private boolean isTypeValeurAndTypeRemisesChequeSelectedNotChange(List<String> idTypeRemisesChequeFromUser,
			List<String> idTypeRemisesChequeSelected, List<String> idTypeValeursFromUser,
			List<String> idTypeValeursSelected) {
		return idTypeRemisesChequeFromUser.equals(idTypeRemisesChequeSelected) && idTypeValeursFromUser.equals(idTypeValeursSelected);
	}
	private void updateTypeRemiseChequeToUser(HttpServletRequest req,User user,List<String> idTypeRemisesSelected) {
		log.info("begin updateTypeRemiseChequeToUser *******");
		
		List<TypeRemise> typeRemiseToUpdate= user.getTypeRemises().stream().filter(typeRemise -> {
			// garder que les types remises or que cheque
			return !"CHQ".equals(typeRemise.getTypeValeur().getType());
		}).collect(Collectors.toList());
		List<TypeRemise> typeRemisesCheque = new ArrayList<TypeRemise>();
		idTypeRemisesSelected.forEach(idTypeRemiseSelected ->{
			typeRemisesCheque.add(typeRemiseRepository.findById(idTypeRemiseSelected).get());
		});
		// add  typeRemisesCheque Selected
		typeRemiseToUpdate.addAll(typeRemisesCheque);
		user.setTypeRemises(typeRemiseToUpdate);
		userRepository.save(user);
		log.info("end updateTypeRemiseChequeToUser *******");	
	}
	
	private void updateTypeValeurChequeToUser(HttpServletRequest req,User user,List<String> idTypeValeursSelected) {
		log.info("begin updateTypeValeurChequeToUser *******");
		List<TypeValeur> typeValeursToUpdate=user.getTypeValeurs().stream().filter(typeValeur -> {
			return !"CHQ".equals(typeValeur.getType());
		}).collect(Collectors.toList());
		
		List<TypeValeur> typeValeursChequeSelected = new ArrayList<TypeValeur>();
		idTypeValeursSelected.forEach(idTypeValeurSelected -> {
			typeValeursChequeSelected.add(typeValeurRepository.findById(idTypeValeurSelected).get());
		});
		// add all TypeValeur cheque Selected
		typeValeursToUpdate.addAll(typeValeursChequeSelected);
		
		user.setTypeValeurs(typeValeursToUpdate);
		userRepository.save(user);
		log.info("end updateTypeValeurChequeToUser *******");
	}
	
	private List<String> getIdTypeChequeSelected(HttpServletRequest req) {
		  String idTypeValeurDB = typeValeurRepository.findByTypeValeur("CHQ");
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

	private List<String> getIdTypeChequeFromUser(String userId) {
		User user = getUserFromDB(userId);
		List<String> idTypeValeursUser = user.getTypeValeurs().stream().filter(typeValeur -> {
			return  "CHQ".equals(typeValeur.getType());  
		}).map(typeValeur ->{
			return typeValeur.getIdTypeValeur();
		}).collect(Collectors.toList());
		return idTypeValeursUser;
	}

	private List<String> getIdTypeRemisesChequeFromUser(String userId) {
		User user = getUserFromDB(userId);
		List<String> idtypeRemisesChequeBD = getIdTypeRemisesChequeDB();
		List<String> idtypeRemisesChequeUser = user.getTypeRemises().stream().map(typeRemise -> {

			return typeRemise.getIdTypeRemise();
		}).filter(idTypeRemise -> {
			return idtypeRemisesChequeBD.contains(idTypeRemise);
		}).collect(Collectors.toList());
		return idtypeRemisesChequeUser;
	}

	private List<String> getIdTypeRemisesChequeDB() {
		String idTypeValeur = typeValeurRepository.findByTypeValeur("CHQ");
		List<String> idtypeRemisesChequeBD = typeRemiseRepository.findAllByIdTypeValeur(idTypeValeur).stream()
				.map(typeRemise -> {

					return typeRemise.getIdTypeRemise();
				}).collect(Collectors.toList());
		return idtypeRemisesChequeBD;
	}

	private List<String> getIdTypeRemisesChequeSelected(HttpServletRequest req) {
		List<String> idTypeRemisesChequeDB = getIdTypeRemisesChequeDB();
		List<String> idTypeRemisesChequeOn = idTypeRemisesChequeDB.stream().filter(idTypeRemise -> {
			return "on".equals(req.getParameter(idTypeRemise));
		}).collect(Collectors.toList());
		return idTypeRemisesChequeOn;
	}
}
