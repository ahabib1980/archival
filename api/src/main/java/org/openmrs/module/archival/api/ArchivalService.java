/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.archival.api;

import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 * 
 * @author ali.habib@ihsinformatics.com
 */
@Transactional
public interface ArchivalService extends OpenmrsService {
	
	/**
	 * Returns a list of patients based on a query sent by user. This is a read only transaction
	 * 
	 * @param query
	 * @return list of patients
	 * @throws APIException on Exception
	 */
	
	List<Patient> getPatientListForArchival(String query) throws APIException;
	
	/**
	 * Archives a list of patients
	 * 
	 * @param list of patients
	 * @return void
	 * @throws
	 */
	
	void archivePatients(List<Patient> patientList) throws APIException;
	
	/**
	 * Archives a list of patients
	 * 
	 * @param patient id
	 * @return void
	 * @throws
	 */
	
	void archivePatient(Integer patientId) throws APIException;
	
	/**
	 * Archives a list of encounters
	 * 
	 * @param list of encounters
	 * @return void
	 * @throws
	 */
	
	void archiveEncounters(List<Encounter> encounterList) throws APIException;
	
	/**
	 * Archives an encounter
	 * 
	 * @param Encounter
	 * @return void
	 * @throws
	 */
	
	void archiveEncounter(Encounter encounter) throws APIException;
	
	/**
	 * Returns a list of archived patients based on various criteria
	 * 
	 * @param identifier, name, gender, from archive date, to archive date, archiving user
	 * @return list of patients
	 * @throws APIException on Exception
	 */
	
	List<Patient> getArchivedPatients(String identifier, String name, String gender) throws APIException;
	
	/**
	 * Retrieves a patient from the archive
	 * 
	 * @param patient ID
	 * @return true/false
	 * @throws APIException on Exception
	 */
	
	Boolean retrievePatient(Integer patientId) throws APIException;
	
}
