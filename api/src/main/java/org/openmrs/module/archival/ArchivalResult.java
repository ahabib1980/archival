package org.openmrs.module.archival;

import java.util.ArrayList;

import org.openmrs.Patient;

public class ArchivalResult {
	
	private ArrayList<Patient> successList;
	
	private ArrayList<Patient> failureList;
	
	public ArchivalResult() {
		successList = new ArrayList<Patient>();
		failureList = new ArrayList<Patient>();
		
	}
	
	public ArrayList<Patient> getSuccessList() {
		return successList;
	}
	
	public ArrayList<Patient> getFailureList() {
		return failureList;
	}
	
	public void addSuccess(Patient p) {
		successList.add(p);
	}
	
	public void addFailure(Patient p) {
		failureList.add(p);
	}
	
}
