/**
 * Copyright(C) 2020 Interactive Health Solutions, Pvt. Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
 * You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
 * Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 * Contributors: Tahira Niazi
 */
package org.openmrs.module.archival.web.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;

/**
 * @author tahira.niazi@ihsinformatics.com
 */
public class PatientDto {
	
	private Integer patientId;
	
	private String identifier;
	
	private String patientName;
	
	private String gender;
	
	private String dob;
	
	private String enrolledPrograms;
	
	@SuppressWarnings("deprecation")
	public PatientDto(Patient patient) {
		this.patientId = patient.getPatientId();
		this.identifier = patient.getPatientIdentifier() == null ? "" : patient.getPatientIdentifier().getIdentifier();
		this.patientName = patient.getPerson().getPersonName().getFullName();
		this.gender = patient.getGender().equals("M") ? "Male" : "Female";
		DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		this.dob = dateFormat.format(patient.getPerson().getBirthdate());
		//		String programString = "";
		//		List<PatientProgram> programs = (List<PatientProgram>) Context.getProgramWorkflowService().getPatientPrograms(
		//		    patient);
		//		if (programs.size() > 0) {
		//			for (PatientProgram patientProgram : programs) {
		//				programString.concat(patientProgram.getProgram().getName());
		//				programString.concat(" ,");
		//			}
		//			this.enrolledPrograms = programString.substring(0, programString.length() - 2);
		//		} else
		//			this.enrolledPrograms = "";
	}
	
	public Integer getPatientId() {
		return patientId;
	}
	
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getPatientName() {
		return patientName;
	}
	
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getDob() {
		return dob;
	}
	
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	public String getEnrolledPrograms() {
		return enrolledPrograms;
	}
	
	public void setEnrolledPrograms(String enrolledPrograms) {
		this.enrolledPrograms = enrolledPrograms;
	}
}
