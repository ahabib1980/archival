/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.archival.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.archival.ArchivedEncounter;
import org.openmrs.module.archival.ArchivedEncounterProvider;
import org.openmrs.module.archival.ArchivedObs;
import org.openmrs.module.archival.api.ArchivalService;
import org.openmrs.module.archival.api.dao.ArchivalDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import ca.uhn.hl7v2.model.v26.group.EHC_E15_ADJUSTMENT_PAYEE;

/**
 * Implementation of the main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 * 
 * @author ali.habib@ihsinformatics.com
 */

public class ArchivalServiceImpl extends BaseOpenmrsService implements ArchivalService {
	
	ArchivalDao dao;
	
	UserService userService;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(ArchivalDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public List<Patient> getPatientListForArchival(String query) {
		return dao.getPatientListForArchival(query);
	}
	
	@Override
	public List<Integer> getPatientEncounterIds(Integer patientId) {
		List<Encounter> eList = null;
		List<Integer> idList = new ArrayList<Integer>();
		Patient patient = Context.getPatientService().getPatient(patientId);
		eList = Context.getEncounterService().getEncounters(patient, null, null, null, null, null, null, null, null, true);
		
		for (Encounter e : eList) {
			idList.add(e.getEncounterId());
		}
		
		return idList;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean archiveEncounter(Integer encounterId) {
		
		Encounter encounter = Context.getEncounterService().getEncounter(encounterId);
		
		Set<EncounterProvider> epSet = encounter.getEncounterProviders();
		
		Set<Obs> obsSet = encounter.getAllObs(true);
		
		//try {
		dao.archiveEncounter(encounter, epSet, obsSet);
		return Boolean.TRUE;
		//}
		
		/*
		 * catch (ConstraintViolationException cve) { cve.printStackTrace(); }
		 * 
		 * catch (Exception ex) { ex.printStackTrace(); }
		 * 
		 * return Boolean.FALSE;
		 */
		
	}
	
	@Override
	public List<Patient> getArchivedPatients(String identifier, String name, String gender) {
		
		return dao.getArchivedPatients(identifier, name, gender);
		
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean retrievePatient(Integer patientId) throws APIException {
		// TODO check if patient archived (just in case)
		//try {
		dao.retrieveArchivedPatient(patientId);
		return Boolean.TRUE;
		//}
		
		/*
		 * catch (ConstraintViolationException cve) { cve.printStackTrace(); }
		 * 
		 * catch (Exception ex) { ex.printStackTrace(); }
		 * 
		 * return Boolean.FALSE;
		 */
	}
	
}
