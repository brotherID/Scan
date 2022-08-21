package dev.procheck.capweb.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dev.procheck.capweb.dao.CompteGrRepository;
import dev.procheck.capweb.dao.ParamRefRemiseRepository;
import dev.procheck.capweb.dao.UserRepository;
import dev.procheck.capweb.entities.CompteGr;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("modals")
@Slf4j
public class ModalSettingGeneralcontoller {
	
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public CompteGrRepository compteGrRepository;
	
	@GetMapping("modal/settingGeneral")
	public String modalSettingGeneral(@RequestParam("userId") String userId, Model model) {
		log.info("begin ModalSettingGeneralcontoller .....");
		model.addAttribute("userId", userId);
		List<CompteGr> listCompteGr = compteGrRepository.findByIdUser(userId);
		model.addAttribute("listCompteGr", listCompteGr);
		log.info("end ModalSettingGeneralcontoller .....");
		return  "params/parametre-general";
	}

}
