package org.geppetto.persistence.db.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jdo.FetchGroup;
import javax.jdo.annotations.FetchPlan;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
@FetchPlan(name="mainPlan", maxFetchDepth=5, fetchSize=1000, fetchGroups={FetchGroup.ALL, FetchGroup.DEFAULT})
public class SimulationRun implements Serializable {
	private static final long serialVersionUID = 1;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private SimulationStatus status;
	
	private Map<String, String> parameters = new LinkedHashMap<String, String>();

	
	// TODO: add the preferredViews once we have a View object
	public SimulationRun(SimulationStatus status, Map<String, String> parameters) {
		super();
		this.status = status;
		this.parameters = parameters;
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
	
	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
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