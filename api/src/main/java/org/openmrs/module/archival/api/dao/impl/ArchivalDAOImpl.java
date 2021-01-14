package org.openmrs.module.archival.api.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.archival.ArchivedEncounter;
import org.openmrs.module.archival.ArchivedEncounterProvider;
import org.openmrs.module.archival.ArchivedObs;
import org.openmrs.module.archival.ObsWrapper;
import org.openmrs.module.archival.api.dao.ArchivalDao;
import org.openmrs.module.archival.util.ArchivalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class ArchivalDAOImpl implements ArchivalDao {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public List<Patient> getPatientListForArchival(String query) {
		ArrayList<Patient> list = new ArrayList<Patient>();
		AdministrationService service = Context.getAdministrationService();
		List<List<Object>> idList = service.executeSQL(query, true);
		Patient patient = null;
		PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName("Archived");
		Person person = null;
		PersonAttribute pa = null;
		
		for (List<Object> l : idList) {
			Integer id = (Integer) (l.get(0));
			patient = Context.getPatientService().getPatient(id);
			if (patient != null) {
				person = patient.getPerson();
				pa = person.getAttribute(pat.getId());
				
				//limit the returned list to patients who are not already archived
				if (pa == null || pa.getValue().equals("No")) {
					list.add(Context.getPatientService().getPatient(id));
				}
			}
		}
		
		return list;
	}
	
	public void archiveEncounter(Encounter e, Set<EncounterProvider> epSet, Set<Obs> obsSet) {
		
		//TODO: return detail based on how many archived, and how many failed
		
		Session session = sessionFactory.getCurrentSession();
		
		Logger.getAnonymousLogger().info("ARCHIVING - " + e.getId());
		
		ArrayList<ObsWrapper> obsList = new ArrayList<ObsWrapper>();
		
		if (obsSet != null) {
			for (Obs o : obsSet) {
				obsList.add(new ObsWrapper(o));
			}
		}
		
		Collections.sort(obsList);
		Collections.reverse(obsList);
		
		if (epSet != null) {
			for (EncounterProvider ep : epSet) {
				
				session.saveOrUpdate(new ArchivedEncounterProvider(ep));
				e.getEncounterProviders().remove(ep);
				session.delete(ep);
			}
		}
		
		for (ObsWrapper ow : obsList) {
			
			session.saveOrUpdate(new ArchivedObs(ow.getObs()));
			
			//query used here because OpenMRS technically does not apper to allow Obs to be edited as of v2.0
			//so session.saveOrUpdate() fails with odd exceptions. A direct query works around this.
			String queryString = "delete from Obs where obs_id=" + ow.getObs().getObsId();
			Logger.getAnonymousLogger().info("DEL OBS: " + queryString);
			Query query = session.createSQLQuery(queryString);
			query.executeUpdate();
		}
		
		session.saveOrUpdate(new ArchivedEncounter(e));
		
		Person person = e.getPatient().getPerson();
		PersonAttributeType paType = Context.getPersonService().getPersonAttributeTypeByName("Archived");
		PersonAttribute pa = person.getAttribute(paType);
		
		if (pa == null || !pa.getValue().equals("Yes")) {
			person.addAttribute(new PersonAttribute(paType, "Yes"));
			session.saveOrUpdate(person);
		}
		
		session.delete(e);
		
	}
	
	@Override
	public List<Patient> getArchivedPatients(String identifier, String name, String gender) {
		
		PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByName("Patient ID");
		
		if (pit == null) {
			return null;
		}
		
		ArrayList<PatientIdentifierType> pits = new ArrayList<PatientIdentifierType>();
		pits.add(pit);
		
		PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName("Archived");
		
		if (pat == null) {
			return null;
		}
		
		Integer patId = pat.getId();
		
		List<Patient> patList = null;
		ArrayList<Patient> midList = new ArrayList<Patient>();
		ArrayList<Patient> finalList = new ArrayList<Patient>();
		
		patList = Context.getPatientService().getPatients(name, identifier, pits, true);
		
		if (patList != null && patList.size() != 0) {
			if (gender != null) {
				for (Patient p : patList) {
					if (p.getGender().equalsIgnoreCase(gender.substring(0, 1))) {
						midList.add(p);
					}
				}
			}
			
			else {
				for (Patient p : patList) {
					midList.add(p);
				}
			}
			
			if (midList.size() != 0) {
				//check for Archived Identifier and add to final List
				for (Patient p : midList) {
					Person person = Context.getPersonService().getPerson(p.getId());
					
					PersonAttribute pa = person.getAttribute(patId);
					
					if (pa != null && pa.getValue().equalsIgnoreCase("yes")) {
						finalList.add(p);
					}
				}
			}
			
		}
		
		return finalList;
	}
	
	@Override
	public void retrieveArchivedPatient(Integer patientId) {
		
		//try {
		
		Logger.getAnonymousLogger().info("ARCHIVAL - retrieving Patient: " + patientId);
		List<ArchivedEncounter> aeList = getArchivedEncountersForPatient(patientId);
		
		Session session = sessionFactory.getCurrentSession();
		
		//this should all happen in a single Tx - either retrieve everything or nothing
		List<ArchivedEncounterProvider> aepList = null;
		List<ArchivedObs> aoList = null;
		
		for (ArchivedEncounter ae : aeList) {
			
			Logger.getAnonymousLogger().info("ARCHIVAL - retrieving E: " + ae.getEncounterId());
			
			aepList = getArchivedEncounterProvidersForArchivedEncounter(ae.getEncounterId());
			aoList = getArchivedObsForArchivedEncounter(ae.getEncounterId());
			
			Set<EncounterProvider> epSet = new HashSet<EncounterProvider>();
			Set<Obs> oSet = new HashSet<Obs>();
			
			ArrayList<EncounterProvider> epList = new ArrayList<EncounterProvider>();
			ArrayList<Obs> oList = new ArrayList<Obs>();
			
			List<ArchivedEncounterProvider> aepDelList = new ArrayList<ArchivedEncounterProvider>();
			List<ArchivedObs> aoDelList = new ArrayList<ArchivedObs>();
			
			Encounter e = ae.getEncounter();
			
			for (ArchivedEncounterProvider aep : aepList) {
				
				EncounterProvider ep = aep.getEncounterProvider();
				ep.setEncounter(e);
				
				epSet.add(ep);
				aepDelList.add(aep);
				epList.add(ep);
				
			}
			
			for (ArchivedObs ao : aoList) {
				
				Obs o = ao.getObs();
				o.setEncounter(e);
				
				oSet.add(o);
				aoDelList.add(ao);
				oList.add(o);
			}
			
			e.setEncounterProviders(epSet);
			
			e.setObs(oSet);
			
			//ADD ENCOUNTER
			String encounterQuery = ArchivalUtil.eToQuery(e);
			
			Query query = session.createSQLQuery(encounterQuery);
			query.executeUpdate();
			
			for (EncounterProvider ep : epList) {
				String epQuery = ArchivalUtil.epToQuery(ep);
				query = session.createSQLQuery(epQuery);
				query.executeUpdate();
			}
			
			for (Obs o : oList) {
				String oQuery = ArchivalUtil.oToQuery(o);
				query = session.createSQLQuery(oQuery);
				query.executeUpdate();
				
			}
			
			session.delete(ae);
			
			for (ArchivedEncounterProvider delAep : aepDelList) {
				session.delete(delAep);
			}
			
			for (ArchivedObs delAo : aoDelList) {
				session.delete(delAo);
			}
			
		}
		
		PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName("Archived");
		Person person = Context.getPatientService().getPatient(patientId).getPerson();
		person.addAttribute(new PersonAttribute(pat, "No"));
		session.saveOrUpdate(person);
		
	}
	
	//@Override
	//public void retrieveArchivedEncounter(ArchivedEncounter ae, Session session) {
	
	/*
	 * for (ArchivedEncounterProvider aep : aepDelList) { session.delete(aep); }
	 * 
	 * for (ArchivedObs ao : aoDelList) { session.delete(ao); }
	 * 
	 * session.delete(ae);
	 */
	
	//}
	
	/*
	 * @Override public void retrieveArchivedEncounterProvider(Integer
	 * encounterProviderId, Session session) {
	 * 
	 * Logger.getAnonymousLogger().info("ARCHIVAL - retrieving EP: " +
	 * encounterProviderId);
	 * 
	 * ArchivedEncounterProvider aep =
	 * getArchivedEncounterProvider(encounterProviderId);
	 * 
	 * EncounterProvider ep = aep.getEncounterProvider();
	 * 
	 * session.saveOrUpdate(ep);
	 * 
	 * session.delete(aep);
	 * 
	 * }
	 */
	
	/*
	 * @Override public void retrieveArchivedObs(Integer obsId, Session session) {
	 * 
	 * Logger.getAnonymousLogger().info("ARCHIVAL - retrieving Obs: " + obsId);
	 * ArchivedObs ao = getArchivedObs(obsId);
	 * 
	 * Obs o = ao.getObs();
	 * 
	 * session.saveOrUpdate(o);
	 * 
	 * session.delete(ao);
	 * 
	 * }
	 */
	
	@Override
	public ArchivedEncounter getArchivedEncounter(Integer encounterId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedEncounter.class);
		criteria.add(Restrictions.eq("encounterId", encounterId));
		return (ArchivedEncounter) criteria.uniqueResult();
	}
	
	@Override
	public ArchivedEncounterProvider getArchivedEncounterProvider(Integer encounterProviderId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedEncounterProvider.class);
		criteria.add(Restrictions.eq("encounterProviderId", encounterProviderId));
		return (ArchivedEncounterProvider) criteria.uniqueResult();
		
	}
	
	@Override
	public ArchivedObs getArchivedObs(Integer archivedObsId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedObs.class);
		criteria.add(Restrictions.eq("archivalObsId", archivedObsId));
		return (ArchivedObs) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ArchivedEncounter> getArchivedEncountersForPatient(Integer patientId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedEncounter.class);
		criteria.addOrder(Order.asc("archivalEncounterId"));
		criteria.add(Restrictions.eq("patientId", patientId));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ArchivedEncounterProvider> getArchivedEncounterProvidersForArchivedEncounter(Integer encounterId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedEncounterProvider.class);
		criteria.addOrder(Order.asc("encounterProviderId"));
		criteria.add(Restrictions.eq("encounterId", encounterId));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ArchivedObs> getArchivedObsForArchivedEncounter(Integer encounterId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedObs.class);
		criteria.addOrder(Order.asc("obsId"));
		criteria.add(Restrictions.eq("encounterId", encounterId));
		
		return criteria.list();
	}
	
}
