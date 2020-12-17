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

import java.util.List;
import java.util.*;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.archival.api.ArchivalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * This class configured as controller using annotation and mapped with the URL of
 * 'module/${rootArtifactid}/${rootArtifactid}Link.form'.
 */
@Controller
public class ArchivalController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/** Success form view name */
	private final String SUCCESS_AND_VIEW = "/module/archival/archive";
	
	ArchivalService archivalService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/archival/archival.form")
	public String showForm(ModelMap model) {
		archivalService = Context.getService(ArchivalService.class);
		
		return SUCCESS_AND_VIEW;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/archival/executeQuery.form")
	@ResponseBody
	public String executeQuery(HttpServletRequest request, @RequestParam(value = "query", required = false) String query) {
		
		Logger.getAnonymousLogger().info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Query: " + query);
		//		query = " Select * from patient p inner join person_name pn on p.patient_id = pn.person_id where pn.given_name like '%John%';";
		archivalService = Context.getService(ArchivalService.class);
		
		JsonObject responseObj = new JsonObject();
		JsonArray patientArray = new JsonArray();
		List<Patient> patients = archivalService.getPatientListForArchival(query);
		Logger.getAnonymousLogger().info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Searched patient size is: " + patients.size());
		
		for (Patient p : patients) {
			JsonObject obj = new JsonObject();
			obj.addProperty("patientId", p.getPatientId().toString());
			patientArray.add(obj);
		}
		
		responseObj.add("patientList", patientArray);
		return responseObj.toString();
	}
}
