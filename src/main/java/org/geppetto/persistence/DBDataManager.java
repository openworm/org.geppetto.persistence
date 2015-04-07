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
import java.util.ArrayList;
import java.util.List;

import org.geppetto.core.data.IGeppettoDataManager;
import org.geppetto.core.data.JsonRequestException;
import org.geppetto.core.data.model.IGeppettoProject;
import org.geppetto.persistence.db.DBManager;
import org.geppetto.persistence.db.model.GeppettoProject;
import org.geppetto.persistence.db.model.InstancePath;
import org.geppetto.persistence.db.model.Parameter;
import org.geppetto.persistence.db.model.User;
import org.springframework.http.HttpStatus;

import com.google.gson.Gson;

public class DBDataManager implements IGeppettoDataManager
{

	private DBManager dbManager;

	public void setDbManager(DBManager manager)
	{
		dbManager = manager;
	}

	public String getName()
	{
		return "DB data manager";
	}

	public boolean isDefault()
	{
		return false;
	}

	public List<GeppettoProject> getAllGeppettoProjects()
	{
		return dbManager.getAllEntities(GeppettoProject.class);
	}

	public User getCurrentUser()
	{
		return getUserByLogin("guest");
	}

	public User getUserByLogin(String login)
	{
		return dbManager.findUserByLogin(login);
	}

	public GeppettoProject getGeppettoProjectById(long id)
	{
		return dbManager.findEntityById(GeppettoProject.class, id);
	}

	public List<GeppettoProject> getGeppettoProjectsForUser(String login)
	{
		List<GeppettoProject> projects = new ArrayList<GeppettoProject>();
		User user = dbManager.findUserByLogin(login);
		if(user != null && user.getGeppettoProjects() != null)
		{
			projects.addAll(user.getGeppettoProjects());
		}
		return projects;
	}

	public void createParameter(String name, String value)
	{
		Parameter parameter = new Parameter(new InstancePath(name, name, name), value);
		dbManager.storeEntity(parameter);
	}

	public Object deleteGeppettoProject(IGeppettoProject project)
	{
		return new JsonRequestException("Not implemented", HttpStatus.BAD_REQUEST);
	}

	public IGeppettoProject getProjectFromJson(Gson gson, String json)
	{
		return gson.fromJson(json, GeppettoProject.class);
	}

	public IGeppettoProject getProjectFromJson(Gson gson, Reader json)
	{
		return gson.fromJson(json, GeppettoProject.class);
	}

}
