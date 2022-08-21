package dev.procheck.capweb.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import dev.procheck.capweb.dao.CompteGrRepository;

@RestController
@RequestMapping("/compteGrs")
public class CompteGrController {
	
	@Autowired
	private CompteGrRepository compteGrRepository;
	
	@GetMapping("/ribRemettant/{rib}")
	public Object  getNomRemettantByRIB(HttpServletRequest req,@PathVariable String rib) throws IOException {
		String id_user = (String)req.getSession().getAttribute("userid");
		if(id_user!=null) {
			return compteGrRepository.findByRibRemettantAndUser_IdUser(rib,id_user);
		}
		else {
			ModelAndView modelAndView=new ModelAndView("redirect:/login");
			return modelAndView;
		}
	}
}
