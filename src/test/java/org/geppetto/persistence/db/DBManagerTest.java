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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.geppetto.core.data.model.ExperimentStatus;
import org.geppetto.core.data.model.IAspectConfiguration;
import org.geppetto.core.data.model.IExperiment;
import org.geppetto.core.data.model.IGeppettoProject;
import org.geppetto.core.data.model.IPersistedData;
import org.geppetto.core.data.model.ISimulationResult;
import org.geppetto.core.data.model.ISimulatorConfiguration;
import org.geppetto.core.data.model.IUser;
import org.geppetto.core.data.model.PersistedDataType;
import org.geppetto.core.data.model.ResultsFormat;
import org.geppetto.core.data.model.UserPrivileges;
import org.geppetto.persistence.GeppettoDataManager;
import org.geppetto.persistence.db.model.AspectConfiguration;
import org.geppetto.persistence.db.model.Experiment;
import org.geppetto.persistence.db.model.GeppettoProject;
import org.geppetto.persistence.db.model.PersistedData;
import org.geppetto.persistence.db.model.SimulationResult;
import org.geppetto.persistence.db.model.User;
import org.geppetto.persistence.util.DBTestData;
import org.junit.Assert;
import org.junit.Test;

public class DBManagerTest
{

	private DBManager db = new DBManager();

	private User user;

	private GeppettoDataManager dataManager;

	public DBManagerTest()
	{
		db.setPersistenceManagerFactory(DBTestData.getPersistenceManagerFactory());
		dataManager = new GeppettoDataManager();
		dataManager.setDbManager(db);
	}

	@Test
	public void testUserGroups(){
		user = db.findUserByLogin("guest1");
		
		Assert.assertTrue(user != null);
		Assert.assertTrue(user.getUserGroup() != null);
		Assert.assertTrue(user.getUserGroup().getPrivileges().size() == 5);
		Assert.assertTrue(user.getUserGroup().getPrivileges().get(0) == UserPrivileges.READ_PROJECT);
	}
	
	@Test
	public void testExperimentCreate()
	{
		user = db.findUserByLogin("guest1");
		GeppettoProject project = user.getGeppettoProjects().get(0);
		int count = project.getExperiments().size();
		int allCount = db.getAllEntities(Experiment.class).size();
		Experiment experiment = new Experiment(null, "test exp", "test exp", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project);
		project.getExperiments().add(experiment);
		db.storeEntity(project);

		project = db.findEntityById(GeppettoProject.class, project.getId());
		Assert.assertEquals(count + 1, project.getExperiments().size());
		Assert.assertEquals(allCount + 1, db.getAllEntities(Experiment.class).size());
		Experiment experiment2 = new Experiment(null, "test exp", "test exp", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project);
		// standalone experiment, not added to a project
		db.storeEntity(experiment2);
		project = db.findEntityById(GeppettoProject.class, project.getId());
		Assert.assertEquals(count + 1, project.getExperiments().size());
		Assert.assertEquals(allCount + 2, db.getAllEntities(Experiment.class).size());

		experiment = db.findEntityById(Experiment.class, experiment.getId());
		project.getExperiments().remove(experiment);
		db.storeEntity(project);
		db.deleteEntity(experiment);
		db.deleteEntity(experiment2);
		project = db.findEntityById(GeppettoProject.class, project.getId());
		Assert.assertEquals(count, project.getExperiments().size());
		Assert.assertEquals(allCount, db.getAllEntities(Experiment.class).size());
	}

	@Test
	public void testSimulatorResultsUpdate() throws MalformedURLException
	{
		user = db.findUserByLogin("guest1");
		Experiment experiment = db.findEntityById(Experiment.class, 1l);
		Assert.assertEquals(0, experiment.getSimulationResults().size());
		String aspect = new String("hhcell");
		PersistedData recording = new PersistedData("http://testURL", PersistedDataType.RECORDING);
		ISimulationResult results = new SimulationResult(aspect, recording, ResultsFormat.GEPPETTO_RECORDING);
		db.storeEntity(results);
		experiment.addSimulationResult(results);
		Assert.assertEquals(results, experiment.getSimulationResults().get(0));
		Assert.assertEquals(recording, experiment.getSimulationResults().get(0).getResult());
		Assert.assertEquals(aspect, experiment.getSimulationResults().get(0).getSimulatedInstance());
		db.storeEntity(experiment);
		Assert.assertEquals(results, experiment.getSimulationResults().get(0));
		Assert.assertEquals(recording, experiment.getSimulationResults().get(0).getResult());
		Assert.assertEquals(aspect, experiment.getSimulationResults().get(0).getSimulatedInstance());
		experiment.getSimulationResults().clear();
		db.storeEntity(experiment);
		db.deleteEntity(recording);
		db.deleteEntity(results);
		db.storeEntity(experiment);
		Assert.assertEquals(0, experiment.getSimulationResults().size());
	}

	@Test
	public void testExperimentUpdate()
	{
		user = db.findUserByLogin("guest1");
		Experiment experiment = db.findEntityById(Experiment.class, 1l);
		experiment.setStatus(ExperimentStatus.COMPLETED);
		db.storeEntity(experiment);
		experiment = db.findEntityById(Experiment.class, 1l);
		Assert.assertEquals(ExperimentStatus.COMPLETED, experiment.getStatus());
		experiment.setStatus(ExperimentStatus.DESIGN);
		db.storeEntity(experiment);
		experiment = db.findEntityById(Experiment.class, 1l);
		Assert.assertEquals(ExperimentStatus.DESIGN, experiment.getStatus());
	}

	@Test
	public void testNewExperiment()
	{
		user = db.findUserByLogin("guest1");
		GeppettoProject project = db.findEntityById(GeppettoProject.class, 1l);
		IExperiment experiment = dataManager.newExperiment("E", "D", project);
		Assert.assertNotNull(experiment.getName());
		String instancePath = "entity.aspect";
		ISimulatorConfiguration simulatorConfiguration = dataManager.newSimulatorConfiguration("", "", 0l, 0l);
		IAspectConfiguration aspectConfiguration = dataManager.newAspectConfiguration(experiment, instancePath, simulatorConfiguration);
		Assert.assertNotNull(experiment.getName());
		Assert.assertTrue(experiment.getAspectConfigurations().contains(aspectConfiguration));
		Assert.assertEquals(simulatorConfiguration, aspectConfiguration.getSimulatorConfiguration());
	}
	
	@Test
	public void testDeleteExperiment()
	{
		user = db.findUserByLogin("guest1");
		
		//retrieve project
		GeppettoProject project = db.findEntityById(GeppettoProject.class, 1l);
		int experimentsSize = project.getExperiments().size(); 
		IExperiment experiment = dataManager.newExperiment("E", "D", project);
		
		//retrieve project again 
		project = db.findEntityById(GeppettoProject.class, 1l);
		int newExperimentsSize = project.getExperiments().size(); 
		Assert.assertEquals(experimentsSize+1, newExperimentsSize);
		long id = experiment.getId();
		
		
		dataManager.deleteExperiment(experiment);
		
		project = db.findEntityById(GeppettoProject.class, 1l);
		IExperiment theExperiment = null;
		for(IExperiment e : project.getExperiments())
		{
			if(e.getId() == id)
			{
				// The experiment is found
				theExperiment = e;
			}
		}
		Assert.assertNull(theExperiment);
		Assert.assertEquals(experimentsSize, project.getExperiments().size());
	}
	
	@Test
	public void testCloneExperiment()
	{
		user = db.findUserByLogin("guest1");
		
		//retrieve project
		GeppettoProject project = db.findEntityById(GeppettoProject.class, 1l);
		IExperiment originalExperiment = db.findEntityById(Experiment.class, 1l);
		ISimulatorConfiguration simulatorConfiguration = dataManager.newSimulatorConfiguration("", "", 0l, 0l);
		IAspectConfiguration aspectConfiguration = dataManager.newAspectConfiguration(originalExperiment, "instancePath", simulatorConfiguration);
		int experimentsSize = project.getExperiments().size(); 
		originalExperiment = db.findEntityById(Experiment.class, 1l);
		IExperiment experiment = dataManager.cloneExperiment("E", "D", project, originalExperiment);
		
		//retrieve project again 
		project = db.findEntityById(GeppettoProject.class, 1l);
		int newExperimentsSize = project.getExperiments().size(); 
		Assert.assertEquals(experimentsSize+1, newExperimentsSize);
		long id = experiment.getId();

		float newLength =
				experiment.getAspectConfigurations().get(0).getSimulatorConfiguration().getLength();
		float oldLength =
				originalExperiment.getAspectConfigurations().get(0).getSimulatorConfiguration().getLength();
		Assert.assertEquals(oldLength, newLength, 0);
		
		float newStep =
				experiment.getAspectConfigurations().get(0).getSimulatorConfiguration().getTimestep();
		float oldStep =
				originalExperiment.getAspectConfigurations().get(0).getSimulatorConfiguration().getTimestep();
		Assert.assertEquals(oldStep, newStep, 0);

		String oldWatchedVariable =
				originalExperiment.getAspectConfigurations().get(0).getWatchedVariables().get(0);
		
		String newWatchedVariable =
				experiment.getAspectConfigurations().get(0).getWatchedVariables().get(0);
		Assert.assertEquals(oldWatchedVariable,newWatchedVariable);
		
		String oldWatchedVariable2 =
				originalExperiment.getAspectConfigurations().get(0).getWatchedVariables().get(1);
		
		String newWatchedVariable2 =
				experiment.getAspectConfigurations().get(0).getWatchedVariables().get(1);
		Assert.assertEquals(oldWatchedVariable2,newWatchedVariable2);
		
		int oldWatchedVariableSize =
				originalExperiment.getAspectConfigurations().get(0).getWatchedVariables().size();
		
		int newWatchedVariableSize =
				experiment.getAspectConfigurations().get(0).getWatchedVariables().size();
		Assert.assertEquals(oldWatchedVariableSize,newWatchedVariableSize);
		
		dataManager.deleteExperiment(experiment);
		
		project = db.findEntityById(GeppettoProject.class, 1l);
		IExperiment theExperiment = null;
		for(IExperiment e : project.getExperiments())
		{
			if(e.getId() == id)
			{
				// The experiment is found
				theExperiment = e;
			}
		}
		Assert.assertNull(theExperiment);
		Assert.assertEquals(experimentsSize, project.getExperiments().size());
	}
	
	@Test
	public void testMultipleNewAndDeleteExperiments()
	{
		int runNumber = 5;
		user = db.findUserByLogin("guest1");
		GeppettoProject project = db.findEntityById(GeppettoProject.class, 1l);
		int experimentsSize = project.getExperiments().size(); 
		for(int i =0; i<runNumber; i++){
			IExperiment experiment = dataManager.newExperiment("E", "D", project);
			
			//retrieve project again from db to get updated version
			project = db.findEntityById(GeppettoProject.class, 1l);
			long id = experiment.getId();
			
			dataManager.deleteExperiment(experiment);
			
			//retrieve project again from db to get updated version
			project = db.findEntityById(GeppettoProject.class, 1l);
			IExperiment theExperiment = null;
			for(IExperiment e : project.getExperiments())
			{
				if(e.getId() == id)
				{
					// The experiment is found
					theExperiment = e;
				}
			}
			Assert.assertNull(theExperiment);
		}
		project = db.findEntityById(GeppettoProject.class, 1l);
		Assert.assertEquals(experimentsSize, project.getExperiments().size());
	}

	@Test
	public void testMultipleQueriesPart1()
	{
		List<? extends IUser> users = dataManager.getAllUsers();
	}

	@Test
	public void testMultipleQueriesPart2()
	{
		dataManager.getUserByLogin("guest1");
		dataManager.getGeppettoProjectsForUser("guest1");
		IGeppettoProject project = dataManager.getGeppettoProjectById(3l);
		IExperiment theExperiment = null;
		for(IExperiment e : project.getExperiments())
		{
			if(e.getId() == 8l)
			{
				// The experiment is found
				theExperiment = e;
				break;
			}
		}
		theExperiment.getParentProject().setActiveExperimentId(theExperiment.getId());
		dataManager.saveEntity(theExperiment.getParentProject());

	}

	@Test
	public void testNPEOnGetInstancePath() throws MalformedURLException
	{
		user = db.findUserByLogin("guest1");
		IGeppettoProject project=dataManager.getGeppettoProjectById(1l);
		IExperiment experiment = project.getExperiments().get(1);
		for(int i = 0; i < 10; i++)
		{
			URL result = new URL("http://org.geppetto.bucket.s3.amazonaws.com/projects/1/results.h5");
			String instance = "hhcell.electrical";
			IPersistedData recording = dataManager.newPersistedData(result, PersistedDataType.RECORDING);
			ISimulationResult simulationResults = dataManager.newSimulationResult(instance, recording, ResultsFormat.GEPPETTO_RECORDING);
			experiment.addSimulationResult(simulationResults);
			dataManager.saveEntity(experiment.getParentProject());
		}
	}
}
