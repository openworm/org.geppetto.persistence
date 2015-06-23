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

import org.geppetto.core.data.model.ExperimentStatus;
import org.geppetto.core.data.model.IInstancePath;
import org.geppetto.core.data.model.IPersistedData;
import org.geppetto.core.data.model.ISimulationResult;
import org.geppetto.core.data.model.PersistedDataType;
import org.geppetto.persistence.db.model.Experiment;
import org.geppetto.persistence.db.model.GeppettoProject;
import org.geppetto.persistence.db.model.InstancePath;
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

	public DBManagerTest()
	{
		db.setPersistenceManagerFactory(DBTestData.getPersistenceManagerFactory());
		user = db.findUserByLogin("guest1");
	}

	@Test
	public void testExperimentCreate()
	{
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
		Experiment experiment = db.findEntityById(Experiment.class, 1l);
		Assert.assertEquals(0, experiment.getSimulationResults().size());
		InstancePath aspect = new InstancePath("hhcell","electrical","");
		PersistedData recording = new PersistedData("http://testURL", PersistedDataType.RECORDING);
		ISimulationResult results = new SimulationResult(aspect, recording);
		experiment.addSimulationResult(results);
		Assert.assertEquals(results, experiment.getSimulationResults().get(0));
		Assert.assertEquals(recording, experiment.getSimulationResults().get(0).getResult());
		Assert.assertEquals(aspect, experiment.getSimulationResults().get(0).getAspect());
		db.storeEntity(experiment);
		Assert.assertEquals(results, experiment.getSimulationResults().get(0));
		Assert.assertEquals(recording, experiment.getSimulationResults().get(0).getResult());
		Assert.assertEquals(aspect, experiment.getSimulationResults().get(0).getAspect());
		experiment.getSimulationResults().clear();
		db.deleteEntity(aspect);
		db.deleteEntity(recording);
		db.deleteEntity(results);
		db.storeEntity(experiment);
		Assert.assertEquals(0, experiment.getSimulationResults().size());
	}
	
	@Test
	public void testExperimentUpdate()
	{
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
}
