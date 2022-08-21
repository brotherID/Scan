package dev.procheck.capweb.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dev.procheck.capweb.dao.TypeRemiseRepository;
import dev.procheck.capweb.dao.TypeValeurRepository;
import dev.procheck.capweb.dao.UserRepository;
import dev.procheck.capweb.entities.TypeRemise;
import dev.procheck.capweb.entities.TypeValeur;
import dev.procheck.capweb.entities.User;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("modals")
@Slf4j
public class ModalParamChequeLcnController {
	
	@Autowired
	public TypeRemiseRepository typeRemiseRepository;

	@Autowired
	public UserRepository userRepository;

	@Autowired
	public TypeValeurRepository typeValeurRepository;
	
	@GetMapping("modal/chequeLcn")
	public String modalParamChequeLcn(@RequestParam("userId") String userId, Model model) {
		log.info("begin modalParamChequeLcnController .....");
		String idTypeValeur = typeValeurRepository.findByTypeValeur("CHQ");
    	model.addAttribute("idTypeValeur", idTypeValeur);
		String idTypeValeurLCN = typeValeurRepository.findByTypeValeur("LCN");
		model.addAttribute("idTypeValeurLCN", idTypeValeurLCN);
		List<TypeRemise> typeRemisesCHQ = typeRemiseRepository.findAllByIdTypeValeur(idTypeValeur);
		model.addAttribute("typeRemisesCHQ", typeRemisesCHQ);
		List<TypeRemise> typeRemisesLCN = typeRemiseRepository.findAllByIdTypeValeur(idTypeValeurLCN);
		model.addAttribute("typeRemisesLCN", typeRemisesLCN);
		model.addAttribute("hasCheque", isHasCheque(userId));
		model.addAttribute("hasLcn", isHasLcn(userId));
		model.addAttribute("listIdTypeRemiseHasUser", listIdTypeRemiseHasUser(userId));
		model.addAttribute("userId", userId);
		log.info("end modalParamChequeLcnController .....");
		return  "params/parametre-cheque-lcn";
	}
	public boolean isHasCheque(String userId) {
		Optional<User> optionalUser = userRepository.findById(userId);
		User user = optionalUser.get();
		String idTypeValeurCheque = typeValeurRepository.findByTypeValeur("CHQ");
		Optional<TypeValeur> typeValeurCheque = user.getTypeValeurs().stream()
				.filter(typeValeur -> idTypeValeurCheque.equals(typeValeur.getIdTypeValeur())).findFirst();
		return typeValeurCheque.isPresent();
	}
	
	public List<String> listIdTypeRemiseHasUser(String userId) {
		Optional<User> optionalUser = userRepository.findById(userId);
		User user = optionalUser.get();
		return user.getTypeRemises().stream().map(TypeRemise::getIdTypeRemise).collect(Collectors.toList());
	}
	public boolean isHasLcn(String userId) {

		Optional<User> optionalUser = userRepository.findById(userId);
		User user = optionalUser.get();
		String idTypeValeurLcn = typeValeurRepository.findByTypeValeur("LCN");
		Optional<TypeValeur> typeValeurLcn = user.getTypeValeurs().stream()
				.filter(typeValeur -> idTypeValeurLcn.equals(typeValeur.getIdTypeValeur())).findFirst();
		return typeValeurLcn.isPresent();
	}
}
