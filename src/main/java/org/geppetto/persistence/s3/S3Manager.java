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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.data.IGeppettoS3Manager;
import org.geppetto.persistence.util.PersistenceHelper;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Manager implements IGeppettoS3Manager
{

	private AmazonS3 _s3Connection;

	private static Log _logger = LogFactory.getLog(S3Manager.class);

	public S3Manager()
	{
		// TODO: this will be removed once we have real S3 usage
		// new Thread(new Runnable()
		// {
		// public void run()
		// {
		// try
		// {
		// Thread.sleep(5000);
		// doSomeRealModelS3Work();
		// }
		// catch(InterruptedException e)
		// {
		// // ignore
		// }
		// }
		// }).start();
	}

	private AmazonS3 getS3Connection()
	{
		if(_s3Connection == null)
		{
			File credentialsFile = new File(PersistenceHelper.SETTINGS_DIR + "/aws.credentials");
			try
			{
				_s3Connection = new AmazonS3Client(new PropertiesCredentials(credentialsFile));
			}
			catch(Exception e)
			{
				_logger.warn("Could not initialize S3 connection", e);
			}
		}
		return _s3Connection;
	}

	public void saveFileToS3(File file, String path)
	{
		AmazonS3 s3 = getS3Connection();
		s3.putObject(PersistenceHelper.BUCKET_NAME, path, file);
	}

	public void saveTextToS3(String text, String path) throws IOException
	{
		File file = File.createTempFile("file", "");
		Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
		saveFileToS3(file, path);
	}

	public List<S3ObjectSummary> retrievePathsFromS3(String prefix)
	{
		ObjectListing listing = getS3Connection().listObjects(PersistenceHelper.BUCKET_NAME, prefix);
		List<S3ObjectSummary> allSummaries = new ArrayList<>();
		List<S3ObjectSummary> summaries = listing.getObjectSummaries();
		while(!summaries.isEmpty())
		{
			allSummaries.addAll(summaries);
			summaries.clear();
			if(listing.isTruncated())
			{
				summaries = listing.getObjectSummaries();
			}
		}
		return allSummaries;
	}

	public void deleteFromS3(String path)
	{
		getS3Connection().deleteObject(PersistenceHelper.BUCKET_NAME, path);
	}

	private void doSomeRealModelS3Work()
	{
		try
		{
			saveTextToS3("some text to test the S3 stuff", "test/testfile" + System.currentTimeMillis() + ".txt");
		}
		catch(IOException e)
		{
			_logger.warn("Could not save to S3", e);
		}
		List<S3ObjectSummary> persistedSummaries = retrievePathsFromS3("test");
		if(persistedSummaries.size() > 0)
		{
			deleteFromS3(persistedSummaries.get(0).getKey());
		}

	}

}
