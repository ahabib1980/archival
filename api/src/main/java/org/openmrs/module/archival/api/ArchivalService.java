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
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.archival.ArchivalResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 * 
 * @author ali.habib@ihsinformatics.com
 */

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
	 * Retrieves a list of patient encounters
	 * 
	 * @param patient id
	 * @return list of all encounters for a given patient
	 * @throws
	 */
	
	List<Integer> getPatientEncounterIds(Integer patientId) throws APIException;
	
	/**
	 * Archives an encounter
	 * 
	 * @param EncounterId
	 * @return true if success, false if failure
	 * @throws
	 */
	
	Boolean archiveEncounter(Integer encounterId);//, Set<EncounterProvider> epSet, Set<Obs> obsSet) throws APIException;
	
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
