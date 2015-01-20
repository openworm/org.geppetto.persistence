package org.geppetto.persistence;

import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.sql.DataSource;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.orm.jdo.LocalPersistenceManagerFactoryBean;

public class OSGiLocalPersistenceManagerFactoryBean extends LocalPersistenceManagerFactoryBean implements
		BundleContextAware {

	private BundleContext bundleContext;
	private DataSource dataSource;

	public OSGiLocalPersistenceManagerFactoryBean() {
	}

	@Override
	protected PersistenceManagerFactory newPersistenceManagerFactory(String name) {
		return JDOHelper.getPersistenceManagerFactory(name, getClassLoader());
		// return JDOHelper.getPersistenceManagerFactory(name,
		// OSGiLocalPersistenceManagerFactoryBean.class.getClassLoader());
	}

	@Override
	protected PersistenceManagerFactory newPersistenceManagerFactory(Map props) {
		ClassLoader classLoader = getClassLoader();

		props.put("datanucleus.primaryClassLoader", classLoader);

		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props, classLoader);

		return pmf;
	}

	private ClassLoader getClassLoader() {
		ClassLoader classloader = null;
		Bundle[] bundles = bundleContext.getBundles();

		for (int x = 0; x < bundles.length; x++) {
			// if ("org.datanucleus.store.rdbms".equals(bundles[x].getSymbolicName())) {
			if ("org.datanucleus.api.jdo".equals(bundles[x].getSymbolicName())) {
				try {
					classloader = bundles[x].loadClass("org.datanucleus.api.jdo.JDOPersistenceManagerFactory")
							.getClassLoader();
					// classloader = bundles[x].loadClass("org.datanucleus.JDOClassLoaderResolver").getClassLoader();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

		return classloader;
	}

	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
}
