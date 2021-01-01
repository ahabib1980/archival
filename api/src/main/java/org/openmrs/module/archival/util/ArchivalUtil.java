package org.openmrs.module.archival.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;

;

public class ArchivalUtil {
	
	public static String utilDatetoSqlDate(Date utilDate) {
		String sqlString = "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
		sqlString = sdf.format(utilDate);
		
		return sqlString;
		
	}
	
	public static String epToQuery(EncounterProvider ep) {
		String queryString = "INSERT INTO encounter_provider (encounter_provider_id, encounter_id, provider_id,encounter_role_id,creator,date_created,voided,voided_by,date_voided,void_reason,changed_by,date_changed,uuid) VALUES (";
		queryString += ep.getEncounterProviderId() + ",";
		queryString += ep.getEncounter().getId() + ",";
		
		queryString += ep.getProvider().getId() + ",";
		queryString += ep.getEncounterRole().getId() + ",";
		
		queryString += ep.getCreator().getId() + ",";
		queryString += "'" + ArchivalUtil.utilDatetoSqlDate(ep.getDateCreated()) + "',";
		queryString += ep.getVoided() + ",";
		
		if (ep.getVoidedBy() != null) {
			queryString += ep.getVoidedBy().getId() + ",";
		}
		
		else {
			queryString += null + ",";
		}
		
		if (ep.getDateVoided() != null) {
			queryString += "'" + ArchivalUtil.utilDatetoSqlDate(ep.getDateVoided()) + "',";
		}
		
		else {
			queryString += null + ",";
		}
		
		if (ep.getVoidReason() != null)
			queryString += "'" + ep.getVoidReason() + "',";
		else
			queryString += null + ",";
		
		if (ep.getChangedBy() != null) {
			queryString += ep.getChangedBy().getId() + ",";
		}
		
		else {
			queryString += null + ",";
		}
		
		if (ep.getDateChanged() != null) {
			queryString += "'" + ArchivalUtil.utilDatetoSqlDate(ep.getDateChanged()) + "',";
		}
		
		else {
			queryString += null + ",";
		}
		
		queryString += "'" + ep.getUuid() + "')";
		
		System.out.println("EP Query:" + queryString);
		
		return queryString;
	}
	
	public static String eToQuery(Encounter e) {
		String queryString = "INSERT INTO encounter (encounter_id, encounter_type, patient_id,location_id,form_id,encounter_datetime,creator,date_created,voided,voided_by,date_voided,void_reason,changed_by,date_changed,uuid) VALUES (";
		queryString += e.getEncounterId() + ",";
		queryString += e.getEncounterType().getId() + ",";
		queryString += e.getPatient().getPatientId() + ",";
		queryString += e.getLocation().getLocationId() + ",";
		if (e.getForm() != null) {
			queryString += e.getForm().getId() + ",";
		} else {
			queryString += null + ",";
		}
		
		queryString += "'" + ArchivalUtil.utilDatetoSqlDate(e.getEncounterDatetime()) + "',";
		queryString += e.getCreator().getId() + ",";
		queryString += "'" + ArchivalUtil.utilDatetoSqlDate(e.getDateCreated()) + "',";
		queryString += e.getVoided() + ",";
		
		if (e.getVoidedBy() != null) {
			queryString += e.getVoidedBy().getId() + ",";
		}
		
		else {
			queryString += null + ",";
		}
		
		if (e.getDateVoided() != null) {
			queryString += "'" + ArchivalUtil.utilDatetoSqlDate(e.getDateVoided()) + "',";
		}
		
		else {
			queryString += null + ",";
		}
		
		if (e.getVoidReason() != null)
			queryString += "'" + e.getVoidReason() + "',";
		else
			queryString += null + ",";
		
		if (e.getChangedBy() != null) {
			queryString += e.getChangedBy().getId() + ",";
		}
		
		else {
			queryString += null + ",";
		}
		
		if (e.getDateChanged() != null) {
			queryString += "'" + ArchivalUtil.utilDatetoSqlDate(e.getDateChanged()) + "',";
		}
		
		else {
			queryString += null + ",";
		}
		
		queryString += "'" + e.getUuid() + "')";
		
		System.out.println("Enc Query:" + queryString);
		
		return queryString;
	}
	
	public static String oToQuery(Obs o) {
		String queryString = "INSERT INTO obs (obs_id, person_id, concept_id,encounter_id,order_id,obs_datetime,location_id,obs_group_id,accession_number,value_group_id,value_coded,value_coded_name_id,value_drug,value_datetime,value_numeric,value_modifier,value_text,value_complex,comments,creator,date_created,voided,voided_by,date_voided,void_reason,uuid,previous_version,status) VALUES (";
		
		queryString += o.getId() + ",";
		queryString += o.getPersonId() + ",";
		queryString += o.getConcept().getId() + ",";
		queryString += o.getEncounter().getEncounterId() + ",";
		
		if (o.getOrder() != null)
			queryString += o.getOrder().getId() + ",";
		else
			queryString += null + ",";
		
		queryString += "'" + utilDatetoSqlDate(o.getObsDatetime()) + "',";
		
		queryString += o.getLocation().getId() + ",";
		
		if (o.getObsGroup() != null)
			queryString += o.getObsGroup().getObsId() + ",";
		else
			queryString += null + ",";
		
		if (o.getAccessionNumber() != null)
			queryString += "'" + o.getAccessionNumber() + "',";
		else
			queryString += null + ",";
		
		if (o.getValueGroupId() != null)
			queryString += o.getValueGroupId() + ",";
		else
			queryString += null + ",";
		
		if (o.getValueCoded() != null)
			queryString += o.getValueCoded().getConceptId() + ",";
		else
			queryString += null + ",";
		
		if (o.getValueCodedName() != null)
			queryString += o.getValueCodedName().getConceptNameId() + ",";
		else
			queryString += null + ",";
		
		if (o.getValueDrug() != null)
			queryString += o.getValueDrug().getDrugId() + ",";
		else
			queryString += null + ",";
		
		if (o.getValueDatetime() != null)
			queryString += "'" + utilDatetoSqlDate(o.getValueDatetime()) + "',";
		else
			queryString += null + ",";
		
		if (o.getValueNumeric() != null)
			queryString += o.getValueNumeric() + ",";
		else
			queryString += null + ",";
		
		if (o.getValueModifier() != null)
			queryString += "'" + o.getValueModifier() + "',";
		else
			queryString += null + ",";
		
		if (o.getValueText() != null)
			queryString += "'" + o.getValueText() + "',";
		else
			queryString += null + ",";
		
		if (o.getValueComplex() != null)
			queryString += "'" + o.getValueComplex() + "',";
		else
			queryString += null + ",";
		
		if (o.getComment() != null)
			queryString += "'" + o.getComment() + "',";
		else
			queryString += null + ",";
		
		queryString += o.getCreator().getId() + ",";
		queryString += "'" + ArchivalUtil.utilDatetoSqlDate(o.getDateCreated()) + "',";
		queryString += o.getVoided() + ",";
		
		if (o.getVoidedBy() != null) {
			queryString += o.getVoidedBy().getId() + ",";
		}
		
		else {
			queryString += null + ",";
		}
		
		if (o.getDateVoided() != null) {
			queryString += "'" + ArchivalUtil.utilDatetoSqlDate(o.getDateVoided()) + "',";
		}
		
		else {
			queryString += null + ",";
		}
		
		if (o.getVoidReason() != null)
			queryString += "'" + o.getVoidReason() + "',";
		else
			queryString += null + ",";
		
		queryString += "'" + o.getUuid() + "',";
		
		if (o.getPreviousVersion() != null) {
			queryString += o.getPreviousVersion().getObsId() + ",";
		}
		
		else {
			queryString += null + ",";
		}
		
		if (o.getStatus() != null)
			queryString += "'" + o.getStatus() + "'";
		else
			queryString += null;
		
		queryString += ")";
		
		/*
		 * if (o.getInterpretation() != null) { queryString +=
		 * o.getInterpretation().toString(); }
		 */
		System.out.println("Obs Query:" + queryString);
		return queryString;
	}
	
}
