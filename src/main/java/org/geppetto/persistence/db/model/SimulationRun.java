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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.ISimulationRun;
import org.geppetto.core.data.model.SimulationStatus;

@PersistenceCapable
public class SimulationRun implements Serializable, ISimulationRun
{
	private static final long serialVersionUID = 1;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private SimulationStatus status;

	@Column(name = "results_id")
	@Persistent(dependent = "true")
	private PersistedData results;

	@Join
	@Persistent
	private List<Parameter> simulationParameters;

	private Date startDate;

	private Date endDate;

	public SimulationRun(SimulationStatus status, List<Parameter> simulationParameters, PersistedData results, Date startDate, Date endDate)
	{
		super();
		this.status = status;
		this.simulationParameters = simulationParameters;
		this.results = results;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public long getId()
	{
		return id;
	}

	public SimulationStatus getStatus()
	{
		return status;
	}

	public void setStatus(SimulationStatus status)
	{
		this.status = status;
	}

	public PersistedData getResults()
	{
		return results;
	}

	public void setResults(PersistedData results)
	{
		this.results = results;
	}

	public List<Parameter> getSimulationParameters()
	{
		return simulationParameters;
	}

	public void setSimulationParameters(List<Parameter> simulationParameters)
	{
		this.simulationParameters = simulationParameters;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	public boolean equals(Object obj)
	{
		return id == ((SimulationRun) obj).id;
	}

	public int hashCode()
	{
		return status.hashCode();
	}

}