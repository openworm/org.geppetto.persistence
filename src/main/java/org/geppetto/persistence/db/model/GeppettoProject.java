package org.geppetto.persistence.db.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.FetchGroup;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.FetchPlan;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
@FetchPlan(name="mainPlan", maxFetchDepth=5, fetchSize=1000, fetchGroups={FetchGroup.ALL, FetchGroup.DEFAULT})
public class GeppettoProject implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String name;

	@Column(name="persisteddata_id")
	private PersistedData geppettoModel;

	@Join
	private List<SimulationRun> simulationRuns;

	public GeppettoProject(String name, PersistedData geppettoModel, List<SimulationRun> simulationRuns) {
		super();
		this.name = name;
		this.geppettoModel = geppettoModel;
		this.simulationRuns = simulationRuns;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PersistedData getGeppettoModel() {
		return geppettoModel;
	}

	public void setGeppettoModel(PersistedData geppettoModel) {
		this.geppettoModel = geppettoModel;
	}
	
	public List<SimulationRun> getSimulationRuns() {
		return simulationRuns;
	}

	public void setSimulationRuns(List<SimulationRun> simulationRuns) {
		this.simulationRuns = simulationRuns;
	}

	@Override
	public boolean equals(Object obj) {
		return id == ((GeppettoProject) obj).id;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}