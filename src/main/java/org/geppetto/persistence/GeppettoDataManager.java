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
package org.geppetto.persistence;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.geppetto.core.data.IGeppettoDataManager;
import org.geppetto.core.data.model.ExperimentStatus;
import org.geppetto.core.data.model.IAspectConfiguration;
import org.geppetto.core.data.model.IExperiment;
import org.geppetto.core.data.model.IGeppettoProject;
import org.geppetto.core.data.model.IInstancePath;
import org.geppetto.core.data.model.IParameter;
import org.geppetto.core.data.model.ISimulationResult;
import org.geppetto.core.data.model.IUser;
import org.geppetto.persistence.db.DBManager;
import org.geppetto.persistence.db.model.AspectConfiguration;
import org.geppetto.persistence.db.model.Experiment;
import org.geppetto.persistence.db.model.GeppettoProject;
import org.geppetto.persistence.db.model.InstancePath;
import org.geppetto.persistence.db.model.Parameter;
import org.geppetto.persistence.db.model.SimulationResult;
import org.geppetto.persistence.db.model.User;

import com.google.gson.Gson;

public class GeppettoDataManager implements IGeppettoDataManager
{

	private DBManager dbManager;
	
	Map<Long,GeppettoProject> projects=new ConcurrentHashMap<Long,GeppettoProject>();

	public void setDbManager(DBManager manager)
	{
		dbManager = manager;
	}

	@Override
	public String getName()
	{
		return "DB data manager";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#getAllUsers()
	 */
	@Override
	public List<User> getAllUsers()
	{
		return dbManager.getAllEntities(User.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#getAllGeppettoProjects()
	 */
	@Override
	public List<GeppettoProject> getAllGeppettoProjects()
	{
		return dbManager.getAllEntities(GeppettoProject.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#getUserByLogin(java.lang.String)
	 */
	@Override
	public User getUserByLogin(String login)
	{
		return dbManager.findUserByLogin(login);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#getGeppettoProjectById(long)
	 */
	@Override
	public GeppettoProject getGeppettoProjectById(long id)
	{
		if(!projects.containsKey(id))
		{
			GeppettoProject project = dbManager.findEntityById(GeppettoProject.class, id);
			for(Experiment e:project.getExperiments())
			{
				e.setParentProject(project);
			}
			projects.put(id,project);
		}
		return projects.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#getGeppettoProjectsForUser(java.lang.String)
	 */
	@Override
	public List<GeppettoProject> getGeppettoProjectsForUser(String login)
	{
		User user = dbManager.findUserByLogin(login);
		return user.getGeppettoProjects();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#getExperimentsForProject(long)
	 */
	@Override
	public List<Experiment> getExperimentsForProject(long projectId)
	{
		GeppettoProject project = getGeppettoProjectById(projectId);
		return project.getExperiments();
	}

	public ISimulationResult newSimulationResult()
	{
		SimulationResult result = new SimulationResult(null, null);
		dbManager.storeEntity(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#createParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public IParameter newParameter(IInstancePath instancePath, String value)
	{
		Parameter parameter = new Parameter((InstancePath) instancePath, value);
		dbManager.storeEntity(parameter);
		return parameter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#newInstancePath(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IInstancePath newInstancePath(String entityPath, String aspectPath, String localPath)
	{
		InstancePath instancePath = new InstancePath(entityPath, aspectPath, localPath);
		dbManager.storeEntity(instancePath);
		return instancePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#newExperiment(java.lang.String, java.lang.String)
	 */
	@Override
	public IExperiment newExperiment(String name, String description, IGeppettoProject project)
	{
		Experiment experiment = new Experiment(new ArrayList<AspectConfiguration>(), name, description, new Date(), new Date(), ExperimentStatus.DESIGN, new ArrayList<SimulationResult>(), new Date(),
				new Date(), project);
		dbManager.storeEntity(experiment);
		return experiment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#newUser(java.lang.String)
	 */
	@Override
	public IUser newUser(String name, String password, boolean persistent)
	{
		User user = new User(name, password, name, new ArrayList<GeppettoProject>(), 0, 0);
		if(persistent)
		{
			dbManager.storeEntity(user);
		}
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#addGeppettoProject(org.geppetto.core.data.model.IGeppettoProject)
	 */
	@Override
	public void addGeppettoProject(IGeppettoProject project)
	{
		dbManager.storeEntity(project);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#deleteGeppettoProject(org.geppetto.core.data.model.IGeppettoProject)
	 */
	@Override
	public Object deleteGeppettoProject(long id, IUser user)
	{
		dbManager.deleteProject(id, user);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#deleteExperiment(org.geppetto.core.data.model.IExperiment)
	 */
	@Override
	public Object deleteExperiment(IExperiment experiment)
	{
		dbManager.deleteEntity(experiment);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#getProjectFromJson(com.google.gson.Gson, java.lang.String)
	 */
	@Override
	public IGeppettoProject getProjectFromJson(Gson gson, String json)
	{
		return gson.fromJson(json, GeppettoProject.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#getProjectFromJson(com.google.gson.Gson, java.io.Reader)
	 */
	@Override
	public IGeppettoProject getProjectFromJson(Gson gson, Reader json)
	{
		return gson.fromJson(json, GeppettoProject.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#clearWatchedVariables(org.geppetto.core.data.model.IAspectConfiguration)
	 */
	@Override
	public void clearWatchedVariables(IAspectConfiguration aspectConfig)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.geppetto.core.data.IGeppettoDataManager#saveProject(org.geppetto.core.data.model.IGeppettoProject)
	 */
	@Override
	public void saveProject(IGeppettoProject project)
	{
		dbManager.storeEntity(project);
	}

	/* (non-Javadoc)
	 * @see org.geppetto.core.data.IGeppettoDataManager#saveExperiment(org.geppetto.core.data.model.IExperiment)
	 */
	@Override
	public void saveExperiment(IExperiment experiment)
	{
		dbManager.storeEntity(experiment);		
	}

}
