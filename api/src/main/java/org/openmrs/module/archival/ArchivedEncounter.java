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

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;

/**
 * Please note that a corresponding table schema must be created in liquibase.xml.
 */

/**
 * This entity represents an archived encounter
 * 
 * @author ali.habib@ihsinformatics.com
 */
@Entity(name = "archival.ArchivedEncounter")
@Table(name = "archival_encounter")
public class ArchivedEncounter extends BaseOpenmrsData {
	
	public ArchivedEncounter(Encounter e) {
		this.setEncounterId(e.getEncounterId());
		this.setEncounterType(e.getEncounterType().getEncounterTypeId());
		this.setPatientId(e.getPatient().getPatientId());
		this.setLocationId(e.getLocation().getId());
		this.setFormId(e.getForm().getId());
		this.setEncounterDatetime(e.getEncounterDatetime());
		this.setUuid(e.getUuid());
		this.setChangedBy(e.getChangedBy());
		this.setCreator(e.getCreator());
		this.setDateChanged(e.getDateChanged());
		this.setDateCreated(e.getDateCreated());
		this.setDateVoided(e.getDateVoided());
		this.setVoided(e.getVoided());
		this.setVoidedBy(e.getVoidedBy());
		this.setVoidReason(e.getVoidReason());
	}
	
	public ArchivedEncounter() {
		
	}
	
	public Integer getArchivalEncounterId() {
		return archivalEncounterId;
	}
	
	public void setArchivalEncounterId(Integer archivalEncounterId) {
		this.archivalEncounterId = archivalEncounterId;
	}
	
	public Integer getEncounterId() {
		return encounterId;
	}
	
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}
	
	public Integer getEncounterType() {
		return encounterType;
	}
	
	public void setEncounterType(Integer encounterType) {
		this.encounterType = encounterType;
	}
	
	public Integer getPatientId() {
		return patientId;
	}
	
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
	public Integer getLocationId() {
		return locationId;
	}
	
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	
	public Integer getFormId() {
		return formId;
	}
	
	public void setFormId(Integer formId) {
		this.formId = formId;
	}
	
	public Date getEncounterDatetime() {
		return encounterDatetime;
	}
	
	public void setEncounterDatetime(Date encounterDatetime) {
		this.encounterDatetime = encounterDatetime;
	}
	
	/*
	 * public String getUuid() { return uuid; }
	 * 
	 * public void setUuid(String uuid) { this.uuid = uuid; }
	 */
	
	@Id
	@GeneratedValue
	@Column(name = "archival_encounter_id")
	private Integer archivalEncounterId;
	
	@Basic
	@Column(name = "encounter_id")
	private Integer encounterId;
	
	@Basic
	@Column(name = "encounter_type")
	private Integer encounterType;
	
	@Basic
	@Column(name = "patient_id")
	private Integer patientId;
	
	@Basic
	@Column(name = "location_id")
	private Integer locationId;
	
	@Basic
	@Column(name = "form_id")
	private Integer formId;
	
	// @Temporal(TemporalType.DATE)
	@Column(name = "encounter_datetime")
	private Date encounterDatetime;
	
	/*
	 * @Basic
	 * 
	 * @Column(name = "uuid", length = 38) private String uuid;
	 */
	
	@Override
	public Integer getId() {
		
		return archivalEncounterId;
	}
	
	@Override
	public void setId(Integer id) {
		this.archivalEncounterId = id;
		
	}
	
	public Encounter getEncounter() {
		Encounter e = new Encounter();
		e.setEncounterId(this.getEncounterId());
		e.setId(this.getEncounterId());
		e.setEncounterType(Context.getEncounterService().getEncounterType(this.getEncounterType()));
		//e.setPatientId(this.getPatientId());
		e.setPatient(Context.getPatientService().getPatient(this.getPatientId()));
		e.setLocation(Context.getLocationService().getLocation(this.getLocationId()));
		e.setForm(Context.getFormService().getForm(this.getFormId()));
		e.setEncounterDatetime(this.getEncounterDatetime());
		e.setUuid(this.getUuid());
		e.setChangedBy(this.getChangedBy());
		e.setCreator(this.getCreator());
		e.setDateChanged(this.getDateChanged());
		e.setDateCreated(this.getDateCreated());
		e.setDateVoided(this.getDateVoided());
		e.setVoided(this.getVoided());
		e.setVoidedBy(this.getVoidedBy());
		e.setVoidReason(this.getVoidReason());
		
		return e;
		
	}
	
}
