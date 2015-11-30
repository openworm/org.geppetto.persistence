package org.geppetto.persistence.db.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.IInstancePath;

@PersistenceCapable(detachable = "true")
public class InstancePath implements IInstancePath
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String instancePath;

	public InstancePath(String instancePathString)
	{
		super();
		this.instancePath = instancePathString;
	}

	public long getId()
	{
		return id;
	}
	

	public String getInstancePath()
	{
		return instancePath;
	}

	@Override
	public void setId(long id)
	{
		this.id = id;
	}

}