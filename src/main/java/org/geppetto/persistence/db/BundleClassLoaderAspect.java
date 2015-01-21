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

import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.Ordered;

/**
 * <p>
 * Aspect for setting the correct class loader when invoking a method in the service layer.
 * </p>
 * <p>
 * When invoking a method from a bundle in the service layer of another bundle the classloader of the invoking bundle is
 * used. This poses the problem that the invoking class loader needs to know about classes in the service layer of the
 * other bundle. This aspect sets the <tt>ContextClassLoader</tt> of the invoking thread to that of the other bundle,
 * the bundle that owns the method in the service layer which is being invoked. After the invoke is completed the aspect
 * sets the <tt>ContextClassLoader</tt> back to the original classloader of the invoker.
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