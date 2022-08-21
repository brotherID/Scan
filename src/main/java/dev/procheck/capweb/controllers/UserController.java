package dev.procheck.capweb.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.procheck.capweb.common.CryptWithMD5;
import dev.procheck.capweb.common.PageWrapper;
import dev.procheck.capweb.dao.TypeRemiseRepository;
import dev.procheck.capweb.dao.TypeValeurRepository;
import dev.procheck.capweb.dao.UserRepository;
import dev.procheck.capweb.entities.TypeRemise;
import dev.procheck.capweb.entities.User;
import lombok.extern.slf4j.Slf4j;
import procheck.dev.pkcore.bean.PKConnect;
import procheck.dev.pkcore.common.CommonData;
import procheck.dev.pkcore.security.PKSecurity;

@Controller
@Slf4j
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	public TypeValeurRepository typeValeurRepository;
	
	@Autowired
	public TypeRemiseRepository typeRemiseRepository;
	
	
	@GetMapping(value = { "/login", "/error" })
	public void error(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
	}

	@GetMapping(value = "/")
	public ModelAndView Accueil(HttpServletRequest req, HttpServletResponse res) throws Exception {
		log.info("Redirection authentification page");
		Connection cnxToDb = null;
		cnxToDb = PKConnect.getCnxToDb();
		try {
			if (!PKSecurity.isUserAuth(req, res, CommonData.PKEXPLORER_APP_CODE, cnxToDb)) {
				res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
				try {
					cnxToDb.close();
				} catch (Exception e) {
					log.error("Error ", e);
					// TODO: handle exception
				}
				return null;
			} else {
				String id_user = (String) req.getSession().getAttribute("userid");
				User u = userRepository.findById(id_user).get();
				req.getSession().setAttribute("username", u.getUserName());
				req.getSession().setAttribute("userlogin", u.getUserLogin());
				req.getSession().setAttribute("role", u.getRole());
				return new ModelAndView("redirect:/remises");
			}
		} catch (Exception e) {
			res.sendRedirect(res.encodeRedirectURL("/PKAuth/logout"));
		} finally {
			try {

				cnxToDb.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return null;
	}

	@GetMapping(value = "/listUsers")
	public ModelAndView listUsers(HttpServletRequest req, HttpServletResponse res, Model model,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "motCle", defaultValue = "") String motCle,
			@RequestParam(name = "typeG", defaultValue = "GR") String typeG,
			@RequestParam(name = "size", defaultValue = "20") int size) throws IOException {
		Connection cnxToDb = null;
		cnxToDb = PKConnect.getCnxToDb();
		if (!PKSecurity.isUserAuth(req, res, CommonData.PKEXPLORER_APP_CODE, cnxToDb)) {
			res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
			try {
				cnxToDb.close();
			} catch (Exception e) {
				log.error("Error ", e);
				// TODO: handle exception
			}
			return null;
		} else {
			try {
				cnxToDb.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		String id_user = (String) req.getSession().getAttribute("userid");
		if (id_user != null) {
			String role = (String) req.getSession().getAttribute("role");
			if (role.equals("ADMIN")) {
				Page<User> users;
				if (typeG.equals("GR")) {
					users = userRepository.findAllByRoleIsNotAndCapturePointLikeAndIsGrTrue(role, "%" + motCle + "%",
							PageRequest.of(page, size));
				} else {
					users = userRepository.findAllByRoleIsNotAndIsGrFalseOrIsGrIsNull(role, PageRequest.of(page, size));
				}
				PageWrapper<User> pageUsers = new PageWrapper<User>(users, "/listUsers");
				model.addAttribute("page", pageUsers);
				model.addAttribute("currentNumber", page);
				model.addAttribute("totalElements", users.getTotalElements());
				model.addAttribute("users", users.getContent());
				model.addAttribute("motCle", motCle);
				model.addAttribute("typeG", typeG);
				User u = userRepository.findById(id_user).get();
				model.addAttribute("currentUser", u);
		    	String idTypeValeur = typeValeurRepository.findByTypeValeur("CHQ");
		    	model.addAttribute("idTypeValeur", idTypeValeur);
		    	String idTypeValeurLCN = typeValeurRepository.findByTypeValeur("LCN");
		    	model.addAttribute("idTypeValeurLCN", idTypeValeurLCN);
				List<TypeRemise> typeRemisesCHQ = typeRemiseRepository.findAllByIdTypeValeur(idTypeValeur);
				model.addAttribute("typeRemisesCHQ", typeRemisesCHQ);
				List<TypeRemise> typeRemisesLCN = typeRemiseRepository.findAllByIdTypeValeur(idTypeValeurLCN);
				model.addAttribute("typeRemisesLCN", typeRemisesLCN);
				return new ModelAndView("listUsers");
			} else {
				return new ModelAndView("redirect:/login");
			}
		} else {
			return new ModelAndView("redirect:/login");
		}

	}
	
	public List<String> listIdTypeRemiseHasUser(String userId) {
		Optional<User> optionalUser = userRepository.findById(userId);
		User user = optionalUser.get();
		return user.getTypeRemises().stream().map(TypeRemise::getIdTypeRemise).collect(Collectors.toList());

	}
	

	@GetMapping(value = "/actionUser")
	public ModelAndView activeUser(HttpServletRequest req, HttpServletResponse res,@RequestParam String idUser, @RequestParam String action,
			RedirectAttributes redirectAttrs) throws IOException {
		Connection cnxToDb = null;
		cnxToDb = PKConnect.getCnxToDb();
		if (!PKSecurity.isUserAuth(req, res, CommonData.PKEXPLORER_APP_CODE, cnxToDb)) {
			res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
			try {
				cnxToDb.close();
			} catch (Exception e) {
				log.error("Error ", e);
				// TODO: handle exception
			}
			return null;
		} else {
			try {
				cnxToDb.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		Optional<User> user = userRepository.findById(idUser);
		if (user.isPresent()) {
			User u = user.get();
			String typeG = "GR";
			if (u.getIsGr() == null || !u.getIsGr())
				typeG = "AG";
			if (action.equals("enable")) {
				u.setIsActif(1);
				u.setNbFailPassword(0);
				userRepository.save(u);
				redirectAttrs.addFlashAttribute("message",
						"L'utilisateur " + u.getUserLogin() + " est activé avec succès.");
			} else if (action.equals("disable")) {
				u.setIsActif(0);
				userRepository.save(u);
				redirectAttrs.addFlashAttribute("message",
						"L'utilisateur " + u.getUserLogin() + " est désactivé avec succès.");
			} else if (u.getIsGr() != null && u.getIsGr() && action.equals("reset")) {
				u.setIsActif(1);
				u.setNbFailPassword(0);
				u.setIsPasswordChanged(false);
				u.setUserPassword(CryptWithMD5.cryptWithMD5("password"));
				userRepository.save(u);
				redirectAttrs.addFlashAttribute("message",
						"Le mot de passe de " + u.getUserLogin() + " est initialisé avec succès par password.");
			}

			return new ModelAndView("redirect:/listUsers?typeG=" + typeG);
		} else {
			return new ModelAndView("redirect:/login");
		}

	}

}
