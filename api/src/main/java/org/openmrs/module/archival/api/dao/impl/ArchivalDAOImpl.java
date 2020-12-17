package org.openmrs.module.archival.api.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.archival.ArchivedEncounter;
import org.openmrs.module.archival.ArchivedEncounterProvider;
import org.openmrs.module.archival.ArchivedObs;
import org.openmrs.module.archival.api.dao.ArchivalDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArchivalDAOImpl implements ArchivalDao {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
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
		
		for (List<Object> l : idList) {
			Integer id = (Integer) (l.get(0));
			list.add(Context.getPatientService().getPatient(id));
		}
		
		return list;
	}
	
	@Override
	public void archivePatient(Integer patientId) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void archiveEncounter(Encounter e, Set<EncounterProvider> epSet, Set<Obs> obsSet) {
		
		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		
		try {
			
			for (EncounterProvider ep : epSet) {
				archiveEncounterProvider(ep, session);
			}
			
			for (Obs o : obsSet) {
				archiveObs(o, session);
			}
			
			session.saveOrUpdate(new ArchivedEncounter(e));
			session.delete(e);
			
			tx.commit();
			
		}
		
		catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			tx.rollback();
			//TODO Logging and proper handling
		}
	}
	
	@Override
	public void archiveEncounterProvider(EncounterProvider ep, Session session) throws ConstraintViolationException {
		
		session.saveOrUpdate(new ArchivedEncounterProvider(ep));
		session.delete(ep);
		
	}
	
	@Override
	public void archiveObs(Obs o, Session session) throws ConstraintViolationException {
		
		session.saveOrUpdate(new ArchivedObs(o));
		session.delete(o);
	}
	
	@Override
	public List<Patient> getArchivedPatients(String identifier, String name, String gender, Date fromDate, Date toDate,
	        User archivedBy) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void retrieveArchivedPatient(Integer patientId) {
		List<ArchivedEncounter> aeList = getArchivedEncountersForPatient(patientId);
		
		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		
		try {
			
			for (ArchivedEncounter ae : aeList) {
				retrieveArchivedEncounter(ae.getEncounterId(), session);
				
			}
			
			tx.commit();
		}
		
		catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			tx.rollback();
			//TODO Logging and proper handling
		}
		
	}
	
	@Override
	public void retrieveArchivedEncounter(Integer encounterId, Session session) {
		
		ArchivedEncounter ae = getArchivedEncounter(encounterId);
		
		List<ArchivedEncounterProvider> aepList = getArchivedEncounterProvidersForArchivedEncounter(ae.getEncounterId());
		List<ArchivedObs> aoList = getArchivedObsForArchivedEncounter(ae.getEncounterId());
		
		//TODO code to retrieve Encounter
		Encounter e = ae.getEncounter();
		
		session.saveOrUpdate(e);
		
		for (ArchivedEncounterProvider aep : aepList) {
			retrieveArchivedEncounterProvider(aep.getEncounterProviderId(), session);
		}
		
		for (ArchivedObs ao : aoList) {
			retrieveArchivedObs(ao.getArchivalObsId(), session);
		}
		
		session.delete(ae);
		
	}
	
	@Override
	public void retrieveArchivedEncounterProvider(Integer encounterProviderId, Session session) {
		ArchivedEncounterProvider aep = getArchivedEncounterProvider(encounterProviderId);
		
		EncounterProvider ep = aep.getEncounterProvider();
		
		session.saveOrUpdate(ep);
		
		session.delete(aep);
		
	}
	
	@Override
	public void retrieveArchivedObs(Integer obsId, Session session) {
		ArchivedObs ao = getArchivedObs(obsId);
		
		Obs o = ao.getObs();
		
		session.saveOrUpdate(o);
		
		session.delete(ao);
		
	}
	
	@Override
	public ArchivedEncounter getArchivedEncounter(Integer encounterId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedEncounter.class);
		criteria.add(Restrictions.eq("encounterId", encounterId));
		return (ArchivedEncounter) criteria.uniqueResult();
	}
	
	@Override
	public ArchivedEncounterProvider getArchivedEncounterProvider(Integer encounterProviderId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedEncounterProvider.class);
		criteria.add(Restrictions.eq("archivedEncounterProviderId", encounterProviderId));
		return (ArchivedEncounterProvider) criteria.uniqueResult();
		
	}
	
	@Override
	public ArchivedObs getArchivedObs(Integer archivedObsId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedObs.class);
		criteria.add(Restrictions.eq("archivedObsId", archivedObsId));
		return (ArchivedObs) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ArchivedEncounter> getArchivedEncountersForPatient(Integer patientId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedEncounter.class);
		criteria.addOrder(Order.asc("archivedEncounterId"));
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
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ArchivedEncounterProvider.class);
		criteria.addOrder(Order.asc("obsId"));
		criteria.add(Restrictions.eq("encounterId", encounterId));
		
		return criteria.list();
	}
	
}
