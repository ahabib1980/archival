package org.openmrs.module.archival.api.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void retrieveArchivedEncounter(Integer archivedEncounterId) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void retrieveArchivedEncounterProvider(Integer archivedEncounterProviderId, Session session) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void retrieveArchivedObs(Integer archivedObsId, Session session) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ArchivedEncounter getArchivedEncounter(Integer encounterId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArchivedEncounterProvider getArchivedEncounterProvider(Integer encounterProviderId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArchivedObs getArchivedObs(Integer archivedObsId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<ArchivedEncounter> getArchivedEncountersForPatient(Integer patientId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<ArchivedEncounterProvider> getArchivedEncounterProvidersForArchivedEncounter(Integer encounterId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<ArchivedObs> getArchivedObsForArchivedEncounter(Integer encounterId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
