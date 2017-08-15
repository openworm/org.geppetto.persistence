

package org.geppetto.persistence.db.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.IUserGroup;
import org.geppetto.core.data.model.UserPrivileges;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PersistenceCapable(detachable = "true")
public class UserGroup implements Serializable, IUserGroup {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	private String name;
	
	@Join
	@Persistent(defaultFetchGroup = "true")
	private List<UserPrivileges> privileges;

	@JsonIgnore
	private long spaceAllowance;

	@JsonIgnore
	private long simulationTimeAllowance;
	
	public UserGroup(String name, List<UserPrivileges> privileges, long spaceAllowance, long timeAllowance)
	{
		super();
		this.name = name;
		this.privileges = privileges;
		this.spaceAllowance = spaceAllowance;
		this.simulationTimeAllowance = timeAllowance;
	}
	
	@Override
	public long getId() {
		return this.id;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public long getSpaceAllowance()
	{
		return spaceAllowance;
	}

	public void setSpaceAllowance(long spaceAllowance)
	{
		this.spaceAllowance = spaceAllowance;
	}

	@Override
	public long getSimulationTimeAllowance()
	{
		return simulationTimeAllowance;
	}

	public void setSimulationTimeAllowance(long simulationTimeAllowance)
	{
		this.simulationTimeAllowance = simulationTimeAllowance;
	}

	public List<UserPrivileges> getPrivileges() {
		return this.privileges;
	}
	
	public void setPrivileges(List<UserPrivileges> privileges) {
		this.privileges = privileges;
	}
}
