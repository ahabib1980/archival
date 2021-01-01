package org.openmrs.module.archival;

import org.openmrs.Obs;

public class ObsWrapper implements Comparable<ObsWrapper> {
	
	private Obs obs;
	
	public ObsWrapper(Obs o) {
		this.obs = o;
	}
	
	public Obs getObs() {
		return obs;
	}
	
	public void setObs(Obs obs) {
		this.obs = obs;
	}
	
	public int compareTo(ObsWrapper ow) {
		return this.getObs().getObsId() - ow.getObs().getId();
		
	}
}
