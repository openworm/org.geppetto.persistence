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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.geppetto.persistence.util.PersistenceHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.orm.jdo.LocalPersistenceManagerFactoryBean;

public class OSGiLocalPersistenceManagerFactoryBean extends LocalPersistenceManagerFactoryBean implements BundleContextAware
{

	private static Log _logger = LogFactory.getLog(OSGiLocalPersistenceManagerFactoryBean.class);

	private BundleContext bundleContext;
	private DataSource dataSource;

	private Map<String, String> dbConnProperties = new HashMap<String, String>();

	public OSGiLocalPersistenceManagerFactoryBean()
	{
		File dbConnFile = new File(PersistenceHelper.SETTINGS_DIR + "/db.properties");
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
			_logger.warn("Could not read DB connection file", e);
		}
	}

	@Override
	protected PersistenceManagerFactory newPersistenceManagerFactory(String name)
	{
		return JDOHelper.getPersistenceManagerFactory(name, getClassLoader());
		// return JDOHelper.getPersistenceManagerFactory(name,
		// OSGiLocalPersistenceManagerFactoryBean.class.getClassLoader());
	}

	@Override
	protected PersistenceManagerFactory newPersistenceManagerFactory(Map props)
	{
		ClassLoader classLoader = getClassLoader();
		props.putAll(dbConnProperties);

		props.put("datanucleus.primaryClassLoader", classLoader);

		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props, classLoader);

		return pmf;
	}

	private ClassLoader getClassLoader()
	{
		ClassLoader classloader = null;
		Bundle[] bundles = bundleContext.getBundles();

		for(int x = 0; x < bundles.length; x++)
		{
			// if ("org.datanucleus.store.rdbms".equals(bundles[x].getSymbolicName())) {
			if("org.datanucleus.api.jdo".equals(bundles[x].getSymbolicName()))
			{
				try
				{
					classloader = bundles[x].loadClass("org.datanucleus.api.jdo.JDOPersistenceManagerFactory").getClassLoader();
					// classloader = bundles[x].loadClass("org.datanucleus.JDOClassLoaderResolver").getClassLoader();
				}
				catch(ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

		return classloader;
	}

	@Override
	public void setBundleContext(BundleContext bundleContext)
	{
		this.bundleContext = bundleContext;
	}
}
