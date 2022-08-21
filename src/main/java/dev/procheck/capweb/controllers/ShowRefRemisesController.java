package dev.procheck.capweb.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import dev.procheck.capweb.dao.ParamRefRemiseRepository;
import dev.procheck.capweb.entities.ParamRefRemise;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ShowRefRemisesController {
	
	@Autowired
	private UserController userController;
	
	@Autowired
	private ParamRefRemiseRepository paramRefRemiseRepository;
	
	@PostMapping("/showRefRemises")
	public String showRefRemises(HttpServletRequest req, Model model) throws IOException {
		log.info("begin ShowRefRemisesController .....");
		model.addAttribute("showRefRemise", true);
		List<ParamRefRemise> listParamRefRemise =  paramRefRemiseRepository.findAll();
		model.addAttribute("listParamRefRemise", listParamRefRemise);
		if(req.getParameter("idParamRefRemiseParametreGeneral") != null) {
			model.addAttribute("idParamRefRemise", req.getParameter("idParamRefRemiseParametreGeneral"));
		}
		model.addAttribute("idCompteGr", req.getParameter("idCompteGrParametreGeneral"));
		userController.listUsers(req, null, model, 0, "", "GR", 20);
		log.info("end ShowRefRemisesController .....");
		return  "listUsers";
	}
}
