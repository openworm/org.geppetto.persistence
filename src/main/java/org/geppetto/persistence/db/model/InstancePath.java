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

	private String entityInstancePath;

	private String aspect;

	private String localInstancePath;

	public InstancePath(String entityInstancePath, String aspect, String localInstancePath)
	{
		super();
		this.entityInstancePath = entityInstancePath;
		this.aspect = aspect;
		this.localInstancePath = localInstancePath;
	}

	public long getId()
	{
		return id;
	}

	public String getEntityInstancePath()
	{
		return entityInstancePath;
	}

	public void setEntityInstancePath(String entityInstancePath)
	{
		this.entityInstancePath = entityInstancePath;
	}

	public String getAspect()
	{
		return aspect;
	}

	public void setAspect(String aspect)
	{
		this.aspect = aspect;
	}

	public String getLocalInstancePath()
	{
		return localInstancePath;
	}

	public void setLocalInstancePath(String localInstancePath)
	{
		this.localInstancePath = localInstancePath;
	}

	public String getInstancePath()
	{
		String path = entityInstancePath;
		if(aspect != null && !aspect.isEmpty())
		{
			path += "." + aspect;
		}
		if(localInstancePath != null && !localInstancePath.isEmpty())
		{
			path += "." + localInstancePath;
		}
		return path;
	}

}