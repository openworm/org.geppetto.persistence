

package org.geppetto.persistence.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geppetto.core.data.model.ExperimentStatus;
import org.geppetto.core.data.model.IAspectConfiguration;
import org.geppetto.core.data.model.IExperiment;
import org.geppetto.core.data.model.IGeppettoProject;
import org.geppetto.core.data.model.IParameter;
import org.geppetto.core.data.model.IPersistedData;
import org.geppetto.core.data.model.ISimulationResult;
import org.geppetto.core.data.model.ISimulatorConfiguration;
import org.geppetto.core.data.model.IUser;
import org.geppetto.core.data.model.PersistedDataType;
import org.geppetto.core.data.model.ResultsFormat;
import org.geppetto.core.data.model.UserPrivileges;
import org.geppetto.core.s3.S3Manager;
import org.geppetto.persistence.GeppettoDataManager;
import org.geppetto.persistence.db.model.AspectConfiguration;
import org.geppetto.persistence.db.model.Experiment;
import org.geppetto.persistence.db.model.GeppettoProject;
import org.geppetto.persistence.db.model.PersistedData;
import org.geppetto.persistence.db.model.SimulationResult;
import org.geppetto.persistence.db.model.User;
import org.geppetto.persistence.db.model.View;
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
		
		user = db.findUserByLogin("guest2");
		
		Assert.assertTrue(user != null);
		Assert.assertTrue(user.getUserGroup() != null);
		Assert.assertTrue(user.getUserGroup().getPrivileges().size() == 1);
		Assert.assertTrue(user.getUserGroup().getPrivileges().get(0) == UserPrivileges.READ_PROJECT);
		
		user = db.findUserByLogin("anonymous");
		
		Assert.assertTrue(user != null);
		Assert.assertTrue(user.getUserGroup() != null);
		Assert.assertTrue(user.getUserGroup().getPrivileges().size() == 1);
		Assert.assertTrue(user.getUserGroup().getPrivileges().get(0) == UserPrivileges.READ_PROJECT);
	}
	
	@Test
	public void testExperimentCreate()
	{
		user = db.findUserByLogin("guest1");
		GeppettoProject project = user.getGeppettoProjects().get(0);
		int count = project.getExperiments().size();
		int allCount = db.getAllEntities(Experiment.class).size();
		Experiment experiment = new Experiment(null, "test exp", "test exp", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));
		project.getExperiments().add(experiment);
		db.storeEntity(project);

		project = db.findEntityById(GeppettoProject.class, project.getId());
		Assert.assertEquals(count + 1, project.getExperiments().size());
		Assert.assertEquals(allCount + 1, db.getAllEntities(Experiment.class).size());
		Experiment experiment2 = new Experiment(null, "test exp", "test exp", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));
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
		ISimulatorConfiguration simulatorConfiguration = dataManager.newSimulatorConfiguration("", "", 0l, 0l,new HashMap<String,String>());
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
		ISimulatorConfiguration simulatorConfiguration = dataManager.newSimulatorConfiguration("", "", 0l, 0l,new HashMap<String,String>());
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
		
		int oldSimParamsSize =
				originalExperiment.getAspectConfigurations().get(0).getSimulatorConfiguration().getParameters().size();
		
		int newSimParamsSize =
				experiment.getAspectConfigurations().get(0).getSimulatorConfiguration().getParameters().size();
		Assert.assertEquals(oldSimParamsSize,newSimParamsSize);
		
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
	public void testSetParameters()
	{
		user = db.findUserByLogin("guest1");
		
		//retrieve project
		GeppettoProject project = db.findEntityById(GeppettoProject.class, 1l);
		IExperiment originalExperiment = db.findEntityById(Experiment.class, 1l);
		ISimulatorConfiguration simulatorConfiguration = dataManager.newSimulatorConfiguration("", "", 0l, 0l,new HashMap<String,String>());
		
		int experimentsSize = project.getExperiments().size(); 
		originalExperiment = db.findEntityById(Experiment.class, 1l);
		IExperiment experiment = dataManager.cloneExperiment("E", "D", project, originalExperiment);
		IParameter parameter = dataManager.newParameter("neuroml.pulseGen1.amplitude", "0.1");
		IAspectConfiguration aspectConfiguration = dataManager.newAspectConfiguration(experiment, "instancePath", simulatorConfiguration);
		aspectConfiguration.addModelParameter(parameter);
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
		
		List<? extends IParameter> newParameterValue =
				experiment.getAspectConfigurations().get(1).getModelParameter();
		Assert.assertNotNull(newParameterValue);
		if(newParameterValue!=null){
			Assert.assertEquals("neuroml.pulseGen1.amplitude",newParameterValue.get(0).getVariable());
			Assert.assertEquals("0.1",newParameterValue.get(0).getValue());
		}
		
		
		int oldSimParamsSize =
				originalExperiment.getAspectConfigurations().get(0).getSimulatorConfiguration().getParameters().size();
		
		int newSimParamsSize =
				experiment.getAspectConfigurations().get(0).getSimulatorConfiguration().getParameters().size();
		Assert.assertEquals(oldSimParamsSize,newSimParamsSize);
		
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
	
	@Test
	public void testStorage()
	{
		user = db.findUserByLogin("guest2");
		List<GeppettoProject> projects = user.getGeppettoProjects();
		long size = 0;
		for(GeppettoProject p: projects){
			size += S3Manager.getInstance().getFileStorage("projects/"+p.getId()+"/");
			System.out.println(size);
		}
		Assert.assertNotNull(size);
	}
}
