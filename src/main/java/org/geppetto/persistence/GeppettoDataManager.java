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
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.data.IGeppettoDataManager;
import org.geppetto.core.data.model.ExperimentStatus;
import org.geppetto.core.data.model.IAspectConfiguration;
import org.geppetto.core.data.model.IExperiment;
import org.geppetto.core.data.model.IGeppettoProject;
import org.geppetto.core.data.model.IInstancePath;
import org.geppetto.core.data.model.IParameter;
import org.geppetto.core.data.model.IPersistedData;
import org.geppetto.core.data.model.ISimulationResult;
import org.geppetto.core.data.model.ISimulatorConfiguration;
import org.geppetto.core.data.model.IUser;
import org.geppetto.core.data.model.PersistedDataType;
import org.geppetto.core.data.model.ResultsFormat;
import org.geppetto.persistence.db.DBManager;
import org.geppetto.persistence.db.model.AspectConfiguration;
import org.geppetto.persistence.db.model.Experiment;
import org.geppetto.persistence.db.model.GeppettoProject;
import org.geppetto.persistence.db.model.InstancePath;
import org.geppetto.persistence.db.model.Parameter;
import org.geppetto.persistence.db.model.PersistedData;
import org.geppetto.persistence.db.model.SimulationResult;
import org.geppetto.persistence.db.model.SimulatorConfiguration;
import org.geppetto.persistence.db.model.User;

import com.google.gson.Gson;

public class GeppettoDataManager implements IGeppettoDataManager
{

	private static Log logger = LogFactory.getLog(GeppettoDataManager.class);

	private DBManager dbManager;

	Map<Long, GeppettoProject> projects = new ConcurrentHashMap<Long, GeppettoProject>();

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
			if(project!=null){
				for(Experiment e : project.getExperiments())
				{
					if(e !=null){
						e.setParentProject(project);
					}
				}
				projects.put(id, project);
			}
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
	public IInstancePath newInstancePath(String instancePathString)
	{
		InstancePath instancePath = new InstancePath(instancePathString);
		saveEntity(instancePath);
		return instancePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#newExperiment(java.lang.String, java.lang.String, org.geppetto.core.data.model.IGeppettoProject)
	 */
	@Override
	public IExperiment newExperiment(String name, String description, IGeppettoProject project)
	{
		Experiment experiment = new Experiment(new ArrayList<AspectConfiguration>(), name, description, new Date(), new Date(), ExperimentStatus.DESIGN, new ArrayList<SimulationResult>(), new Date(),
				new Date(), project);
		((GeppettoProject) project).getExperiments().add(experiment);
		dbManager.storeEntity(project);
		return experiment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#newUser(java.lang.String, java.lang.String, boolean)
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
	 * @see org.geppetto.core.data.IGeppettoDataManager#updateUser(org.geppetto.core.data.model.IUser, java.lang.String)
	 */
	@Override
	public IUser updateUser(IUser user, String password){
		((User)user).setPassword(password);
		dbManager.storeEntity(user);
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#addGeppettoProject(org.geppetto.core.data.model.IGeppettoProject, org.geppetto.core.data.model.IUser)
	 */
	@Override
	public void addGeppettoProject(IGeppettoProject project, IUser user)
	{
		long oldId = project.getId();
		long oldActiveExperimentId = project.getActiveExperimentId();
		String activeExperimentName = null;
		if(oldActiveExperimentId != -1)
		{
			for(IExperiment e : project.getExperiments())
			{
				if(e.getId() == oldActiveExperimentId)
				{
					activeExperimentName = e.getName();
					break;
				}
			}
		}
		project.setVolatile(false);
		dbManager.storeEntity(project);
		((User) user).getGeppettoProjects().add((GeppettoProject) project);
		dbManager.storeEntity(user);
		if(activeExperimentName != null)
		{
			for(IExperiment e : project.getExperiments())
			{
				if(e.getName().equals(activeExperimentName))
				{
					project.setActiveExperimentId(e.getId());
					break;
				}
			}
		}
		projects.remove(oldId);
		projects.put(project.getId(), (GeppettoProject) project);
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
		// everything inside an experiment is cleared automatically thanks to dependent = "true" in the entity configuration
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
		GeppettoProject project = gson.fromJson(json, GeppettoProject.class);
		project.setId(getRandomId());
		project.setVolatile(true);
		for(Experiment e : project.getExperiments())
		{
			e.setParentProject(project);
		}
		projects.put(project.getId(), project);
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#getProjectFromJson(com.google.gson.Gson, java.io.Reader)
	 */
	@Override
	public IGeppettoProject getProjectFromJson(Gson gson, Reader json)
	{
		GeppettoProject project = gson.fromJson(json, GeppettoProject.class);
		project.setId(getRandomId());
		project.setVolatile(true);
		for(Experiment e : project.getExperiments())
		{
			e.setParentProject(project);
		}
		projects.put(project.getId(), project);
		return project;
	}

	private long getRandomId()
	{
		Random rnd = new Random();
		return (long) rnd.nextInt();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#saveProject(org.geppetto.core.data.model.IGeppettoProject)
	 */
	@Override
	public void saveEntity(Object entity)
	{
		dbManager.storeEntity(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#saveProject(org.geppetto.core.data.model.IGeppettoProject)
	 */
	@Override
	public void saveEntity(IGeppettoProject entity)
	{
		if(!entity.isVolatile())
		{
			dbManager.storeEntity(entity);
		}
		else
		{
			logger.debug("Saving of volatile project " + entity + " attempted, saving not performed, need to use addGeppettoProject instead.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#saveProject(org.geppetto.core.data.model.IGeppettoProject)
	 */
	@Override
	public void saveEntity(IExperiment entity)
	{
		entity.updateLastModified();
		dbManager.storeEntity(entity);
	}

	@Override
	public ISimulationResult newSimulationResult(IInstancePath parameterPath, IPersistedData results, ResultsFormat format)
	{
		return new SimulationResult((InstancePath) parameterPath, (PersistedData) results, format);
	}


	@Override
	public IPersistedData newPersistedData(URL url, PersistedDataType type)
	{
		return new PersistedData(url.toString(), type);
	}

	@Override
	public IAspectConfiguration newAspectConfiguration(IExperiment experiment, IInstancePath instancePath, ISimulatorConfiguration simulatorConfiguration)
	{
		AspectConfiguration aspectConfiguration = new AspectConfiguration((InstancePath) instancePath, new ArrayList<InstancePath>(), new ArrayList<Parameter>(),
				(SimulatorConfiguration) simulatorConfiguration);
		saveEntity(aspectConfiguration);
		((Experiment) experiment).getAspectConfigurations().add(aspectConfiguration);
		saveEntity(experiment);
		return aspectConfiguration;
	}

	@Override
	public ISimulatorConfiguration newSimulatorConfiguration(String simulator, String conversionService, long timestep, long length)
	{
		return new SimulatorConfiguration(simulator, conversionService, timestep, length, new HashMap<String, String>());
	}

	@Override
	public void addWatchedVariable(IAspectConfiguration aspectConfiguration, IInstancePath instancePath)
	{
		((AspectConfiguration) aspectConfiguration).getWatchedVariables().add((InstancePath) instancePath);
	}

}
