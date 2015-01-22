/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2011, 2013 OpenWorm.
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

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.persistence.db.model.Simulation;

public class DBManager {

	private PersistenceManagerFactory pmf;

	private static Log _logger = LogFactory.getLog(DBManager.class);

	public DBManager() {
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// ignore
				}
				doSomeDBWork();
			}
		}).start();
	}

	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}

	public void storeSimulation(Simulation simulation) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(simulation);
			tx.commit();
		} catch (Exception e) {
			_logger.warn("Could not store data", e);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

	public void storeSimulations(List<Simulation> simulations) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			for (Simulation simulation : simulations) {
				pm.makePersistent(simulation);
			}
			tx.commit();
		} catch (Exception e) {
			_logger.warn("Could not store data", e);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

	public List<Simulation> findSimulationsByName(String name) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query query = pm.newQuery(Simulation.class);
			query.setFilter("name == searchedName");
			query.declareParameters("String searchedName");
			return (List<Simulation>) query.execute(name);
		} finally {
			pm.close();
		}
	}

	public List<Simulation> getAllSimulations() {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query query = pm.newQuery(Simulation.class);
			return (List<Simulation>) query.execute();
		} finally {
			pm.close();
		}
	}
	
	public void updateSimulation(String name, String newName) {
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			Query query = pm.newQuery(Simulation.class);
			query.setFilter("name == searchedName");
			query.declareParameters("String searchedName");
			List<Simulation> simulations = (List<Simulation>) query.execute(name);
			for (Simulation simulation : simulations) {
				simulation.setName(newName);
			}
		} finally {
			pm.close();
		}
	}
	
	public void deleteAllSimulations() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query query = pm.newQuery(Simulation.class);
			List<Simulation> simulations = (List<Simulation>) query.execute();
			pm.deletePersistentAll(simulations);
			tx.commit();
		} catch (Exception e) {
			_logger.warn("Could not store data", e);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

	private void doSomeDBWork() {
		deleteAllSimulations();

		List<Simulation> simulations = new ArrayList<Simulation>();
		for (int i = 0; i < 2; i++) {
			simulations.add(new Simulation("Name  " + i, new Date(), "url " + i, "status " + i));
		}
		storeSimulations(simulations);
		storeSimulation(new Simulation("Another simulation", new Date(), "http://simulation", ""));

		findSimulationsByName("Name  0");
		updateSimulation("Name  0", "New simulation name");
	}


}
