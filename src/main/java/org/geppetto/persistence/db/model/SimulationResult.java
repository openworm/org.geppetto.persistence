

package org.geppetto.persistence.db.model;

import java.io.Serializable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.ISimulationResult;
import org.geppetto.core.data.model.ResultsFormat;

@PersistenceCapable(detachable = "true")
public class SimulationResult implements Serializable, ISimulationResult
{
	private static final long serialVersionUID = 1;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String simulatedInstance;

	@Column(name = "result_id")
	@Persistent(dependent = "true", defaultFetchGroup = "true")
	private PersistedData result;

	private ResultsFormat format;

	public SimulationResult(String simulatedInstance, PersistedData result, ResultsFormat format)
	{
		super();
		this.simulatedInstance = simulatedInstance;
		this.result = result;
		this.format = format;
	}

	public long getId()
	{
		return id;
	}

	public String getSimulatedInstance()
	{
		return simulatedInstance;
	}

	public void setSimulatedInstance(String simulatedInstance)
	{
		this.simulatedInstance = simulatedInstance;
	}

	public PersistedData getResult()
	{
		return result;
	}

	public void setResult(PersistedData result)
	{
		this.result = result;
	}

	@Override
	public void setId(long id)
	{
		this.id = id;
	}

	@Override
	public ResultsFormat getFormat()
	{
		return this.format;
	}

}