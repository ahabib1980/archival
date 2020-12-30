/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.archival.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.archival.api.ArchivalService;
import org.openmrs.module.archival.web.dto.PatientDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * This class configured as controller using annotation and mapped with the URL of
 * 'module/${rootArtifactid}/${rootArtifactid}Link.form'.
 */
@Controller
public class RetrievalController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/** Success form view name */
	private final String SUCCESS_AND_VIEW = "/module/archival/retrieve";
	
	private final String VIEW = "/module/${rootArtifactid}/${rootArtifactid}";
	
	ArchivalService archivalService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/archival/retrieve.form")
	public String showForm(ModelMap model) {
		archivalService = Context.getService(ArchivalService.class);
		
		return SUCCESS_AND_VIEW;
	}
	
	/**
	 * All the parameters are optional based on the necessity
	 * 
	 * @param httpSession
	 * @param anyRequestObject
	 * @param errors
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String onPost(HttpSession httpSession, @ModelAttribute("anyRequestObject") Object anyRequestObject,
	        BindingResult errors) {
		
		if (errors.hasErrors()) {
			// return error view
		}
		
		return SUCCESS_AND_VIEW;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/archival/searchArchivedPatients.form")
	@ResponseBody
	public String searchArchivedPatients(HttpServletRequest request,
	        @RequestParam(value = "identifier", required = false) String identifier,
	        @RequestParam(value = "name", required = false) String name,
	        @RequestParam(value = "gender", required = false) String gender) {
		
		String json = "";
		JsonObject jsonObj = new JsonObject();
		JsonObject responseObj = new JsonObject();
		JsonArray patientArray = new JsonArray();
		archivalService = Context.getService(ArchivalService.class);
		
		// if not passed, receiving empty parameters
		try {
			// TODO: refactor: retrieve patients method was not implmented that's why using the query method
			String query = "Select * from patient p inner join person_name pn on p.patient_id = pn.person_id where pn.given_name like '%a%';";
			List<Patient> patients = archivalService.getPatientListForArchival(query);
			String patientIdentifier = identifier == "" ? null : identifier;
			String patientName = name == "" ? null : name;
			String patientGender = gender == "" ? null : gender;
			List<Patient> patientss = archivalService.getArchivedPatients(patientIdentifier, patientName, patientGender,
			    null, null, null);
			List<PatientDto> patientDtoList = new ArrayList<PatientDto>();
			Logger.getAnonymousLogger().info(
			    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Count of searched patients is: " + patients.size());
			
			if (patients.size() > 0) {
				for (Patient patient : patients) {
					JsonObject obj = new JsonObject();
					PatientDto patientDto = new PatientDto(patient);
					obj.addProperty("patientId", patient.getPatientId().toString());
					patientDtoList.add(patientDto);
					patientArray.add(obj);
				}
				Gson gson = new Gson();
				json = gson.toJson(patientDtoList);
			} else {
				jsonObj.addProperty("status", "empty");
				return jsonObj.toString();
			}
		}
		catch (APIException e) {
			e.printStackTrace();
			jsonObj.addProperty("status", "fail");
			return jsonObj.toString();
		}
		
		return json;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/archival/retievePatient.form")
	@ResponseBody
	public String retrievePatient(HttpServletRequest request,
	        @RequestParam(value = "patientId", required = false) String patientId) {
		
		JsonObject responseObj = new JsonObject();
		responseObj.addProperty("success", true);
		Logger.getAnonymousLogger().info(
		    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Patient ID: " + patientId);
		
		Boolean status = archivalService.retrievePatient(Integer.parseInt(patientId));
		// TODO: complete it
		return responseObj.toString();
	}
	
}
