package org.geppetto.persistence.db.model;

import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.ISimulatorConfiguration;

@PersistenceCapable(detachable = "true")
public class SimulatorConfiguration implements ISimulatorConfiguration
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String simulatorId;

	private String conversionServiceId;

	//TODO: We are using SI units but in the future we should add scaling factor
	private float timestep;

	//TODO: We are using SI units but in the future we should add scaling factor
	private float length;

	@Join
	@Persistent(defaultFetchGroup = "true")
	private Map<String, String> parameters;

	public SimulatorConfiguration(String simulatorId, String conversionServiceId, float timestep, float length, Map<String, String> parameters)
	{
		super();
		this.simulatorId = simulatorId;
		this.conversionServiceId = conversionServiceId;
		this.timestep = timestep;
		this.length = length;
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

	public float getTimestep()
	{
		return timestep;
	}

	public void setTimestep(float timestep)
	{
		this.timestep = timestep;
	}
	
	public float getLength()
	{
		return length;
	}

	public void setLength(float length)
	{
		this.length = length;
	}

	public Map<String, String> getParameters()
	{
		return parameters;
	}

	public void setParameters(Map<String, String> parameters)
	{
		this.parameters = parameters;
	}

	@Override
	public void setId(long id)
	{
		this.id=id;
	}

}