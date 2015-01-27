package org.geppetto.persistence.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class SimulationRun implements Serializable {
	private static final long serialVersionUID = 1;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private SimulationStatus status;
	
	// TODO: figure this out
//	private Map<String, String> parameters = new LinkedHashMap<String, String>();

	
	// TODO: add the preferredViews once we have a View object
	public SimulationRun(SimulationStatus status) {
		super();
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public SimulationStatus getStatus() {
		return status;
	}

	public void setStatus(SimulationStatus status) {
		this.status = status;
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((SimulationRun) obj).id;
	}
	
	@Override
	public int hashCode() {
		return status.hashCode();
	}

}