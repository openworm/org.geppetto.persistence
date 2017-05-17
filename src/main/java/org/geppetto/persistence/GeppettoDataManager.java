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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.common.GeppettoExecutionException;
import org.geppetto.core.data.DataManagerHelper;
import org.geppetto.core.data.IGeppettoDataManager;
import org.geppetto.core.data.model.ExperimentStatus;
import org.geppetto.core.data.model.IAspectConfiguration;
import org.geppetto.core.data.model.IExperiment;
import org.geppetto.core.data.model.IGeppettoProject;
import org.geppetto.core.data.model.IParameter;
import org.geppetto.core.data.model.IPersistedData;
import org.geppetto.core.data.model.ISimulationResult;
import org.geppetto.core.data.model.ISimulatorConfiguration;
import org.geppetto.core.data.model.IUser;
import org.geppetto.core.data.model.IUserGroup;
import org.geppetto.core.data.model.IView;
import org.geppetto.core.data.model.PersistedDataType;
import org.geppetto.core.data.model.ResultsFormat;
import org.geppetto.core.data.model.UserPrivileges;
import org.geppetto.persistence.db.DBManager;
import org.geppetto.persistence.db.model.AspectConfiguration;
import org.geppetto.persistence.db.model.Experiment;
import org.geppetto.persistence.db.model.GeppettoProject;
import org.geppetto.persistence.db.model.Parameter;
import org.geppetto.persistence.db.model.PersistedData;
import org.geppetto.persistence.db.model.SimulationResult;
import org.geppetto.persistence.db.model.SimulatorConfiguration;
import org.geppetto.persistence.db.model.User;
import org.geppetto.persistence.db.model.UserGroup;
import org.geppetto.persistence.db.model.View;
import org.geppetto.persistence.util.ViewSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	 * @see org.geppetto.core.data.IGeppettoDataManager#getUserGroupById(long)
	 */
	@Override
	public IUserGroup getUserGroupById(long id){
		return dbManager.findEntityById(UserGroup.class, id);	
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
				if(project.getView() == null)
				{
					project.setView(new View(null));
				}
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
		List<GeppettoProject> userProjects = user.getGeppettoProjects();

		return userProjects;
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
	public IParameter newParameter(String instancePath, String value)
	{
		Parameter parameter = new Parameter(instancePath, value);
		dbManager.storeEntity(parameter);
		return parameter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#newExperiment(java.lang.String, java.lang.String, org.geppetto.core.data.model.IGeppettoProject)
	 */
	@Override
	public IExperiment newExperiment(String name, String description, IGeppettoProject project)
	{
		Experiment experiment = new Experiment(
				new ArrayList<AspectConfiguration>(), 
				name, description, 
				new Date(), new Date(), ExperimentStatus.DESIGN, new ArrayList<SimulationResult>(), new Date(),
				new Date(), project, new View("{}"));
		((GeppettoProject) project).getExperiments().add(experiment);
		dbManager.storeEntity(project);
		
		return experiment;
	}

	@Override
	public IView newView(String view, IGeppettoProject project) {
		IView v = new View(view);
		((GeppettoProject) project).setView(v);
		dbManager.storeEntity(project);
		return v;
	}
	
	@Override
	public IView newView(String view, IExperiment experiment) {
		IView v = new View(view);
		((IExperiment) experiment).setView(v);
		dbManager.storeEntity(experiment);
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#cloneExperiment(java.lang.String, java.lang.String, 
	 * 					org.geppetto.core.data.model.IGeppettoProject,org.geppetto.core.data.model.IExperiment)
	 */
	@Override
	public IExperiment cloneExperiment(String name, String description, IGeppettoProject project, IExperiment originalExperiment)
	{	
		Experiment experiment = new Experiment(new ArrayList<AspectConfiguration>(), 
				name, description, new Date(), new Date(), 
				ExperimentStatus.DESIGN, new ArrayList<SimulationResult>(), new Date(),
				new Date(), project, null);
		((GeppettoProject) project).getExperiments().add(experiment);
		dbManager.storeEntity(project);
		Collection<? extends AspectConfiguration> collection = 
				(Collection<? extends AspectConfiguration>) originalExperiment.getAspectConfigurations();
		for(AspectConfiguration a : collection){
			if(a.getSimulatorConfiguration()!=null){
				String simulator = a.getSimulatorConfiguration().getSimulatorId();
				String conversion = a.getSimulatorConfiguration().getConversionServiceId();
				float length = a.getSimulatorConfiguration().getLength();
				float timeStep = a.getSimulatorConfiguration().getTimestep();
				Map<String,String> parameters = a.getSimulatorConfiguration().getParameters();
				List<String> watchedVariables = a.getWatchedVariables();
				List<Parameter> modelParameters = a.getModelParameter();
				ISimulatorConfiguration simulatorConfiguration = this.newSimulatorConfiguration(simulator, conversion, timeStep,length,parameters);
				AspectConfiguration aspectConfiguration = new AspectConfiguration(a.getInstance(), watchedVariables, modelParameters,
						(SimulatorConfiguration) simulatorConfiguration);
				experiment.getAspectConfigurations().add(aspectConfiguration);
				experiment.setView(new View("{}"));
			}
		}
		dbManager.storeEntity(project);
		return experiment;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.IGeppettoDataManager#newUser(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public IUser newUser(String name, String password, boolean persistent, IUserGroup group)
	{
		User user = new User(name, password, name, new ArrayList<GeppettoProject>(), group);
		
		user.addLoginTimeStamp(new Date());

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
	public void addGeppettoProject(IGeppettoProject project, IUser user) throws GeppettoExecutionException
	{
		try{
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
		}catch(Exception e){
			throw new GeppettoExecutionException(e);
		}
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
		GeppettoProject project = dbManager.findEntityById(GeppettoProject.class, experiment.getParentProject().getId());
		Experiment e = dbManager.findEntityById(Experiment.class, experiment.getId());
		project.getExperiments().remove(e);
		dbManager.storeEntity(project);
		dbManager.deleteEntity(e);
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
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(GeppettoProject.class, new ViewSerializer());
		
		GeppettoProject project = gsonBuilder.create().fromJson(json, GeppettoProject.class);
		project.setId(getRandomId());
		project.setVolatile(true);
		if(project.getView()==null){
			project.setView(new View(null));
		}
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
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(GeppettoProject.class, new ViewSerializer());
		
		GeppettoProject project = gsonBuilder.create().fromJson(json, GeppettoProject.class);
		project.setId(getRandomId());
		project.setVolatile(true);
		if(project.getView()==null){
			project.setView(new View(null));
		}
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
	public ISimulationResult newSimulationResult(String parameterPath, IPersistedData results, ResultsFormat format)
	{
		return new SimulationResult(parameterPath, (PersistedData) results, format);
	}


	@Override
	public IPersistedData newPersistedData(URL url, PersistedDataType type)
	{
		return new PersistedData(url.toString(), type);
	}

	@Override
	public IAspectConfiguration newAspectConfiguration(IExperiment experiment, String instancePath, ISimulatorConfiguration simulatorConfiguration)
	{
		AspectConfiguration aspectConfiguration = new AspectConfiguration(instancePath, new ArrayList<String>(), new ArrayList<Parameter>(),
				(SimulatorConfiguration) simulatorConfiguration);
		saveEntity(aspectConfiguration);
		((Experiment) experiment).getAspectConfigurations().add(aspectConfiguration);
		saveEntity(experiment);
		return aspectConfiguration;
	}

	@Override
	public ISimulatorConfiguration newSimulatorConfiguration(String simulator, String conversionService, float timestep, float length, Map<String,String> parameters)
	{
		return new SimulatorConfiguration(simulator, conversionService, timestep, length, parameters);
	}

	@Override
	public void addWatchedVariable(IAspectConfiguration aspectConfiguration, String instancePath)
	{
		((AspectConfiguration) aspectConfiguration).getWatchedVariables().add(instancePath);
	}

	@Override
	public IUserGroup newUserGroup(String name, List<UserPrivileges> privileges, long spaceAllowance, long timeAllowance)
	{
		return new UserGroup(name, privileges, spaceAllowance, timeAllowance);
	}

	@Override
	public void makeGeppettoProjectPublic(long projectId,boolean isPublic) throws GeppettoExecutionException {
		
		GeppettoProject project = this.getGeppettoProjectById(projectId);
		project.setPublic(isPublic);
		dbManager.storeEntity(project);
	}
}
