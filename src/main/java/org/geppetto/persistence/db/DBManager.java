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

package org.geppetto.persistence.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.FetchGroup;
import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.data.model.ExperimentStatus;
import org.geppetto.core.data.model.PersistedDataType;
import org.geppetto.persistence.db.model.AspectConfiguration;
import org.geppetto.persistence.db.model.Experiment;
import org.geppetto.persistence.db.model.GeppettoProject;
import org.geppetto.persistence.db.model.InstancePath;
import org.geppetto.persistence.db.model.Parameter;
import org.geppetto.persistence.db.model.PersistedData;
import org.geppetto.persistence.db.model.SimulationResult;
import org.geppetto.persistence.db.model.SimulatorConfiguration;
import org.geppetto.persistence.db.model.User;

public class DBManager
{

	private PersistenceManagerFactory pmf;

	private static Log _logger = LogFactory.getLog(DBManager.class);

	public DBManager()
	{
		// TODO: this will be removed once we have real DB usage
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					Thread.sleep(5000);
				}
				catch(InterruptedException e)
				{
					// ignore
				}
				buildDemoProject();
			}
		}).start();
	}

	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf)
	{
		this.pmf = pmf;
	}

	public <T> void storeEntity(T entity)
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try
		{
			tx.begin();
			pm.makePersistent(entity);
			tx.commit();
		}
		catch(Exception e)
		{
			_logger.warn("Could not store data", e);
		}
		finally
		{
			if(tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}

	public <T> List<T> getAllEntities(Class<T> type)
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		try
		{
			pm.getFetchPlan().setGroup(FetchGroup.ALL);
			pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
			pm.getFetchPlan().setMaxFetchDepth(5);
			Query query = pm.newQuery(type);
			return (List<T>) query.execute();
		}
		finally
		{
			pm.close();
		}
	}

	public <T> void deleteAllEntities(Class<T> type)
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try
		{
			tx.begin();
			Query query = pm.newQuery(type);
			List<T> entities = (List<T>) query.execute();
			pm.deletePersistentAll(entities);
			tx.commit();
		}
		catch(Exception e)
		{
			_logger.warn("Could not delete data", e);
		}
		finally
		{
			if(tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}

	public <T> void deleteEntity(T entity)
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try
		{
			tx.begin();
			pm.deletePersistent(entity);
			tx.commit();
		}
		catch(Exception e)
		{
			_logger.warn("Could not delete data", e);
		}
		finally
		{
			if(tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}

	public <T> T findEntityById(Class<T> type, long id)
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		try
		{
			pm.getFetchPlan().setGroup(FetchGroup.ALL);
			pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
			pm.getFetchPlan().setMaxFetchDepth(5);
			Query query = pm.newQuery(type);
			query.setFilter("id == searchedId");
			query.declareParameters("int searchedId");
			List<T> entities = (List<T>) query.execute(id);
			if(entities.size() > 0)
			{
				return entities.get(0);
			}
			return null;
		}
		finally
		{
			pm.close();
		}
	}

	public User findUserByLogin(String login)
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		try
		{
			pm.getFetchPlan().setGroup(FetchGroup.ALL);
			pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
			pm.getFetchPlan().setMaxFetchDepth(5);
			Query query = pm.newQuery(User.class);
			query.setFilter("login == searchedLogin");
			query.declareParameters("String searchedLogin");
			List<User> users = (List<User>) query.execute(login);
			if(users.size() > 0)
			{
				return users.get(0);
			}
			return null;
		}
		finally
		{
			pm.close();
		}
	}

	private void buildDemoProject()
	{
		List<GeppettoProject> projects = getAllEntities(GeppettoProject.class);
		if(projects.size() == 0)
		{
			PersistedData geppettoModel = new PersistedData("http://github.com/openworm/org.geppetto.core/blob/datamanager/src/main/resources/project/geppettoModels/SingleComponentHH/GEPPETTO.xml",
					PersistedDataType.GEPPETTO_PROJECT);
			GeppettoProject project = new GeppettoProject("LEMS Sample Hodgkin-Huxley Neuron", geppettoModel);

			List<AspectConfiguration> aspectConfigurations1 = new ArrayList<>();
			aspectConfigurations1.add(new AspectConfiguration(new InstancePath("hhcell", "electrical", ""), null, null, new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0, null)));
			Experiment exp1 = new Experiment(aspectConfigurations1, "Experiment ready to execute", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project);

			List<AspectConfiguration> aspectConfigurations2 = new ArrayList<>();
			List<InstancePath> watchedVariables2 = new ArrayList<>();
			watchedVariables2.add(new InstancePath("hhcell", "electrical", "SimulationTree.hhpop[0].v"));
			watchedVariables2.add(new InstancePath("hhcell", "electrical", "SimulationTree.hhpop[0].bioPhys1.membraneProperties.naChans.na.m.q"));
			watchedVariables2.add(new InstancePath("hhcell", "electrical", "SimulationTree.hhpop[0].bioPhys1.membraneProperties.naChans.na.h.q"));
			watchedVariables2.add(new InstancePath("hhcell", "electrical", "SimulationTree.hhpop[0].bioPhys1.membraneProperties.kChans.k.n.q"));
			aspectConfigurations2.add(new AspectConfiguration(new InstancePath("hhcell", "electrical", ""), watchedVariables2, null, new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0,
					null)));
			List<SimulationResult> simulationResults2 = new ArrayList<>();
			simulationResults2.add(new SimulationResult(new InstancePath("hhcell", "electrical", ""), new PersistedData(
					"http://github.com/openworm/org.geppetto.core/blob/datamanager/src/main/resources/project/geppettoModels/SingleComponentHH/results.h5", PersistedDataType.RECORDING)));
			Experiment exp2 = new Experiment(aspectConfigurations2, "Executed experiment", "", new Date(), new Date(), ExperimentStatus.COMPLETED, simulationResults2, new Date(), new Date(), project);

			List<AspectConfiguration> aspectConfigurations3 = new ArrayList<>();
			List<InstancePath> watchedVariables3 = new ArrayList<>();
			watchedVariables3.add(new InstancePath("hhcell", "electrical", "SimulationTree.hhpop[0].v"));
			watchedVariables3.add(new InstancePath("hhcell", "electrical", "SimulationTree.hhpop[0].bioPhys1.membraneProperties.naChans.na.m.q"));
			watchedVariables3.add(new InstancePath("hhcell", "electrical", "SimulationTree.hhpop[0].bioPhys1.membraneProperties.naChans.na.h.q"));
			watchedVariables3.add(new InstancePath("hhcell", "electrical", "SimulationTree.hhpop[0].bioPhys1.membraneProperties.kChans.k.n.q"));
			List<Parameter> modelParameters3 = new ArrayList<>();
			modelParameters3.add(new Parameter(new InstancePath("hhcell", "electrical", "SimulationTree.hhpop[0].bioPhys1.membraneProperties.naChans.na.m.q"), "0"));
			modelParameters3.add(new Parameter(new InstancePath("hhcell", "electrical", "SimulationTree.hhpop[0].bioPhys1.membraneProperties.naChans.na.h.q"), "0"));

			aspectConfigurations3.add(new AspectConfiguration(new InstancePath("hhcell", "electrical", ""), watchedVariables3, modelParameters3, new SimulatorConfiguration("neuronSimulator",
					"lemsConversion", 0, null)));
			List<SimulationResult> simulationResults3 = new ArrayList<>();
			simulationResults3.add(new SimulationResult(new InstancePath("hhcell", "electrical", ""), new PersistedData(
					"http://github.com/openworm/org.geppetto.core/blob/datamanager/src/main/resources/project/geppettoModels/SingleComponentHH/results.h5", PersistedDataType.RECORDING)));
			Experiment exp3 = new Experiment(aspectConfigurations3, "Experiment with parameters", "", new Date(), new Date(), ExperimentStatus.DESIGN, simulationResults3, new Date(), new Date(),
					project);

			List<Experiment> experiments = new ArrayList<>();
			experiments.add(exp1);
			experiments.add(exp2);
			experiments.add(exp3);
			project.setExperiments(experiments);
			projects = new ArrayList<>();
			projects.add(project);

			long value = 1000l * 1000 * 1000;
			projects = new ArrayList<>();
			projects.add(project);
			User user = new User("guest", "Guest user", projects, value, 2 * value);
			storeEntity(user);
		}
	}

}
