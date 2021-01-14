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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.archival.api.ArchivalService;
import org.openmrs.module.archival.web.dto.PatientDto;
import org.openmrs.module.archival.web.util.PdfReport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
		
		String json = "";
		JsonObject jsonObj = new JsonObject();
		JsonArray patientArray = new JsonArray();
		archivalService = Context.getService(ArchivalService.class);
		
		try {
			List<Patient> patients = archivalService.getPatientListForArchival(query);
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
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/archival/archivePatients.form")
	@ResponseBody
	public String archivePatients(HttpServletRequest request,
	        @RequestParam(value = "patients", required = false) String patientIdArray) {
		
		JsonObject responseObj = new JsonObject();
		Logger.getAnonymousLogger().info(
		    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Patient array string: " + patientIdArray);
		
		try {
			ArrayList<String> patientArrayList = new ArrayList<String>();
			if (patientIdArray.contains(",")) {
				String[] patientIds = patientIdArray.split(",");
				/* patientArrayList = (ArrayList<String>) Arrays.asList(patientIds); */
				for (int i = 0; i < patientIds.length; i++) {
					patientArrayList.add(patientIds[i]);
				}
			} else
				patientArrayList.add(patientIdArray);
			
			List<Integer> archivedPatients = new ArrayList<Integer>();
			List<Integer> failedArchivePatients = new ArrayList<Integer>();
			List<Integer> encIds = new ArrayList<Integer>();
			
			int encounterCount = 0;
			Boolean archiveResult = null;
			Person person = null;
			
			PersonAttributeType paType = Context.getPersonService().getPersonAttributeTypeByName("Archived");
			
			for (String id : patientArrayList) {
				
				encounterCount = 0;
				Patient pat = Context.getPatientService().getPatient(Integer.parseInt(id));
				
				if (pat != null) {
					encIds = archivalService.getPatientEncounterIds(pat.getPatientId());
					
					for (Integer encId : encIds) {
						try {
							
							archiveResult = archivalService.archiveEncounter(encId);
							if (archiveResult != null && archiveResult)
								encounterCount++;
						}
						
						catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					
					if (encounterCount != 0) {
						//						person = pat.getPerson();
						//						person.addAttribute(new PersonAttribute(paType, "Yes"));
						//						Context.getPersonService().savePerson(person);
						archivedPatients.add(pat.getId());
					}
					
					else {
						failedArchivePatients.add(pat.getId());
					}
				}
			}
			
			String successArray = "";
			for (Integer id : archivedPatients) {
				successArray += id + ",";
			}
			
			if (successArray.length() != 0)
				successArray = successArray.substring(0, successArray.length() - 1);
			
			Logger.getAnonymousLogger().info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Success: " + successArray);
			
			String failureArray = "";
			for (Integer id : failedArchivePatients) {
				failureArray += id + ",";
			}
			
			if (failureArray.length() != 0)
				failureArray = failureArray.substring(0, failureArray.length() - 1);
			
			Logger.getAnonymousLogger().info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> failure: " + failureArray);
			
			//responseObj.addProperty("patientIds", patientIdArray);
			responseObj.addProperty("successIds", successArray);
			responseObj.addProperty("failureIds", failureArray);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			responseObj.addProperty("status", "fail");
			return responseObj.toString();
		}
		
		responseObj.addProperty("status", "success");
		return responseObj.toString();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/archival/downloadReport.form" /*, produces = "application/pdf" */)
	public ResponseEntity<byte[]> downloadReport(
	        @RequestParam(value = "successList", required = false) String successIdArray,
	        @RequestParam(value = "failureList", required = false) String failureIdArray) throws IOException {
		
		ArrayList<String> successArrayList = new ArrayList<String>();
		if (successIdArray.contains(",")) {
			String[] successIds = successIdArray.split(",");
			successArrayList = (ArrayList<String>) Arrays.asList(successIds);
		} else
			successArrayList.add(successIdArray);
		
		List<PatientDto> successDtos = new ArrayList<PatientDto>();
		for (String id : successArrayList) {
			Patient pat = Context.getPatientService().getPatient(Integer.parseInt(id));
			successDtos.add(new PatientDto(pat));
		}
		
		InputStream inputStream = PdfReport.generateReport(successDtos, Context.getAuthenticatedUser());
		ByteArrayInputStream bis = PdfReport.generateReport(successDtos, Context.getAuthenticatedUser());
		byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);
		
		String filename = "archive_report_" + new Timestamp(System.currentTimeMillis()) + ".pdf";
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
		return response;
	}
}
