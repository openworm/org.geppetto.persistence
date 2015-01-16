package org.geppetto.persistence;

import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.Ordered;

/**
 * <p>
 * Aspect for setting the correct class loader when invoking a method in the
 * service layer.
 * </p>
 * <p>
 * When invoking a method from a bundle in the service layer of another bundle
 * the classloader of the invoking bundle is used. This poses the problem that
 * the invoking class loader needs to know about classes in the service layer of
 * the other bundle. This aspect sets the <tt>ContextClassLoader</tt> of the
 * invoking thread to that of the other bundle, the bundle that owns the method
 * in the service layer which is being invoked. After the invoke is completed
 * the aspect sets the <tt>ContextClassLoader</tt> back to the original
 * classloader of the invoker.
 * </p>
 * 
 */
public class BundleClassLoaderAspect implements Ordered {

	private static final int ASPECT_PRECEDENCE = 0;

	public Object setClassLoader(MethodInvocationProceedingJoinPoint pjp) throws Throwable {
		// Save a reference to the classloader of the caller
		ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		// Get a reference to the classloader of the owning bundle
		ClassLoader serviceLoader = pjp.getTarget().getClass().getClassLoader();
		// Set the class loader of the current thread to the class loader of the
		// owner of the bundle
		Thread.currentThread().setContextClassLoader(serviceLoader);

		Object returnValue = null;

		try {
			// Make the actual call to the method.
			returnValue = pjp.proceed();
		} finally {
			// Reset the classloader of this Thread to the original
			// classloader of the method invoker.
			Thread.currentThread().setContextClassLoader(oldLoader);
		}

		return returnValue;
	}

	@Override
	public int getOrder() {
		return ASPECT_PRECEDENCE;
	}
}