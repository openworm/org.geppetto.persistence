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
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.data.model.IDataEntity;
import org.geppetto.core.data.model.IUser;
import org.geppetto.persistence.db.model.GeppettoProject;
import org.geppetto.persistence.db.model.User;

public class DBManager
{

	private static PersistenceManagerFactory pmf;

	private static Log _logger = LogFactory.getLog(DBManager.class);

	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf)
	{
		this.pmf = pmf;
	}

	private static final ThreadLocal<PersistenceManager> PER_THREAD_PM = new ThreadLocal<PersistenceManager>();

	public static PersistenceManager getPersistenceManager()
	{
		PersistenceManager pm = PER_THREAD_PM.get();
		if(pm == null)
		{
			pm = pmf.getPersistenceManager();
			PER_THREAD_PM.set(pm);
		}
		return pm;
	}

	public static void finishRequest()
	{
		PersistenceManager pm = PER_THREAD_PM.get();
		if(pm != null)
		{
			PER_THREAD_PM.remove();
			Transaction tx = pm.currentTransaction();
			if(tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}

	/**
	 * Save or update an entity to the DB.
	 * 
	 * @param entity
	 */
	public synchronized <T> void storeEntity(T entity)
	{
		PersistenceManager pm = getPersistenceManager();
		pm.getFetchPlan().setMaxFetchDepth( -1 );
		Transaction tx = pm.currentTransaction();
		try
		{
		    tx.begin();

		    // We want our object to be detached when it's been persisted
		    pm.setDetachAllOnCommit(true);
			pm.makePersistent(entity);
			
			tx.commit();
		}
		catch(Exception e)
		{
			_logger.warn("Could not insert data", e);
			throw e;
		}
		finally
		{
			finishRequest();
		}

	}
	
	public <T> T detachEntity(T entity)
	{
		T detachedEntity = null;
		PersistenceManager pm = getPersistenceManager();
		try
		{
			detachedEntity = pm.detachCopy(entity);
		}
		catch(Exception e)
		{
			_logger.warn("Could not detach entity", e);
			throw e;
		}
		
		return detachedEntity;
	}

	/**
	 * Delete all entities of a given type.
	 * 
	 * @param type
	 */
	public <T> void deleteAllEntities(Class<T> type)
	{
		PersistenceManager pm = getPersistenceManager();
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
			finishRequest();
		}
	}

	/**
	 * Delete a project identified by id and user.
	 * 
	 * @param id
	 * @param user
	 */
	public void deleteProject(long id, IUser user)
	{
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try
		{
			tx.begin();
			// Had to do this query because otherwise the object is transient and transient instances cannot be deleted
			Query query = pm.newQuery(GeppettoProject.class);
			query.setFilter("id == searchedId");
			query.declareParameters("int searchedId");
			List<GeppettoProject> projects = (List<GeppettoProject>) query.execute(id);
			if(projects.size() > 0)
			{
				GeppettoProject project = projects.get(0);
				query = pm.newQuery(User.class);
				query.setFilter("login == searchedLogin");
				query.declareParameters("String searchedLogin");
				List<User> users = (List<User>) query.execute(user.getLogin());
				if(users.size() > 0)
				{
					user = users.get(0);
					user.getGeppettoProjects().remove(project);
					pm.makePersistent(user);
					pm.deletePersistent(project);
				}
			}
			tx.commit();
		}
		catch(Exception e)
		{
			_logger.warn("Could not delete data", e);
		}
		finally
		{
			finishRequest();
		}
	}

	/**
	 * Delete the provided entity from DB.
	 * 
	 * @param entity
	 */
	public void deleteEntity(IDataEntity entity)
	{
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try
		{
			tx.begin();
			// Had to do this query because otherwise the object is transient and transient instances cannot be deleted
			Query query = pm.newQuery(entity.getClass());
			query.setFilter("id == searchedId");
			query.declareParameters("int searchedId");
			List<IDataEntity> entities = (List<IDataEntity>) query.execute(entity.getId());
			if(entities.size() > 0)
			{
				entity = entities.get(0);
				pm.deletePersistent(entity);
			}
			tx.commit();
		}
		catch(Exception e)
		{
			_logger.warn("Could not delete data",e);
		}
		finally
		{
			finishRequest();
		}
	}

	/**
	 * Retrieve all entities of a given type.
	 * 
	 * @param type
	 * @return
	 */
	public <T> List<T> getAllEntities(Class<T> type)
	{
		PersistenceManager pm = getPersistenceManager();
		try
		{
			pm.getFetchPlan().setMaxFetchDepth(-1);
			Query query = pm.newQuery(type);
			List<T> results = (List<T>) query.execute();
			List<T> entities = new ArrayList<T>();
			for(T t : results)
			{
				entities.add(pm.detachCopy(t));
			}
			return entities;
		}
		finally
		{
			finishRequest();
		}
	}

	/**
	 * Retrieves an entity of a given type and id.
	 * 
	 * @param type
	 * @param id
	 * @return
	 */
	public <T> T findEntityById(Class<T> type, long id)
	{
		PersistenceManager pm = getPersistenceManager();
		try
		{
			pm.getFetchPlan().setMaxFetchDepth(-1);
			Query query = pm.newQuery(type);
			query.setFilter("id == searchedId");
			query.declareParameters("int searchedId");
			List<T> entities = (List<T>) query.execute(id);
			if(entities.size() > 0)
			{
				return pm.detachCopy(entities.get(0));
			}
			return null;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			finishRequest();
		}
	}

	/**
	 * Fetches a user from the database
	 * 
	 * @param login
	 * @return
	 */
	public User findUserByLogin(String login)
	{
		PersistenceManager pm = getPersistenceManager();
		try
		{
			pm.getFetchPlan().setMaxFetchDepth(-1);
			Query query = pm.newQuery(User.class);
			query.setFilter("login == searchedLogin");
			query.declareParameters("String searchedLogin");
			List<User> users = (List<User>) query.execute(login);
			if(users.size() > 0)
			{
				return pm.detachCopy(users.get(0));
			}
			return null;
		}
		finally
		{
			finishRequest();
		}
	}
}
