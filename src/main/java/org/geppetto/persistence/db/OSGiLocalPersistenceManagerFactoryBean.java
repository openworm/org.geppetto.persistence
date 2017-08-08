

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.geppetto.core.beans.PathConfiguration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.orm.jdo.LocalPersistenceManagerFactoryBean;

public class OSGiLocalPersistenceManagerFactoryBean extends LocalPersistenceManagerFactoryBean implements BundleContextAware
{

	private static Log _logger = LogFactory.getLog(OSGiLocalPersistenceManagerFactoryBean.class);

	private BundleContext bundleContext;

	private Map<String, String> dbConnProperties = new HashMap<String, String>();

	public OSGiLocalPersistenceManagerFactoryBean()
	{
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
