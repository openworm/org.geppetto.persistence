package org.geppetto.persistence.db.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.FetchGroup;
import javax.jdo.annotations.FetchPlan;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
@FetchPlan(name="mainPlan", maxFetchDepth=5, fetchSize=1000, fetchGroups={FetchGroup.ALL, FetchGroup.DEFAULT})
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String login;
	
	private String name;
	
	private long spaceAllowance;
	
	private long simulationTimeAllowance;
	
	@Join
	private List<GeppettoProject> geppettoProjects;
	
	// TODO: do we store an encrypted password here?
	public User(String login, String name, List<GeppettoProject> geppettoProjects, long spaceAllowance, long simulationTimeAllowance) {
		super();
		this.login = login;
		this.name = name;
		this.geppettoProjects = geppettoProjects;
		this.spaceAllowance = spaceAllowance;
		this.simulationTimeAllowance = simulationTimeAllowance;
	}

	public long getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<GeppettoProject> getGeppettoProjects() {
		return geppettoProjects;
	}

	public void setGeppettoProjects(List<GeppettoProject> geppettoProjects) {
		this.geppettoProjects = geppettoProjects;
	}

	public long getSpaceAllowance() {
		return spaceAllowance;
	}

	public void setSpaceAllowance(long spaceAllowance) {
		this.spaceAllowance = spaceAllowance;
	}

	public long getSimulationTimeAllowance() {
		return simulationTimeAllowance;
	}

	public void setSimulationTimeAllowance(long simulationTimeAllowance) {
		this.simulationTimeAllowance = simulationTimeAllowance;
	}

}