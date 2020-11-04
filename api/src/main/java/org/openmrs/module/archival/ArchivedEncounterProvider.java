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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.EncounterProvider;
import org.openmrs.api.context.Context;

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
		this.setEncounterId(ep.getEncounter().getEncounterId());
		this.setEncounterProviderId(ep.getEncounterProviderId());
		this.setEncounterRoleId(ep.getEncounterRole().getId());
		this.setProviderId(ep.getProvider().getProviderId());
		this.setUuid(this.getUuid());
		this.setChangedBy(ep.getChangedBy());
		this.setCreator(ep.getCreator());
		this.setDateChanged(ep.getDateChanged());
		this.setDateCreated(ep.getDateCreated());
		this.setDateVoided(ep.getDateVoided());
		this.setVoided(ep.getVoided());
		this.setVoidedBy(ep.getVoidedBy());
		this.setVoidReason(ep.getVoidReason());
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
	
	public Integer getEncounterProviderId() {
		return encounterProviderId;
	}
	
	public void setEncounterProviderId(Integer encounterProviderId) {
		this.encounterProviderId = encounterProviderId;
	}
	
	public Integer getEncounterId() {
		return encounterId;
	}
	
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}
	
	public Integer getProviderId() {
		return providerId;
	}
	
	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}
	
	public Integer getEncounterRoleId() {
		return encounterRoleId;
	}
	
	public void setEncounterRoleId(Integer encounterRoleId) {
		this.encounterRoleId = encounterRoleId;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public Integer getArchivalEncounterProviderId() {
		return archivalEncounterProviderId;
	}
	
	public EncounterProvider getEncounterProvider() {
		
		EncounterProvider ep = new EncounterProvider();
		
		ep.setEncounter(Context.getEncounterService().getEncounter(this.getEncounterId()));
		ep.setEncounterProviderId(this.getEncounterProviderId());
		ep.setEncounterRole(Context.getEncounterService().getEncounterRole(this.getEncounterRoleId()));
		ep.setProvider(Context.getProviderService().getProvider(this.getEncounterProviderId()));
		ep.setUuid(this.getUuid());
		ep.setChangedBy(this.getChangedBy());
		ep.setCreator(this.getCreator());
		ep.setDateChanged(this.getDateChanged());
		ep.setDateCreated(this.getDateCreated());
		ep.setDateVoided(this.getDateVoided());
		ep.setVoided(this.getVoided());
		ep.setVoidedBy(this.getVoidedBy());
		ep.setVoidReason(this.getVoidReason());
		
		return ep;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "archival_encounter_provider_id")
	private Integer archivalEncounterProviderId;
	
	@Basic
	@Column(name = "encounter_provider_id")
	private Integer encounterProviderId;
	
	@Basic
	@Column(name = "encounter_id")
	private Integer encounterId;
	
	@Basic
	@Column(name = "provider_id")
	private Integer providerId;
	
	@Basic
	@Column(name = "encounter_role_id")
	private Integer encounterRoleId;
	
	@Basic
	@Column(name = "uuid", length = 38)
	private String uuid;
	
}
