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
import org.openmrs.Obs;
import org.openmrs.api.context.Context;

/**
 * Please note that a corresponding table schema must be created in liquibase.xml.
 */

/**
 * This entity represents an archived encounter
 * 
 * @author ali.habib@ihsinformatics.com
 */
@Entity(name = "archival.ArchivedObs")
@Table(name = "archival_obs")
public class ArchivedObs extends BaseOpenmrsData {
	
	public ArchivedObs(Obs o) {
		// TODO fill this out 
		this.setObsId(o.getId());
		this.setPersonId(o.getPersonId());
		this.setConceptId(o.getConcept().getConceptId());
		this.setEncounterId(o.getEncounter().getEncounterId());
		
		if (o.getOrder() != null)
			this.setOrderId(o.getOrder().getOrderId());
		else
			this.setOrderId(null);
		
		this.setObsDatetime(o.getObsDatetime());
		
		if (o.getLocation() != null)
			this.setLocationId(o.getLocation().getLocationId());
		else
			this.setLocationId(null);
		
		if (o.getObsGroup() != null)
			this.setObsGroupId(o.getObsGroup().getId());
		else
			this.setObsGroupId(null);
		
		this.setAccessionNumber(o.getAccessionNumber());
		this.setValueGroupId(o.getValueGroupId());
		if (o.getValueCoded() != null)
			this.setValueCoded(o.getValueCoded().getConceptId());
		else
			this.setValueCoded(null);
		
		if (o.getValueCodedName() != null)
			this.setValueCodedNameId(o.getValueCodedName().getConceptNameId());
		else
			this.setValueCodedNameId(null);
		
		if (this.getValueDrug() != null)
			this.setValueDrug(o.getValueDrug().getDrugId());
		else
			this.setValueDrug(null);
		
		this.setValueDatetime(o.getValueDatetime());
		this.setValueNumeric(o.getValueNumeric());
		this.setValueModifier(o.getValueModifier());
		this.setValueText(o.getValueText());
		this.setValueComplex(o.getValueComplex());
		this.setComments(o.getComment());
		this.setUuid(this.getUuid());
		this.setPreviousVersion(this.getPreviousVersion());
		this.setFormNamespaceAndPath(this.getFormNamespaceAndPath());
		this.setStatus(this.getStatus());
		this.setInterpretation(this.getInterpretation());
		this.setChangedBy(this.getChangedBy());
		this.setCreator(this.getCreator());
		this.setDateChanged(this.getDateChanged());
		this.setDateCreated(this.getDateCreated());
		this.setDateVoided(this.getDateVoided());
		this.setVoided(this.getVoided());
		this.setVoidedBy(this.getVoidedBy());
		this.setVoidReason(this.getVoidReason());
		
	}
	
	public Integer getArchivalObsId() {
		return archivalObsId;
	}
	
	public void setArchivalObsId(Integer archivalObsId) {
		this.archivalObsId = archivalObsId;
	}
	
	/*
	 * public String getUuid() { return uuid; }
	 * 
	 * public void setUuid(String uuid) { this.uuid = uuid; }
	 */
	
	@Id
	@GeneratedValue
	@Column(name = "archival_obs_id")
	private Integer archivalObsId;
	
	@Basic
	@Column(name = "obs_id")
	private Integer obsId;
	
	@Basic
	@Column(name = "person_id")
	private Integer personId;
	
	@Basic
	@Column(name = "concept_id")
	private Integer conceptId;
	
	@Basic
	@Column(name = "encounter_id")
	private Integer encounterId;
	
	@Basic
	@Column(name = "order_id")
	private Integer orderId;
	
	// @Temporal(TemporalType.DATE)
	@Column(name = "obs_datetime")
	private Date obsDatetime;
	
	@Basic
	@Column(name = "location_id")
	private Integer locationId;
	
	@Basic
	@Column(name = "obs_group_id")
	private Integer obsGroupId;
	
	@Basic
	@Column(name = "accession_number", length = 255)
	private String accessionNumber;
	
	@Basic
	@Column(name = "value_group_id")
	private Integer valueGroupId;
	
	@Basic
	@Column(name = "value_coded")
	private Integer valueCoded;
	
	@Basic
	@Column(name = "value_coded_name_id")
	private Integer valueCodedNameId;
	
	@Basic
	@Column(name = "value_drug")
	private Integer valueDrug;
	
	// @Temporal(TemporalType.DATE)
	@Column(name = "value_datetime")
	private Date valueDatetime;
	
	@Basic
	@Column(name = "value_numeric")
	private Double valueNumeric;
	
	@Basic
	@Column(name = "value_modifier", length = 2)
	private String valueModifier;
	
	@Basic
	@Column(name = "value_text")
	private String valueText;
	
	@Basic
	@Column(name = "value_complex", length = 1000)
	private String valueComplex;
	
	@Basic
	@Column(name = "comments", length = 255)
	private String comments;
	
	/*
	 * @Basic
	 * 
	 * @Column(name = "uuid", length = 38) private String uuid;
	 */
	
	@Basic
	@Column(name = "previous_version")
	private Integer previousVersion;
	
	@Basic
	@Column(name = "form_namespace_and_path", length = 255)
	private String formNamespaceAndPath;
	
	@Basic
	@Column(name = "status", length = 16)
	private String status;
	
	@Basic
	@Column(name = "interpretation", length = 32)
	private String interpretation;
	
	@Override
	public Integer getId() {
		
		return archivalObsId;
	}
	
	@Override
	public void setId(Integer id) {
		this.archivalObsId = id;
		
	}
	
	public Integer getObsId() {
		return obsId;
	}
	
	public void setObsId(Integer obsId) {
		this.obsId = obsId;
	}
	
	public Integer getPersonId() {
		return personId;
	}
	
	public void setPersonId(Integer personId) {
		this.personId = personId;
	}
	
	public Integer getConceptId() {
		return conceptId;
	}
	
	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}
	
	public Integer getEncounterId() {
		return encounterId;
	}
	
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}
	
	public Integer getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	public Date getObsDatetime() {
		return obsDatetime;
	}
	
	public void setObsDatetime(Date obsDatetime) {
		this.obsDatetime = obsDatetime;
	}
	
	public Integer getLocationId() {
		return locationId;
	}
	
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	
	public Integer getObsGroupId() {
		return obsGroupId;
	}
	
	public void setObsGroupId(Integer obsGroupId) {
		this.obsGroupId = obsGroupId;
	}
	
	public String getAccessionNumber() {
		return accessionNumber;
	}
	
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	
	public Integer getValueGroupId() {
		return valueGroupId;
	}
	
	public void setValueGroupId(Integer valueGroupId) {
		this.valueGroupId = valueGroupId;
	}
	
	public Integer getValueCoded() {
		return valueCoded;
	}
	
	public void setValueCoded(Integer valueCoded) {
		this.valueCoded = valueCoded;
	}
	
	public Integer getValueCodedNameId() {
		return valueCodedNameId;
	}
	
	public void setValueCodedNameId(Integer valueCodedNameId) {
		this.valueCodedNameId = valueCodedNameId;
	}
	
	public Integer getValueDrug() {
		return valueDrug;
	}
	
	public void setValueDrug(Integer valueDrug) {
		this.valueDrug = valueDrug;
	}
	
	public Date getValueDatetime() {
		return valueDatetime;
	}
	
	public void setValueDatetime(Date valueDatetime) {
		this.valueDatetime = valueDatetime;
	}
	
	public Double getValueNumeric() {
		return valueNumeric;
	}
	
	public void setValueNumeric(Double valueNumeric) {
		this.valueNumeric = valueNumeric;
	}
	
	public String getValueModifier() {
		return valueModifier;
	}
	
	public void setValueModifier(String valueModifier) {
		this.valueModifier = valueModifier;
	}
	
	public String getValueText() {
		return valueText;
	}
	
	public void setValueText(String valueText) {
		this.valueText = valueText;
	}
	
	public String getValueComplex() {
		return valueComplex;
	}
	
	public void setValueComplex(String valueComplex) {
		this.valueComplex = valueComplex;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public Integer getPreviousVersion() {
		return previousVersion;
	}
	
	public void setPreviousVersion(Integer previousVersion) {
		this.previousVersion = previousVersion;
	}
	
	public String getFormNamespaceAndPath() {
		return formNamespaceAndPath;
	}
	
	public void setFormNamespaceAndPath(String formNamespaceAndPath) {
		this.formNamespaceAndPath = formNamespaceAndPath;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getInterpretation() {
		return interpretation;
	}
	
	public void setInterpretation(String interpretation) {
		this.interpretation = interpretation;
	}
	
	public Obs getObs() {
		
		Obs o = new Obs();
		
		o.setObsId(this.getId());
		o.setPerson(Context.getPersonService().getPerson(this.getPersonId()));
		o.setConcept(Context.getConceptService().getConcept(this.getConceptId()));
		o.setEncounter(Context.getEncounterService().getEncounter(this.getEncounterId()));
		
		if (this.getOrderId() != null)
			o.setOrder(Context.getOrderService().getOrder(this.getOrderId()));
		
		o.setObsDatetime(this.getObsDatetime());
		
		if (this.getLocationId() != null)
			o.setLocation(Context.getLocationService().getLocation(this.getLocationId()));
		
		if (this.getObsGroupId() != null)
			o.setObsGroup(Context.getObsService().getObs(this.getObsGroupId()));
		
		o.setAccessionNumber(this.getAccessionNumber());
		o.setValueGroupId(this.getValueGroupId());
		
		if (this.getValueCoded() != null)
			o.setValueCoded(Context.getConceptService().getConcept(this.getValueCoded()));
		
		if (this.getValueCodedNameId() != null)
			o.setValueCodedName(Context.getConceptService().getConceptName(this.getValueCodedNameId()));
		
		if (this.getValueDrug() != null)
			o.setValueDrug(Context.getConceptService().getDrug(this.getValueDrug()));
		
		o.setValueDatetime(this.getValueDatetime());
		o.setValueNumeric(this.getValueNumeric());
		o.setValueModifier(this.getValueModifier());
		o.setValueText(this.getValueText());
		o.setValueComplex(this.getValueComplex());
		o.setComment(this.getComments());
		o.setUuid(this.getUuid());
		
		if (this.previousVersion != null)
			o.setPreviousVersion(Context.getObsService().getObs(this.getPreviousVersion()));
		//o.getF
		/*o.setStatus(this.getStatus());
		o.setInterpretation(this.getInterpretation());*/
		o.setChangedBy(this.getChangedBy());
		o.setCreator(this.getCreator());
		o.setDateChanged(this.getDateChanged());
		o.setDateCreated(this.getDateCreated());
		o.setDateVoided(this.getDateVoided());
		o.setVoided(this.getVoided());
		o.setVoidedBy(this.getVoidedBy());
		o.setVoidReason(this.getVoidReason());
		
		return o;
	}
	
}
