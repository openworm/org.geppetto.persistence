/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2011 - 2015 OpenWorm.
 * http://openworm.org
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *
 * Contributors:
 *     	OpenWorm - http://openworm.org/people.html
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
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

	@Column(name = "aspect_id")
	@Persistent(dependent = "true", defaultFetchGroup = "true")
	private InstancePath aspect;

	@Join
	@Persistent(defaultFetchGroup = "true")
	private List<InstancePath> watchedVariables;

	@Join
	@Persistent(defaultFetchGroup = "true")
	private List<Parameter> modelParameters;

	@Column(name = "simulatorconfiguration_id")
	@Persistent(dependent = "true",defaultFetchGroup = "true")
	private SimulatorConfiguration simulatorConfiguration;

	public AspectConfiguration(InstancePath aspect, List<InstancePath> watchedVariables, List<Parameter> modelParameter, SimulatorConfiguration simulatorConfiguration)
	{
		super();
		this.aspect = aspect;
		this.watchedVariables = watchedVariables;
		this.modelParameters = modelParameter;
		this.simulatorConfiguration = simulatorConfiguration;
	}

	public long getId()
	{
		return id;
	}

	public InstancePath getAspect()
	{
		return aspect;
	}

	public List<InstancePath> getWatchedVariables()
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

	public void setAspect(InstancePath aspect)
	{
		this.aspect=aspect;		
	}

}