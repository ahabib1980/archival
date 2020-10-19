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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.archival.api.ArchivalService;
import org.openmrs.module.archival.api.dao.ArchivalDao;

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
	
	/*
	 * @Override public ArchivedEncounter getItemByUuid(String uuid) throws
	 * APIException { return dao.getItemByUuid(uuid); }
	 * 
	 * @Override public ArchivedEncounter saveItem(ArchivedEncounter item) throws
	 * APIException {
	 * 
	 * if (item.getOwner() == null) { item.setOwner(userService.getUser(1)); }
	 * 
	 * 
	 * return dao.saveItem(item); }
	 */
	
	@Override
	public List<Patient> getPatientListForArchival(String query) {
		return dao.getPatientListForArchival(query);
	}
	
	@Override
	public void archivePatients(List<Patient> patientList) {
		for (Patient p : patientList) {
			archivePatient(p.getId());
		}
	}
	
	@Override
	public void archivePatient(Integer patientId) {
		
		List<Encounter> eList = null;
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		eList = Context.getEncounterService().getEncounters(patient, null, null, null, null, null, null, null, null, true);
		
		if (eList != null && eList.size() != 0)
			archiveEncounters(eList);
		
	}
	
	@Override
	public void archiveEncounters(List<Encounter> encounterList) {
		if (encounterList != null) {
			for (Encounter e : encounterList)
				archiveEncounter(e);
		}
		
	}
	
	@Override
	public void archiveEncounter(Encounter encounter) {
		Set<EncounterProvider> epSet = encounter.getEncounterProviders();
		
		Set<Obs> obsSet = encounter.getAllObs();
		
		dao.archiveEncounter(encounter, epSet, obsSet);
		
	}
	
	@Override
	public List<Patient> getArchivedPatients(String identifier, String name, String gender, Date fromDate, Date toDate,
	        User archivedBy) {
		
		return dao.getArchivedPatients(identifier, name, gender, fromDate, toDate, archivedBy);
		
	}
	
	@Override
	public Boolean retrievePatient(Integer patientId) throws APIException {
		// TODO Auto-generated method stub
		// TODO check if patient archived (just in case)
		
		return Boolean.TRUE;
	}
	
}
