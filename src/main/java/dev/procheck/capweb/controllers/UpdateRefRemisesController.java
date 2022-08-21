package dev.procheck.capweb.controllers;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import dev.procheck.capweb.dao.CompteGrRepository;
import dev.procheck.capweb.dao.ParamRefRemiseRepository;
import dev.procheck.capweb.dao.UserRepository;
import dev.procheck.capweb.entities.CompteGr;
import dev.procheck.capweb.entities.ParamRefRemise;
import dev.procheck.capweb.entities.User;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UpdateRefRemisesController {
	
	@Autowired
	private UserController userController;
	
	@Autowired
	private CompteGrRepository compteGrRepository;
	
	@Autowired
	private ParamRefRemiseRepository paramRefRemiseRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/updateRefRemises")
	public String updateRefRemises(HttpServletRequest req, Model model) throws IOException {
		log.info("begin UpdateRefRemisesController .....");
		comparatorIdParamRefRemiseDBAndSelected(req,model);
		Optional<CompteGr> compteGrOptional = compteGrRepository.findById(req.getParameter("idCompteGr"));
		CompteGr compteGr =  compteGrOptional.get();
		Optional<ParamRefRemise> paramRefRemiseOptional = paramRefRemiseRepository.findById(req.getParameter("idParamRefRemise"));
		ParamRefRemise paramRefRemise =  paramRefRemiseOptional.get();
		compteGr.setParamRefRemise(paramRefRemise);
		compteGrRepository.save(compteGr);
		User user = compteGr.getUser();
		log.info("user.getIdUser() {}",user.getIdUser());
		Optional<User> userOptional = userRepository.findById(user.getIdUser());
		User userUpdate =  userOptional.get();
		userUpdate.setParamRefRemise(paramRefRemise);
		userRepository.save(userUpdate);
		userController.listUsers(req, null, model, 0, "", "GR", 20);
		log.info("end UpdateRefRemisesController .....");
		return  "listUsers";
	}
	
	private void comparatorIdParamRefRemiseDBAndSelected(HttpServletRequest req, Model model) {
		Optional<CompteGr> compteGrOptional = compteGrRepository.findById(req.getParameter("idCompteGr"));
		CompteGr compteGr =  compteGrOptional.get();
		String  idParamRefRemise = "";
		if(compteGr.getParamRefRemise()!= null)
		{
			 idParamRefRemise = compteGr.getParamRefRemise().getIdParamRefRemise();
		}
			
		if(idParamRefRemise.equals(req.getParameter("idParamRefRemise")))
		{
			model.addAttribute("settingReferenceRemiseNotChanged", true); 
			model.addAttribute("settingReferenceRemiseisChanged", false);
		}
		else
		{
			model.addAttribute("settingReferenceRemiseisChanged", true);
			model.addAttribute("settingReferenceRemiseNotChanged", false);
		}
	}
}
