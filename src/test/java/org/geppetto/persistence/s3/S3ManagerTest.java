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

package org.geppetto.persistence.s3;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3ManagerTest
{

	private S3Manager s3 = new S3Manager();

	private static final String PATH = "test";

	public S3ManagerTest()
	{
	}

	@Test
	public void testS3Operations() throws IOException
	{
		List<S3ObjectSummary> paths = s3.retrievePathsFromS3(PATH);
		int count = paths.size();
		s3.saveTextToS3("S3ManagerTest test", PATH + "/" + PATH + new Date().getTime() + ".txt");
		List<S3ObjectSummary> objects = s3.retrievePathsFromS3(PATH);
		Assert.assertEquals(count + 1, objects.size());
		s3.deleteFromS3(objects.get(objects.size() - 1).getKey());
		objects = s3.retrievePathsFromS3(PATH);
		Assert.assertEquals(count, objects.size());
	}

}
