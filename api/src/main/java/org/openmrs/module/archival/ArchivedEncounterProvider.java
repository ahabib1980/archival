/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.archival;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.EncounterProvider;

/**
 * Please note that a corresponding table schema must be created in liquibase.xml.
 */

/**
 * This entity represents an archived encounter provider
 * 
 * @author ali.habib@ihsinformatics.com
 */
@Entity(name = "archival.ArchivedEncounter")
@Table(name = "archival_encounter")
public class ArchivedEncounterProvider extends BaseOpenmrsData {
	
	public ArchivedEncounterProvider(EncounterProvider ep) {
		// TODO Fill this out
	}
	
	@Override
	public Integer getId() {
		
		return archivalEncounterProviderId;
	}
	
	@Override
	public void setId(Integer id) {
		this.archivalEncounterProviderId = id;
		
	}
	
	public Integer getArchivalProviderEncounterId() {
		return archivalEncounterProviderId;
	}
	
	public void setArchivalEncounterProviderId(Integer archivalEncounterProviderId) {
		this.archivalEncounterProviderId = archivalEncounterProviderId;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "archival_encounter_provider_id")
	private Integer archivalEncounterProviderId;
	
}
