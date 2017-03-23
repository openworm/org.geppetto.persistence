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

package org.geppetto.persistence.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.geppetto.core.beans.PathConfiguration;
import org.geppetto.core.data.model.ExperimentStatus;
import org.geppetto.core.data.model.IUserGroup;
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

public class DBTestData
{

	private DBManager dbManager;
	private User user, anonymous;
	private User user2, user3, admin;

	public DBTestData() throws ParseException
	{
		SimpleDateFormat formatDate = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
		String date = formatDate.format(new Date());
		Calendar cal = Calendar.getInstance();
		
		dbManager = new DBManager();
		dbManager.setPersistenceManagerFactory(getPersistenceManagerFactory());
		long value = 1000l * 1000 * 1000;
		List<UserPrivileges> privileges = new ArrayList<UserPrivileges>();
		privileges.add(UserPrivileges.READ_PROJECT);
		privileges.add(UserPrivileges.WRITE_PROJECT);
		privileges.add(UserPrivileges.DOWNLOAD);
		privileges.add(UserPrivileges.DROPBOX_INTEGRATION);
		privileges.add(UserPrivileges.RUN_EXPERIMENT);

		date = formatDate.format(new Date());
		IUserGroup group = new UserGroup("guest", privileges, value, value * 2);
		user = new User("guest1", "guest", "Guest user", new ArrayList<GeppettoProject>(), group);
		user.addLoginTimeStamp(formatDate.parse(date));
		dbManager.storeEntity(group);
		dbManager.storeEntity(user);
		
		cal.add(Calendar.DATE, -1);
		date = formatDate.format(new Date());
		List<UserPrivileges> guest2Privileges = new ArrayList<UserPrivileges>();
		guest2Privileges.add(UserPrivileges.READ_PROJECT);
		IUserGroup groupUser2 = new UserGroup("guest2", guest2Privileges, value, value * 2);
		user2 = new User("guest2", "guest", "Guest user 2", new ArrayList<GeppettoProject>(), groupUser2);
		user2.addLoginTimeStamp(cal.getTime());
		dbManager.storeEntity(groupUser2);
		dbManager.storeEntity(user2);

		cal.add(Calendar.DATE, -6);
		date = formatDate.format(new Date());
		List<UserPrivileges> guest3Privileges = new ArrayList<UserPrivileges>();
		guest3Privileges.add(UserPrivileges.READ_PROJECT);
		guest3Privileges.add(UserPrivileges.WRITE_PROJECT);
		IUserGroup groupUser3 = new UserGroup("guest3", guest3Privileges, value, value * 2);
		user3 = new User("guest3", "guest", "Guest user 3", new ArrayList<GeppettoProject>(), groupUser3);
		user2.addLoginTimeStamp(cal.getTime());
		dbManager.storeEntity(groupUser3);
		dbManager.storeEntity(user3);
		
		cal.add(Calendar.DATE, -27);
		date = formatDate.format(new Date());
		List<UserPrivileges> privilegesAnonymous = new ArrayList<UserPrivileges>();
		privilegesAnonymous.add(UserPrivileges.READ_PROJECT);
		IUserGroup anonymousGroup = new UserGroup("anonymous", privilegesAnonymous, value, value * 2);
		anonymous = new User("anonymous", "guest", "Anonymous", new ArrayList<GeppettoProject>(), anonymousGroup);
		user2.addLoginTimeStamp(cal.getTime());
		dbManager.storeEntity(anonymousGroup);
		dbManager.storeEntity(anonymous);
		
		date = formatDate.format(new Date());
		List<UserPrivileges> adminPrivileges = new ArrayList<UserPrivileges>();
		adminPrivileges.add(UserPrivileges.READ_PROJECT);
		adminPrivileges.add(UserPrivileges.WRITE_PROJECT);
		adminPrivileges.add(UserPrivileges.RUN_EXPERIMENT);
		adminPrivileges.add(UserPrivileges.DOWNLOAD);
		adminPrivileges.add(UserPrivileges.ADMIN);
		IUserGroup adminGroup = new UserGroup("admin", adminPrivileges, value, value * 2);
		admin = new User("admin", "admin", "Admin User", new ArrayList<GeppettoProject>(), adminGroup);
		user2.addLoginTimeStamp(cal.getTime());
		dbManager.storeEntity(adminGroup);
		dbManager.storeEntity(admin);
	}

	public static PersistenceManagerFactory getPersistenceManagerFactory()
	{
		Map<String, String> dbConnProperties = new HashMap<>();
		dbConnProperties.put("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
		dbConnProperties.put("datanucleus.storeManagerType", "rdbms");
		dbConnProperties.put("datanucleus.connection.resourceType", "RESOURCE_LOCAL");
		dbConnProperties.put("datanucleus.DetachAllOnCommit", "true");
		dbConnProperties.put("datanucleus.validateTables", "true");
		dbConnProperties.put("datanucleus.connection2.resourceType", "RESOURCE_LOCAL");
		dbConnProperties.put("datanucleus.autoCreateSchema", "true");
		dbConnProperties.put("datanucleus.autoCreateColumns", "true");
		File dbConnFile = new File(PathConfiguration.settingsFolder + "/db.properties");
		try
		{
			List<String> lines = Files.readAllLines(dbConnFile.toPath(), Charset.defaultCharset());
			for(String line : lines)
			{
				int eqIndex = line.indexOf("=");
				if(!line.startsWith("#") && eqIndex > 0)
				{
					dbConnProperties.put(line.substring(0, eqIndex), line.substring(eqIndex + 1));
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		return JDOHelper.getPersistenceManagerFactory(dbConnProperties);
	}

	private void buildACNet2DemoProject(String name, int id)
	{
		String path = "http://org.geppetto.bucket.s3.amazonaws.com/projects/"+id+"/";
		user = dbManager.findUserByLogin("guest1");
		List<GeppettoProject> projects = user.getGeppettoProjects();

		PersistedData geppettoModel = new PersistedData(path + "GeppettoModel.xmi", PersistedDataType.GEPPETTO_PROJECT);
		GeppettoProject project = new GeppettoProject(name, geppettoModel);

		List<AspectConfiguration> aspectConfigurations1 = new ArrayList<>();
		SimulatorConfiguration sc1 = new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0.00005f, 0.3f, new HashMap<String, String>());
		sc1.getParameters().put("target", "network_ACnet2");
		List<String> watchedVariables = new ArrayList<>();
		watchedVariables.add("acnet2.pyramidals_48[0].soma_0.v");
		watchedVariables.add("acnet2.pyramidals_48[0].apical0_1.v");
		watchedVariables.add("acnet2.pyramidals_48[0].apical2_2.v");
		watchedVariables.add("acnet2.pyramidals_48[0].apical3_3.v");
		watchedVariables.add("acnet2.pyramidals_48[0].apical4_4.v");
		watchedVariables.add("acnet2.pyramidals_48[0].apical1_5.v");
		watchedVariables.add("acnet2.pyramidals_48[0].basal0_6.v");
		watchedVariables.add("acnet2.pyramidals_48[0].basal1_7.v");
		watchedVariables.add("acnet2.pyramidals_48[0].basal2_8.v");
		watchedVariables.add("acnet2.pyramidals_48[1].soma_0.v");
		watchedVariables.add("acnet2.baskets_12[2].soma_0.v");
		aspectConfigurations1.add(new AspectConfiguration("acnet2", watchedVariables, null, sc1));
		
		
		Experiment exp1 = new Experiment(aspectConfigurations1, "Experiment ready to execute", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));

		List<AspectConfiguration> aspectConfigurations2 = new ArrayList<>();
		List<String> watchedVariables2 = new ArrayList<>();

		SimulatorConfiguration sc2 = new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0.00005f, 0.3f, new HashMap<String, String>());
		sc2.getParameters().put("target", "network_ACnet2");
		aspectConfigurations2.add(new AspectConfiguration("acnet2", watchedVariables2, null, sc2));
		Experiment exp2 = new Experiment(aspectConfigurations2, "Experiment to configure", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));
		
		List<AspectConfiguration> aspectConfigurations3 = new ArrayList<>();
		List<String> watchedVariables3 = new ArrayList<>();
		watchedVariables3.add("acnet2.pyramidals_48[0].soma_0.v");
		watchedVariables3.add("acnet2.pyramidals_48[1].soma_0.v");
		watchedVariables3.add("acnet2.baskets_12[2].soma_0.v");
		SimulatorConfiguration sc3 = new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0.00005f, 0.3f, new HashMap<String, String>());
		sc3.getParameters().put("target", "network_ACnet2");
		aspectConfigurations3.add(new AspectConfiguration("acnet2", watchedVariables3, null, sc3));
		List<SimulationResult> simulationResults3 = new ArrayList<>();
		simulationResults3
				.add(new SimulationResult("acnet2", new PersistedData(path + "results.h5", PersistedDataType.RECORDING), ResultsFormat.GEPPETTO_RECORDING));
		simulationResults3
			.add(new SimulationResult("acnet2", new PersistedData(path + "rawRecording.zip", PersistedDataType.RECORDING), ResultsFormat.RAW));
		Experiment exp3= new Experiment(aspectConfigurations3, "Experiment executed", "", new Date(), new Date(), ExperimentStatus.COMPLETED, simulationResults3, new Date(), new Date(), project, new View("{}"));

		List<Experiment> experiments = new ArrayList<>();
		experiments.add(exp1);
		experiments.add(exp2);
		experiments.add(exp3);
		project.setExperiments(experiments);
		projects.add(project);

		dbManager.storeEntity(user);

	}
	
	private void buildHHCellDemoProject(String name, int id)
	{
		String path = "http://org.geppetto.bucket.s3.amazonaws.com/projects/"+id+"/";
		user = dbManager.findUserByLogin("guest1");
		user2 = dbManager.findUserByLogin("guest2");
		user3 = dbManager.findUserByLogin("guest3");
		List<GeppettoProject> projects = user.getGeppettoProjects();
		List<GeppettoProject> projectsUser2 = user2.getGeppettoProjects();
		List<GeppettoProject> projectsUser3 = user3.getGeppettoProjects();

		PersistedData geppettoModel = new PersistedData(path + "GeppettoModel.xmi", PersistedDataType.GEPPETTO_PROJECT);
		GeppettoProject project = new GeppettoProject(name, geppettoModel);

		List<AspectConfiguration> aspectConfigurations1 = new ArrayList<>();
		SimulatorConfiguration sc1 = new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0.00005f, 0.3f, new HashMap<String, String>());
		sc1.getParameters().put("target", "net1");
		List<String> watchedVariables = new ArrayList<>();
		watchedVariables.add("hhcell.hhpop[0].v");
		watchedVariables.add("hhcell.hhpop[0].bioPhys1.membraneProperties.naChans.na.m.q");
		watchedVariables.add("hhcell.hhpop[0].bioPhys1.membraneProperties.naChans.na.h.q");
		watchedVariables.add("hhcell.hhpop[0].bioPhys1.membraneProperties.kChans.k.n.q");
		aspectConfigurations1.add(new AspectConfiguration("hhcell", watchedVariables, null, sc1));
		Experiment exp1 = new Experiment(aspectConfigurations1, "Experiment ready to execute", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));

		List<AspectConfiguration> aspectConfigurations2 = new ArrayList<>();
		List<String> watchedVariables2 = new ArrayList<>();
		watchedVariables2.add("hhcell.hhpop[0].v");
		watchedVariables2.add("hhcell.hhpop[0].bioPhys1.membraneProperties.naChans.na.m.q");
		watchedVariables2.add("hhcell.hhpop[0].bioPhys1.membraneProperties.naChans.na.h.q");
		watchedVariables2.add("hhcell.hhpop[0].bioPhys1.membraneProperties.kChans.k.n.q");
		SimulatorConfiguration sc2 = new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0.00005f, 0.3f, new HashMap<String, String>());
		sc2.getParameters().put("target", "net1");
		aspectConfigurations2.add(new AspectConfiguration("hhcell", watchedVariables2, null, sc2));
		List<SimulationResult> simulationResults2 = new ArrayList<>();
		simulationResults2
				.add(new SimulationResult("hhcell", new PersistedData(path + "results.h5", PersistedDataType.RECORDING), ResultsFormat.GEPPETTO_RECORDING));
		simulationResults2
			.add(new SimulationResult("hhcell", new PersistedData(path + "rawRecording.zip", PersistedDataType.RECORDING), ResultsFormat.RAW));
		Experiment exp2 = new Experiment(aspectConfigurations2, "Executed experiment", "", new Date(), new Date(), ExperimentStatus.COMPLETED, simulationResults2, new Date(), new Date(), project, new View("{}"));

		List<AspectConfiguration> aspectConfigurations3 = new ArrayList<>();
		List<String> watchedVariables3 = new ArrayList<>();
		watchedVariables3.add("hhcell.hhpop[0].v");
		watchedVariables3.add("hhcell.hhpop[0].bioPhys1.membraneProperties.naChans.na.m.q");
		watchedVariables3.add("hhcell.hhpop[0].bioPhys1.membraneProperties.naChans.na.h.q");
		watchedVariables3.add("hhcell.hhpop[0].bioPhys1.membraneProperties.kChans.k.n.q");
		List<Parameter> modelParameters3 = new ArrayList<>();
		modelParameters3.add(new Parameter("hhcell.explicitInput.pulseGen1.amplitude", "0.2"));
		SimulatorConfiguration sc3 = new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0.00005f, 0.3f, new HashMap<String, String>());
		sc3.getParameters().put("target", "net1");
		aspectConfigurations3.add(new AspectConfiguration("hhcell", watchedVariables3, modelParameters3, sc3));
		Experiment exp3 = new Experiment(aspectConfigurations3, "Higher input current", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));

		List<Experiment> experiments = new ArrayList<>();
		experiments.add(exp1);
		experiments.add(exp2);
		experiments.add(exp3);
		project.setExperiments(experiments);
		projects.add(project);
		projectsUser2.add(project);
		projectsUser3.add(project);
		dbManager.storeEntity(user);
		dbManager.storeEntity(user2);
		dbManager.storeEntity(user3);
	}
	
	private void buildHHCellOpenCortex246CellsDemoProject(String name, int id)
	{
		String path = "https://raw.githubusercontent.com/openworm/org.geppetto.persistence/usability_actions/src/main/resources/"+name+"/";
		user = dbManager.findUserByLogin("guest1");
		List<GeppettoProject> projects = user.getGeppettoProjects();

		PersistedData geppettoModel = new PersistedData(path + "GeppettoModel.xmi", PersistedDataType.GEPPETTO_PROJECT);
		GeppettoProject project = new GeppettoProject(name, geppettoModel);

		List<AspectConfiguration> aspectConfigurations1 = new ArrayList<>();
		SimulatorConfiguration sc1 = new SimulatorConfiguration("neuronSimulator", null, 0.00005f, 0.3f, new HashMap<String, String>());
		sc1.getParameters().put("target", "Balanced_246cells_26593conns");
		aspectConfigurations1.add(new AspectConfiguration("Balanced_246cells_26593conns", null, null, sc1));
		Experiment exp1 = new Experiment(aspectConfigurations1, "Balanced_246cells_26593conns - net", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));
		exp1.setScript(path+"script.js");
		List<Experiment> experiments = new ArrayList<>();
		experiments.add(exp1);
		project.setExperiments(experiments);
		
		projects.add(project);
		dbManager.storeEntity(user);
	}
	
	private void buildHHCellOpenCortex240CellsDemoProject(String name, int id)
	{
		String path = "https://raw.githubusercontent.com/openworm/org.geppetto.persistence/usability_actions/src/main/resources/"+name+"/";
		user = dbManager.findUserByLogin("guest1");
		List<GeppettoProject> projects = user.getGeppettoProjects();

		PersistedData geppettoModel = new PersistedData(path + "GeppettoModel.xmi", PersistedDataType.GEPPETTO_PROJECT);
		GeppettoProject project = new GeppettoProject(name, geppettoModel);

		List<AspectConfiguration> aspectConfigurations1 = new ArrayList<>();
		SimulatorConfiguration sc1 = new SimulatorConfiguration("neuronSimulator", null, 0.00005f, 0.3f, new HashMap<String, String>());
		sc1.getParameters().put("target", "Balanced_240cells_29299conns");
		aspectConfigurations1.add(new AspectConfiguration("Balanced_240cells_29299conns", null, null, sc1));
		Experiment exp1 = new Experiment(aspectConfigurations1, "Balanced_240cells_29299conns - net", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));
		exp1.setScript(path+"script.js");
		List<Experiment> experiments = new ArrayList<>();
		experiments.add(exp1);
		project.setExperiments(experiments);
		
		projects.add(project);
		dbManager.storeEntity(user);
	}
	
	private void twoCell(String name, int id)
	{
		String path = "https://raw.githubusercontent.com/openworm/org.geppetto.persistence/usability_actions/src/main/resources/"+name+"/";
		user = dbManager.findUserByLogin("guest1");
		List<GeppettoProject> projects = user.getGeppettoProjects();

		PersistedData geppettoModel = new PersistedData(path + "GeppettoModel.xmi", PersistedDataType.GEPPETTO_PROJECT);
		GeppettoProject project = new GeppettoProject(name, geppettoModel);

		List<AspectConfiguration> aspectConfigurations1 = new ArrayList<>();
		SimulatorConfiguration sc1 = new SimulatorConfiguration("neuronSimulator", null, 0.00005f, 0.3f, new HashMap<String, String>());
		sc1.getParameters().put("target", "TwoCell");
		aspectConfigurations1.add(new AspectConfiguration("TwoCell", null, null, sc1));
		Experiment exp1 = new Experiment(aspectConfigurations1, "TwoCell", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));
		List<Experiment> experiments = new ArrayList<>();
		experiments.add(exp1);
		project.setExperiments(experiments);
		
		projects.add(project);
		dbManager.storeEntity(user);
	}
	
	private void c302model(String name, int id)
	{
		String path = "https://raw.githubusercontent.com/openworm/org.geppetto.persistence/admin_view/src/main/resources/"+name+"/";
		admin = dbManager.findUserByLogin("admin");
		List<GeppettoProject> projects = admin.getGeppettoProjects();

		PersistedData geppettoModel = new PersistedData(path + "GeppettoModel.xmi", PersistedDataType.GEPPETTO_PROJECT);
		GeppettoProject project = new GeppettoProject(name, geppettoModel);

		HashMap<String, String> map = new HashMap<String, String>();
		List<AspectConfiguration> aspectConfigurations1 = new ArrayList<>();
		SimulatorConfiguration sc1 = new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0.0001f, 0, map);
		sc1.getParameters().put("target", "network_CElegans");
		aspectConfigurations1.add(new AspectConfiguration("c302", null, null, sc1));
		Experiment exp1 = new Experiment(aspectConfigurations1, "C302", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));
		List<Experiment> experiments = new ArrayList<>();
		experiments.add(exp1);
		project.setExperiments(experiments);
		
		projects.add(project);
		dbManager.storeEntity(admin);
	}
	
	private void demoProjects(String name, int id)
	{
		String path = "https://raw.githubusercontent.com/openworm/org.geppetto.persistence/usability_actions/src/main/resources/"+name+"/";
		user3 = dbManager.findUserByLogin("guest3");
		List<GeppettoProject> projects = user3.getGeppettoProjects();

		PersistedData geppettoModel = new PersistedData(path + "GeppettoModel.xmi", PersistedDataType.GEPPETTO_PROJECT);
		GeppettoProject project = new GeppettoProject(name, geppettoModel);

		List<AspectConfiguration> aspectConfigurations1 = new ArrayList<>();
		SimulatorConfiguration sc1 = new SimulatorConfiguration("neuronSimulator", null, 0.00005f, 0.3f, new HashMap<String, String>());
		sc1.getParameters().put("target", "TwoCell");
		aspectConfigurations1.add(new AspectConfiguration("TwoCell", null, null, sc1));
		Experiment exp1 = new Experiment(aspectConfigurations1, "TwoCell", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));
		List<Experiment> experiments = new ArrayList<>();
		exp1.setStatus(ExperimentStatus.ERROR);
		exp1.updateEndDate();
		exp1.setDetails("Experiment Failed during run attempt");
		experiments.add(exp1);
		project.setExperiments(experiments);
		
		projects.add(project);
		dbManager.storeEntity(user3);
		
		Experiment exp2 = new Experiment(aspectConfigurations1, "TwoCell", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project, new View("{}"));
		exp2.updateEndDate();
		project.getExperiments().add(exp2);
		
		user2 = dbManager.findUserByLogin("guest2");
		List<GeppettoProject> projects2 = user2.getGeppettoProjects();
		projects2.add(project);
		dbManager.storeEntity(user2);
		
		admin = dbManager.findUserByLogin("admin");
		admin.getGeppettoProjects().add(project);
		dbManager.storeEntity(admin);
		
		anonymous = dbManager.findUserByLogin("anonymous");
		admin.getGeppettoProjects().add(project);
		dbManager.storeEntity(anonymous);
	}

	public static void main(String[] args)
	{
		DBTestData testDBCreator;
		try {
			testDBCreator = new DBTestData();
			testDBCreator.buildHHCellDemoProject("Hodgkin-Huxley Model 1",1);
			testDBCreator.buildHHCellDemoProject("Hodgkin-Huxley Model 2",1);
			testDBCreator.buildHHCellDemoProject("Hodgkin-Huxley Model 3",1);
			testDBCreator.buildACNet2DemoProject("ACNet2 1",5);
			testDBCreator.buildACNet2DemoProject("ACNet2 2",5);
			testDBCreator.buildACNet2DemoProject("ACNet2 3",5);
			testDBCreator.buildHHCellOpenCortex246CellsDemoProject("Balanced_246cells_26593conns.net", 10);
			testDBCreator.buildHHCellOpenCortex240CellsDemoProject("Balanced_240cells_29299conns.net", 15);
			testDBCreator.twoCell("twocell", 20);
			testDBCreator.demoProjects("twocell", 25);
			testDBCreator.c302model("cElegansConnectome", 30);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

}
