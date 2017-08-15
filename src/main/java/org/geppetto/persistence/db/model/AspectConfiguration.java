
package org.geppetto.persistence.db.model;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.IAspectConfiguration;
import org.geppetto.core.data.model.IParameter;

@PersistenceCapable(detachable = "true")
public class AspectConfiguration implements IAspectConfiguration
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String instance;

    @Join
    @Column(name = "watchedVariables")
	@Persistent(dependentElement = "true", defaultFetchGroup = "true")
	private List<String> watchedVariables;

	@Join
	@Persistent(dependentElement = "true", defaultFetchGroup = "true")
	private List<Parameter> modelParameters;

	@Column(name = "simulatorconfiguration_id")
	@Persistent(dependent = "true", defaultFetchGroup = "true")
	private SimulatorConfiguration simulatorConfiguration;

	public AspectConfiguration(String instance, List<String> watchedVariables, List<Parameter> modelParameter, SimulatorConfiguration simulatorConfiguration)
	{
		super();
		this.instance = instance;
		this.watchedVariables = watchedVariables;
		this.modelParameters = modelParameter;
		this.simulatorConfiguration = simulatorConfiguration;
	}

	public long getId()
	{
		return id;
	}

	public String getInstance()
	{
		return instance;
	}

	public List<String> getWatchedVariables()
	{
		return watchedVariables;
	}

	public List<Parameter> getModelParameter()
	{
		return modelParameters;
	}

	public void setModelParameter(List<Parameter> parameters)
	{
		modelParameters = parameters;
	}

	public SimulatorConfiguration getSimulatorConfiguration()
	{
		return simulatorConfiguration;
	}

	@Override
	public void addModelParameter(IParameter modelParameter)
	{
		if(modelParameter instanceof Parameter)
		{
			modelParameters.add((Parameter) modelParameter);
		}
	}

	public void setInstance(String instance)
	{
		this.instance = instance;
	}

	@Override
	public void setId(long id)
	{
		this.id = id;
	}

}