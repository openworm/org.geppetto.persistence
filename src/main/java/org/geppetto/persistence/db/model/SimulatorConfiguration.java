package org.geppetto.persistence.db.model;

import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.ISimulatorConfiguration;

@PersistenceCapable
public class SimulatorConfiguration implements ISimulatorConfiguration
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String simulatorId;

	private String conversionServiceId;

	private String timestep;

	private Map<String, String> parameters;

	public SimulatorConfiguration(String simulatorId, String conversionServiceId, String timestep, Map<String, String> parameters)
	{
		super();
		this.simulatorId = simulatorId;
		this.conversionServiceId = conversionServiceId;
		this.timestep = timestep;
		this.parameters = parameters;
	}

	public long getId()
	{
		return id;
	}

	public String getSimulatorId()
	{
		return simulatorId;
	}

	public void setSimulatorId(String simulatorId)
	{
		this.simulatorId = simulatorId;
	}

	public String getConversionServiceId()
	{
		return conversionServiceId;
	}

	public void setConversionServiceId(String conversionServiceId)
	{
		this.conversionServiceId = conversionServiceId;
	}

	public String getTimestep()
	{
		return timestep;
	}

	public void setTimestep(String timestep)
	{
		this.timestep = timestep;
	}

	public Map<String, String> getParameters()
	{
		return parameters;
	}

	public void setParameters(Map<String, String> parameters)
	{
		this.parameters = parameters;
	}

}