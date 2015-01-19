package org.geppetto.persistence;

import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.sql.DataSource;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
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
//		return JDOHelper.getPersistenceManagerFactory(name, OSGiLocalPersistenceManagerFactoryBean.class.getClassLoader());
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
//			if ("org.datanucleus.store.rdbms".equals(bundles[x].getSymbolicName())) {
				if ("org.datanucleus.api.jdo".equals(bundles[x].getSymbolicName())) {
				try {
					classloader = bundles[x].loadClass("org.datanucleus.api.jdo.JDOPersistenceManagerFactory").getClassLoader();
//					classloader = bundles[x].loadClass("org.datanucleus.JDOClassLoaderResolver").getClassLoader();
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

// public class OSGiLocalPersistenceManagerFactoryBean extends
// LocalPersistenceManagerFactoryBean implements BundleContextAware {
//
// public static final String JDO_BUNDLE_NAME = "org.datanucleus.api.jdo";
// public static final String JDO_PMF_CLASS_NAME =
// "org.datanucleus.api.jdo.JDOPersistenceManagerFactory";
//
// private BundleContext bundleContext;
//
// @Override
// protected PersistenceManagerFactory newPersistenceManagerFactory(String name)
// {
// return JDOHelper.getPersistenceManagerFactory(name, getClassLoader());
// }
//
// @Override
// protected PersistenceManagerFactory newPersistenceManagerFactory(Map props) {
// ClassLoader classLoader = getClassLoader();
//
// props.put("datanucleus.primaryClassLoader", classLoader);
//
// if (FrameworkUtil.getBundle(this.getClass()) != null) { // running in
// // OSGi
// props.put("datanucleus.plugin.pluginRegistryClassName",
// "org.datanucleus.plugin.OSGiPluginRegistry");
// }
//
// PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(
// props, classLoader);
//
// return pmf;
// }
//
// private ClassLoader getClassLoader() {
// ClassLoader classLoader = null;
// Bundle thisBundle = FrameworkUtil.getBundle(this.getClass());
//
// if (thisBundle != null) { // on OSGi runtime
// Bundle[] bundles = bundleContext.getBundles();
//
// for (Bundle bundle : bundles) {
// if (JDO_BUNDLE_NAME.equals(bundle.getSymbolicName())) {
// try {
// classLoader = bundle.loadClass(JDO_PMF_CLASS_NAME)
// .getClassLoader();
// } catch (ClassNotFoundException e) {
// // do something fancy here ...
// }
// break;
// }
// }
// } else { // somewhere else
// classLoader = this.getClass().getClassLoader();
// }
// return classLoader;
// }
//
// @Override
// public void setBundleContext(BundleContext bundleContext) {
// this.bundleContext = bundleContext;
// }
// }
