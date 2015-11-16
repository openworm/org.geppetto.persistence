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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.geppetto.persistence.db.model.InstancePath;
import org.geppetto.persistence.db.model.Parameter;
import org.geppetto.persistence.db.model.PersistedData;
import org.geppetto.persistence.db.model.SimulationResult;
import org.geppetto.persistence.db.model.SimulatorConfiguration;
import org.geppetto.persistence.db.model.User;
import org.geppetto.persistence.db.model.UserGroup;

public class DBTestData
{

	private DBManager dbManager;
	private User user;

	public DBTestData()
	{
		dbManager = new DBManager();
		dbManager.setPersistenceManagerFactory(getPersistenceManagerFactory());
		long value = 1000l * 1000 * 1000;
		
		List<UserPrivileges> privileges = Arrays.asList(UserPrivileges.READ_PROJECT);
		IUserGroup group = new UserGroup("guest", privileges, value, value * 2);
		user = new User("guest1", "guest", "Guest user", new ArrayList<GeppettoProject>(), group);
		dbManager.storeEntity(user);
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

	private void buildDemoProject(String name)
	{
		String path = "http://org.geppetto.bucket.s3.amazonaws.com/projects/1/";
		user = dbManager.findUserByLogin("guest1");
		List<GeppettoProject> projects = user.getGeppettoProjects();

		PersistedData geppettoModel = new PersistedData(path + "GEPPETTO.xml", PersistedDataType.GEPPETTO_PROJECT);
		GeppettoProject project = new GeppettoProject(name, geppettoModel);

		List<AspectConfiguration> aspectConfigurations1 = new ArrayList<>();
		SimulatorConfiguration sc1 = new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0.00005f, 0.3f, new HashMap<String, String>());
		sc1.getParameters().put("target", "net1");
		List<InstancePath> watchedVariables = new ArrayList<>();
		watchedVariables.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].v"));
		watchedVariables.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].bioPhys1.membraneProperties.naChans.na.m.q"));
		watchedVariables.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].bioPhys1.membraneProperties.naChans.na.h.q"));
		watchedVariables.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].bioPhys1.membraneProperties.kChans.k.n.q"));
		aspectConfigurations1.add(new AspectConfiguration(new InstancePath("hhcell", "electrical", ""), watchedVariables, null, sc1));
		Experiment exp1 = new Experiment(aspectConfigurations1, "Experiment ready to execute", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project);

		List<AspectConfiguration> aspectConfigurations2 = new ArrayList<>();
		List<InstancePath> watchedVariables2 = new ArrayList<>();
		watchedVariables2.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].v"));
		watchedVariables2.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].bioPhys1.membraneProperties.naChans.na.m.q"));
		watchedVariables2.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].bioPhys1.membraneProperties.naChans.na.h.q"));
		watchedVariables2.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].bioPhys1.membraneProperties.kChans.k.n.q"));
		SimulatorConfiguration sc2 = new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0.00005f, 0.3f, new HashMap<String, String>());
		sc2.getParameters().put("target", "net1");
		aspectConfigurations2.add(new AspectConfiguration(new InstancePath("hhcell", "electrical", ""), watchedVariables2, null, sc2));
		List<SimulationResult> simulationResults2 = new ArrayList<>();
		simulationResults2
				.add(new SimulationResult(new InstancePath("hhcell", "electrical", ""), new PersistedData(path + "results.h5", PersistedDataType.RECORDING), ResultsFormat.GEPPETTO_RECORDING));
		Experiment exp2 = new Experiment(aspectConfigurations2, "Executed experiment", "", new Date(), new Date(), ExperimentStatus.COMPLETED, simulationResults2, new Date(), new Date(), project);

		List<AspectConfiguration> aspectConfigurations3 = new ArrayList<>();
		List<InstancePath> watchedVariables3 = new ArrayList<>();
		watchedVariables3.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].v"));
		watchedVariables3.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].bioPhys1.membraneProperties.naChans.na.m.q"));
		watchedVariables3.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].bioPhys1.membraneProperties.naChans.na.h.q"));
		watchedVariables3.add(new InstancePath("hhcell", "electrical.SimulationTree", "hhpop[0].bioPhys1.membraneProperties.kChans.k.n.q"));
		List<Parameter> modelParameters3 = new ArrayList<>();
		modelParameters3.add(new Parameter(new InstancePath("hhcell", "electrical.ModelTree", "pulseGen1.Amplitude"), "0.2"));
		SimulatorConfiguration sc3 = new SimulatorConfiguration("neuronSimulator", "lemsConversion", 0.00005f, 0.3f, new HashMap<String, String>());
		sc3.getParameters().put("target", "net1");
		aspectConfigurations3.add(new AspectConfiguration(new InstancePath("hhcell", "electrical", ""), watchedVariables3, modelParameters3, sc3));
		Experiment exp3 = new Experiment(aspectConfigurations3, "Higher input current", "", new Date(), new Date(), ExperimentStatus.DESIGN, null, new Date(), new Date(), project);

		List<Experiment> experiments = new ArrayList<>();
		experiments.add(exp1);
		experiments.add(exp2);
		experiments.add(exp3);
		project.setExperiments(experiments);
		projects.add(project);

		dbManager.storeEntity(user);

	}

	public static void main(String[] args)
	{
		DBTestData testDBCreator = new DBTestData();
		testDBCreator.buildDemoProject("Hodgkin-Huxley Model 1");
		testDBCreator.buildDemoProject("Hodgkin-Huxley Model 2");
		testDBCreator.buildDemoProject("Hodgkin-Huxley Model 3");
	}

}
