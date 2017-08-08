

package org.geppetto.persistence.db.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.IParameter;

@PersistenceCapable(detachable = "true")
public class Parameter implements Serializable, IParameter
{
	private static final long serialVersionUID = 1;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String variable;

	private String value;

	public Parameter(String variable, String value)
	{
		super();
		this.variable = variable;
		this.value = value;
	}

	public long getId()
	{
		return id;
	}

	public String getVariable()
	{
		return variable;
	}

	public void setVariable(String instancePath)
	{
		this.variable = instancePath;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

}