/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.archival.api.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.module.archival.ArchivedEncounter;
import org.openmrs.module.archival.ArchivedEncounterProvider;
import org.openmrs.module.archival.ArchivedObs;
import org.hibernate.Session;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.springframework.stereotype.Repository;

@Repository("archival.ArchivalDao")
public interface ArchivalDao {
	
	//for archival
	/**
	 * Returns a list of patients based on a query sent by user. This is a read only transaction
	 * 
	 * @param query
	 * @return list of patients
	 */
	
	List<Patient> getPatientListForArchival(String query);
	
	void archivePatient(Integer patientId);
	
	void archiveEncounter(Encounter e, Set<EncounterProvider> epSet, Set<Obs> obsSet);
	
	void archiveEncounterProvider(EncounterProvider ep, DbSession session);
	
	void archiveObs(Obs o, DbSession session);
	
	//for retrieval
	
	/**
	 * Returns a list of archived patients based on various criteria
	 * 
	 * @param identifier, name, gender, from archive date, to archive date, archiving user
	 * @return list of patients
	 * @throws APIException on Exception
	 */
	
	List<Patient> getArchivedPatients(String identifier, String name, String gender);
	
	void retrieveArchivedPatient(Integer patientId);
	
	void retrieveArchivedEncounter(Integer archivedEncounterId, Session session);
	
	void retrieveArchivedEncounterProvider(Integer archivedEncounterProviderId, Session session);
	
	void retrieveArchivedObs(Integer archivedObsId, Session session);
	
	ArchivedEncounter getArchivedEncounter(Integer encounterId);
	
	ArchivedEncounterProvider getArchivedEncounterProvider(Integer encounterProviderId);
	
	ArchivedObs getArchivedObs(Integer archivedObsId);
	
	List<ArchivedEncounter> getArchivedEncountersForPatient(Integer patientId);
	
	List<ArchivedEncounterProvider> getArchivedEncounterProvidersForArchivedEncounter(Integer encounterId);
	
	List<ArchivedObs> getArchivedObsForArchivedEncounter(Integer encounterId);
	
}
