package com.skilldistillery.frameworkautomation.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skilldistillery.frameworkautomation.entities.Rating;
import com.skilldistillery.frameworkautomation.entities.Template;
import com.skilldistillery.frameworkautomation.entities.TemplateInformation;
import com.skilldistillery.frameworkautomation.entities.User;
import com.skilldistillery.frameworkautomation.repositories.RatingRepository;
import com.skilldistillery.frameworkautomation.repositories.TemplateRepository;
import com.skilldistillery.frameworkautomation.services.TemplateService;
import com.skilldistillery.frameworkautomation.services.UserService;

@RestController
@RequestMapping(path = "api")
@CrossOrigin({ "*", "http://localhost:4289" })
public class TemplateController {

	@Autowired
	private TemplateService svc;

	@Autowired
	private UserService userSvc;

	@Autowired
	private TemplateRepository tempRepo;

	@Autowired
	RatingRepository ratingRepo;

	@GetMapping("templates")
	public List<TemplateInformation> findAllTemplates() {
		return svc.getAllActiveTemplates(); // gets template names
	}

//	@GetMapping("templates/search/{keyword}")
//	public List<Template> findTemplateByKeyword(@PathVariable String keyword) {
//		return tempRepo.findByNameLike("%" + keyword + "%"); // gets template names
//	}

	@PostMapping("templates")
	public Template createTemplate(@RequestBody Template template, Principal principal) {
		User user = userSvc.findByUsername(principal.getName());
		template.setUser(user);
		template.setAccess(true);
		template.setEnabled(true);
		Template newTemplate = svc.createTemplate(template);

		return newTemplate;
	}

	@PutMapping("templates/{id}")
	public Template editTemplate(@PathVariable Integer id, @RequestBody Template template, Principal principal) {
		Template oldTemplate = svc.findTemplateById(template.getId());
		if (oldTemplate.getUser().getUsername().equals(principal.getName())) {
			Template newTemplate = svc.updateTemplate(template, oldTemplate.getId());
			return newTemplate;
		} else {
			throw new RuntimeException("You dont own this template");
		}

	}

	@GetMapping("templates/{id}")
	public Template findTemplateById(@PathVariable Integer id, Principal principal) {
		return svc.findTemplateById(id);
	}

	@DeleteMapping("templates/{id}")
	public Boolean deleteTemplateById(@PathVariable Integer id, Principal principal) {
		Template template = svc.findTemplateById(id);
		if (template.getUser().getUsername().equals(principal.getName())) {
			svc.deleteTemplateById(id);
			return true;
		} else {
			return false;
		}
	}

	@PutMapping("templates/{id}/subtemplates/{subId}")
	public Template addSubtemplateToTemplate(@PathVariable Integer id, @PathVariable Integer subId,
			Principal principal) {
		Template parentTemplate = svc.findTemplateById(id);
		Template subTemplate = svc.findTemplateById(subId);
		if (parentTemplate.getUser().getUsername().equals(principal.getName())) {
			parentTemplate.addSubTemplate(subTemplate);
			svc.updateTemplate(parentTemplate, id);
			svc.updateTemplate(subTemplate, subId);
			return parentTemplate;

		} else {
			throw new RuntimeException("You do not own this template");
		}
	}

	@DeleteMapping("templates/{id}/subtemplates/{subId}")
	public Template deleteSubtemplateToTemplate(@PathVariable Integer id, @PathVariable Integer subId,
			Principal principal) {
		Template parentTemplate = svc.findTemplateById(id);
		Template subTemplate = svc.findTemplateById(subId);

		if (parentTemplate.getUser().getUsername().equals(principal.getName())) {
			parentTemplate.removeSubTemplate(subTemplate);
			svc.updateTemplate(parentTemplate, id);
			svc.updateTemplate(subTemplate, subId);

			if (subTemplate.getParentTemplates().size() == 0) {
				svc.deleteTemplateById(subTemplate.getId());
			}
			return parentTemplate;

		} else {
			throw new RuntimeException("You do not own this template");
		}
	}

	@GetMapping("templates/search/{keyword}")
	public List<TemplateInformation> getTemplateByKeyword(@PathVariable String keyword) {
		List<Template> templates = tempRepo.findByNameLike("%" + keyword + "%"); // gets template names
		List<TemplateInformation> tempInfo = new ArrayList<>();
		for (Template template : templates) {
			tempInfo.add(new TemplateInformation(template));
		}
		return tempInfo;
	}

	@PutMapping("templates/{templateId}/rating")
	public void addRatingToTemplate(Integer templateId, Principal principal) {
		Template template = svc.findTemplateById(templateId);
		User user = userSvc.findByUsername(principal.getName());

		Rating rating = new Rating();
		rating.setUser(user);
		rating.setTemplate(template);

		template.addRating(rating);
	}
	
	@GetMapping("me/rating/{id}")
	public Rating GetUserRating(@PathVariable Integer id, Principal principal) {
		String userName = principal.getName();
	
		Rating rating = ratingRepo.findByTemplate_idAndUser_Username(id, userName);
		TemplateInformation tempInfo = svc.getTemplateInformation(id);
		System.out.println(tempInfo.getRatings());
		
		return rating;
	}

	@PostMapping("me/rating/{id}")
	public TemplateInformation addRating(@PathVariable Integer id, Principal principal) {
		boolean didItWork = false;
		String userName = principal.getName();
		Rating rating = ratingRepo.findByTemplate_idAndUser_Username(id, userName);
		
		TemplateInformation tempInfo = svc.getTemplateInformation(id);
		
		if (rating == null) {
			didItWork = true;
			userSvc.addRating(userName, id);
		} else {
			didItWork = false;
			userSvc.removeRating(userName, id);
		}

		return tempInfo;

	}

	@PutMapping("me/rating/{id}")
	public boolean removeRating(@PathVariable Integer id, Principal principal) {
		String userName = principal.getName();
		return userSvc.removeRating(userName, id);
	}

	@GetMapping("templateInformation/{id}")
	public TemplateInformation getTemplateInformation(@PathVariable Integer id, Principal principal) {
		TemplateInformation tempInfo = svc.getTemplateInformation(id);
		System.out.println(tempInfo);
		return tempInfo;
	}

}
