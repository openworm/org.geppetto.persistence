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
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.IUser;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PersistenceCapable(detachable = "true")
public class User implements Serializable, IUser
{

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String login;

	@JsonIgnore
	private String password;

	private String name;

	@JsonIgnore
	private long spaceAllowance;

	@JsonIgnore
	private long simulationTimeAllowance;

	@Join
	@JsonIgnore
	@Persistent(defaultFetchGroup = "true")
	private List<GeppettoProject> geppettoProjects;

	@JsonIgnore
	private String dropboxToken;

	public User(String login, String password, String name, List<GeppettoProject> geppettoProjects, long spaceAllowance, long simulationTimeAllowance)
	{
		super();
		this.login = login;
		this.password = password;
		this.name = name;
		this.geppettoProjects = geppettoProjects;
		this.spaceAllowance = spaceAllowance;
		this.simulationTimeAllowance = simulationTimeAllowance;
	}

	public long getId()
	{
		return id;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<GeppettoProject> getGeppettoProjects()
	{
		return geppettoProjects;
	}

	public void setGeppettoProjects(List<GeppettoProject> geppettoProjects)
	{
		this.geppettoProjects = geppettoProjects;
	}

	public long getSpaceAllowance()
	{
		return spaceAllowance;
	}

	public void setSpaceAllowance(long spaceAllowance)
	{
		this.spaceAllowance = spaceAllowance;
	}

	public long getSimulationTimeAllowance()
	{
		return simulationTimeAllowance;
	}

	public void setSimulationTimeAllowance(long simulationTimeAllowance)
	{
		this.simulationTimeAllowance = simulationTimeAllowance;
	}
	
	@Override
	public String getDropboxToken()
	{
		return this.dropboxToken;
	}

	@Override
	public void setDropboxToken(String token)
	{
		this.dropboxToken=token;
	}


}