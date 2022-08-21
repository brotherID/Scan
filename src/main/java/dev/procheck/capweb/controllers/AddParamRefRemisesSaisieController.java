package dev.procheck.capweb.controllers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import dev.procheck.capweb.common.Methodes;
import dev.procheck.capweb.common.ReferenceRemiseGenerator;
import dev.procheck.capweb.common.reference.remise.generator.ReferenceRemiseGenerated;
import dev.procheck.capweb.common.reference.remise.generator.ReferenceRemiseGeneratorEngine;
import dev.procheck.capweb.dao.CompteGrRepository;
import dev.procheck.capweb.dao.ParamRefRemiseRepository;
import dev.procheck.capweb.dao.UserRepository;
import dev.procheck.capweb.entities.CompteGr;
import dev.procheck.capweb.entities.ParamRefRemise;
import dev.procheck.capweb.entities.User;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AddParamRefRemisesSaisieController {

	@Value("${max.Length.Reference.Remise.Generated:9}")
	private Integer maxLengthReferenceRemiseGenerated;

	@Autowired
	private ParamRefRemiseRepository paramRefRemiseRepository;

	@Autowired
	private ReferenceRemiseGenerator referenceRemiseGenerator;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CompteGrRepository compteGrRepository;

	@Autowired
	private ReferenceRemiseGeneratorEngine referenceRemiseGeneratorEngine;
	
	@Autowired
	private ShowRefRemisesController showRefRemisesController;
	
	
	
	
	
	@PostMapping("/addParamRefRemisesSaisie")
	public String addRefRemises(HttpServletRequest req, Model model) throws IOException {
		log.info("begin addParamRefRemisesSaisie .....");
		String formule = req.getParameter("formule");
		String idCompteGr = req.getParameter("idCompteGr");
		if (checkValidateFormule(formule)) {
			model.addAttribute("errorFormule", false);
			model.addAttribute("existingFormule", false);
			String idParamRefRemise = String.valueOf(Methodes.GenerateRandom());
			ParamRefRemise paramRefRemise = new ParamRefRemise(idParamRefRemise, true, true, 9, 9, formule, formule,
					null, 0, 0, referenceRemiseGenerator.getDateNowAsString());
			paramRefRemiseRepository.save(paramRefRemise);
			CompteGr compteGr = compteGrRepository.findById(idCompteGr).get();
			compteGr.setParamRefRemise(paramRefRemise);
			User user = compteGr.getUser();
			log.info("user.getIdUser() {}", user.getIdUser());
			user.setParamRefRemise(paramRefRemise);
			userRepository.save(user);
			model.addAttribute("idCompteGr", idCompteGr);
			req.setAttribute("idCompteGrParametreGeneral", idCompteGr);
			user.setParamRefRemise(paramRefRemise);
			model.addAttribute("idParamRefRemise", paramRefRemise.getIdParamRefRemise());
			return showRefRemisesController.showRefRemises(req, model);
		} else {
			model.addAttribute("errorFormule", true);
			model.addAttribute("existingFormule", false);
			model.addAttribute("idCompteGr", idCompteGr);
			req.setAttribute("idCompteGrParametreGeneral", idCompteGr);
			CompteGr compteGr = compteGrRepository.findById(idCompteGr).get();
			if(compteGr.getParamRefRemise()!= null)
			{
				model.addAttribute("idParamRefRemise",compteGr.getParamRefRemise().getIdParamRefRemise() );
			}
			else
			{
				model.addAttribute("idParamRefRemise","");
			}
			log.error("error formul {}",formule);
		}
		if(checkContainsFormule(formule) == true)
		{
			model.addAttribute("existingFormule", true);
			model.addAttribute("errorFormule", false);
			model.addAttribute("formule", formule);
		}
		
		log.info("end addParamRefRemisesSaisie .....");
		return showRefRemisesController.showRefRemises(req, model);
	}
	
	private boolean checkContainsFormule(String formule) {
		List<String> paramRefRemises =   paramRefRemiseRepository.findAll().stream().map(paramRefRemise -> {
			return paramRefRemise.getFormule();
		}).collect(Collectors.toList());
		return paramRefRemises.contains(formule);
		
	}

	private boolean checkValidateFormule(String formule) {
		ReferenceRemiseGenerated referenceRemise = referenceRemiseGeneratorEngine.buildFakeReferenceRemise(formule);
		List<String> paramRefRemises =   paramRefRemiseRepository.findAll().stream().map(paramRefRemise -> {
			return paramRefRemise.getFormule();
		}).collect(Collectors.toList());
		
		return (referenceRemise.getReferenceRemiseGenerated().length() <= maxLengthReferenceRemiseGenerated && !paramRefRemises.contains(formule));
	}
}
