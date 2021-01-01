package org.openmrs.module.archival.api.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchivalDAOImpl implements ArchivalDao {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	//private DbSessionFactory sessionFactory;
	//Original code. Commented due to Hibernate version conflicts. OpenMRS dependencies that came with SDK include Hibernate 3.x. SessionFactory requires  4.x
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/*
	 * public SessionFactory getSessionFactory() { return sessionFactory; }
	 */
	
	/*
	 * public DbSessionFactory getSessionFactory() { return sessionFactory; }
	 * 
	 * public void setSessionFactory(DbSessionFactory sessionFactory) {
	 * this.sessionFactory = sessionFactory; }
	 */
	
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
	@Transactional
	public void archiveEncounter(Encounter e, Set<EncounterProvider> epSet, Set<Obs> obsSet) {
		
		ArrayList<ObsWrapper> obsList = new ArrayList<ObsWrapper>();
		
		if (obsSet != null) {
			for (Obs o : obsSet) {
				obsList.add(new ObsWrapper(o));
			}
		}
		
		Collections.sort(obsList);
		Collections.reverse(obsList);
		
		Session session = sessionFactory.getCurrentSession();
		
		try {
			
			if (epSet != null) {
				for (EncounterProvider ep : epSet) {
					//archiveEncounterProvider(ep, session);
					session.saveOrUpdate(new ArchivedEncounterProvider(ep));
					e.getEncounterProviders().remove(ep);
					session.delete(ep);
				}
			}
			
			//if (obsSet != null) {
			for (ObsWrapper ow : obsList) {
				//archiveObs(o, session);
				session.saveOrUpdate(new ArchivedObs(ow.getObs()));
				//	e.getObs().remove(o);
				
				//	session.delete(o);
				String queryString = "delete from Obs where obs_id=" + ow.getObs().getObsId();
				System.out.println("DEL OBS: " + queryString);
				Query query = session.createSQLQuery(queryString);
				query.executeUpdate();
			}
			
			session.saveOrUpdate(new ArchivedEncounter(e));
			
			Patient p = e.getPatient();
			session.delete(e);
			
			PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName("Archived");
			Person person = p.getPerson();
			PersonAttribute pa = new PersonAttribute(pat, "Yes");
			person.addAttribute(pa);
			session.saveOrUpdate(person);
			
		}
		
		catch (ConstraintViolationException cve) {
			System.out.print("CVE: ");
			cve.printStackTrace();
			
			log.error(cve.getMessage());
			//TODO Logging and proper handling
		}
		
		catch (Exception ex) {
			System.out.print("ERROR: ");
			ex.printStackTrace();
			
			log.error(ex.getMessage());
			//TODO Logging and proper handling
		}
		
	}
	
	@Override
	public void archiveEncounterProvider(EncounterProvider ep, DbSession session) throws ConstraintViolationException {
		
		session.saveOrUpdate(new ArchivedEncounterProvider(ep));
		session.delete(ep);
		
	}
	
	@Override
	public void archiveObs(Obs o, DbSession session) throws ConstraintViolationException {
		
		session.saveOrUpdate(new ArchivedObs(o));
		session.delete(o);
	}
	
	@Override
	public List<Patient> getArchivedPatients(String identifier, String name, String gender) {
		
		PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByName("Patient ID");
		
		if (pit == null) {
			return null;
		}
		
		ArrayList<PatientIdentifierType> pits = new ArrayList<PatientIdentifierType>();
		pits.add(pit);
		
		Integer pitId = pit.getId();
		
		PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName("Archived");
		
		if (pat == null) {
			return null;
		}
		
		Integer patId = pat.getId();
		
		List<Patient> patList = null;
		ArrayList<Patient> midList = new ArrayList<Patient>();
		ArrayList<Patient> finalList = new ArrayList<Patient>();
		
		/*
		 * String query = null;
		 * 
		 * query =
		 * "select * from person as p JOIN patient_identifier as pi ON p.person_id = pi.patient_id where "
		 * ;
		 * 
		 * if(identifier!=null) { query += "pi.identifier_type = " + pitId +
		 * " AND pi.identifier = '" + identifier + "'"; }
		 * 
		 * if(name != null) {
		 * 
		 * }
		 */
		
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
		
		if (finalList.size() != 0) {
			for (Patient patient : finalList) {
				System.out.println(patient.getId());
			}
		}
		
		return finalList;
	}
	
	@Override
	@Transactional
	public void retrieveArchivedPatient(Integer patientId) {
		log.info("ARCHIVAL - retrieving Patient: " + patientId);
		List<ArchivedEncounter> aeList = getArchivedEncountersForPatient(patientId);
		
		Session session = sessionFactory.getCurrentSession();
		Transaction tx = null;
		
		try {
			
			//	tx = session.beginTransaction();
			
			for (ArchivedEncounter ae : aeList) {
				retrieveArchivedEncounter(ae, session);
				
				//ArchivedEncounter ae = getArchivedEncounter(encounterId);
				
				//	List<ArchivedEncounterProvider> aepList = getArchivedEncounterProvidersForArchivedEncounter(ae
				//	        .getEncounterId());
				//	List<ArchivedObs> aoList = getArchivedObsForArchivedEncounter(ae.getEncounterId());
				
				//	Encounter e = ae.getEncounter();
				
				//	session.saveOrUpdate(e);
				
				/*
				 * for (ArchivedEncounterProvider aep : aepList) {
				 * retrieveArchivedEncounterProvider(aep.getEncounterProviderId(), session); }
				 * 
				 * for (ArchivedObs ao : aoList) { retrieveArchivedObs(ao.getArchivalObsId(),
				 * session); }
				 */
				
				//session.delete(ae);
				
			}
			
			//tx.commit();
			PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName("Archived");
			Person person = Context.getPatientService().getPatient(patientId).getPerson();
			PersonAttribute pa = new PersonAttribute(pat, "No");
			person.addAttribute(pa);
			session.saveOrUpdate(person);
		}
		
		catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			//	tx.rollback();
			//TODO Logging and proper handling
		}
		
		catch (Exception ex) {
			ex.printStackTrace();
			/*
			 * if (tx != null) tx.rollback();
			 */
			//TODO Logging and proper handling
		}
		
		/*
		 * finally { session.close(); }
		 */
		
	}
	
	@Override
	public void retrieveArchivedEncounter(ArchivedEncounter ae, Session session) {
		
		System.out.println("ARCHIVAL - retrieving E: " + ae.getEncounterId());
		
		//ArchivedEncounter ae = getArchivedEncounter(encounterId);
		
		List<ArchivedEncounterProvider> aepList = getArchivedEncounterProvidersForArchivedEncounter(ae.getEncounterId());
		List<ArchivedObs> aoList = getArchivedObsForArchivedEncounter(ae.getEncounterId());
		
		Set<EncounterProvider> epSet = new HashSet<EncounterProvider>();
		Set<Obs> oSet = new HashSet<Obs>();
		
		ArrayList<EncounterProvider> epList = new ArrayList<EncounterProvider>();
		ArrayList<Obs> oList = new ArrayList<Obs>();
		
		List<ArchivedEncounterProvider> aepDelList = new ArrayList<ArchivedEncounterProvider>();
		List<ArchivedObs> aoDelList = new ArrayList<ArchivedObs>();
		
		Encounter e = ae.getEncounter();
		
		for (ArchivedEncounterProvider aep : aepList) {
			//retrieveArchivedEncounterProvider(aep.getEncounterProviderId(), session);
			//	System.out.println("ARCHIVAL - retrieving EP: " + aep.getEncounterProviderId());
			
			//ArchivedEncounterProvider aep = getArchivedEncounterProvider(encounterProviderId);
			
			EncounterProvider ep = aep.getEncounterProvider();
			ep.setEncounter(e);
			
			System.out.println("_____________________");
			System.out.println(ep.getUuid());
			System.out.println(ep.getEncounter().getId());
			System.out.println(ep.getEncounterProviderId());
			System.out.println(ep.getEncounterRole());
			System.out.println(ep.getProvider().getId());
			System.out.println("_____________________");
			
			//	System.out.println("ARCHIVAL - adding EP: " + ep.getEncounterProviderId());
			
			epSet.add(ep);
			aepDelList.add(aep);
			epList.add(ep);
			
			//session.delete(aep);
		}
		
		for (ArchivedObs ao : aoList) {
			//retrieveArchivedObs(ao.getArchivalObsId(), session);
			//System.out.println("ARCHIVAL - retrieving Obs: " + ao.getObsId());
			//ArchivedObs ao = getArchivedObs(obsId);
			
			Obs o = ao.getObs();
			o.setEncounter(e);
			
			//System.out.println("ARCHIVAL - saving Obs: " + o.getObsId());
			//session.saveOrUpdate(o);
			
			//session.delete(ao);
			oSet.add(o);
			aoDelList.add(ao);
			oList.add(o);
		}
		
		e.setEncounterProviders(epSet);
		
		for (EncounterProvider eps : e.getEncounterProviders()) {
			System.out.println(" -- " + eps.getId() + " --");
		}
		e.setObs(oSet);
		
		//session.saveOrUpdate(e);
		
		//ADD ENCOUNTER
		String encounterQuery = ArchivalUtil.eToQuery(e);
		
		System.out.println("QQ: " + encounterQuery);
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
		
		/*
		 * for (ArchivedEncounterProvider aep : aepDelList) { session.delete(aep); }
		 * 
		 * for (ArchivedObs ao : aoDelList) { session.delete(ao); }
		 * 
		 * session.delete(ae);
		 */
		
	}
	
	@Override
	public void retrieveArchivedEncounterProvider(Integer encounterProviderId, Session session) {
		
		System.out.println("ARCHIVAL - retrieving EP: " + encounterProviderId);
		
		ArchivedEncounterProvider aep = getArchivedEncounterProvider(encounterProviderId);
		
		EncounterProvider ep = aep.getEncounterProvider();
		
		System.out.println("_____________________");
		System.out.println(ep.getUuid());
		System.out.println(ep.getEncounter().getId());
		System.out.println(ep.getEncounterProviderId());
		System.out.println(ep.getEncounterRole());
		System.out.println(ep.getProvider().getId());
		System.out.println("_____________________");
		
		session.saveOrUpdate(ep);
		
		session.delete(aep);
		
	}
	
	@Override
	public void retrieveArchivedObs(Integer obsId, Session session) {
		
		System.out.println("ARCHIVAL - retrieving Obs: " + obsId);
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
