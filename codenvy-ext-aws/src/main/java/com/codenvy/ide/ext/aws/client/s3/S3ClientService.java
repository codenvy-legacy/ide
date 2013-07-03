/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.aws.client.s3;

import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.shared.s3.NewS3Object;
import com.codenvy.ide.ext.aws.shared.s3.S3Bucket;
import com.codenvy.ide.ext.aws.shared.s3.S3ObjectsList;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.http.client.RequestException;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3ClientService {
    public void getBuckets(AwsAsyncRequestCallback<JsonArray<S3Bucket>> callback) throws RequestException;

    public void getS3ObjectsList(AwsAsyncRequestCallback<S3ObjectsList> callback, String s3Bucket, String nextMarker, int itemNums)
            throws RequestException;

    public void deleteObject(AwsAsyncRequestCallback<String> callback, String s3Bucket, String s3key)
            throws RequestException;

    public void deleteBucket(AwsAsyncRequestCallback<String> callback, String bucketId) throws RequestException;

    public void createBucket(AwsAsyncRequestCallback<String> callback, String name, String region) throws RequestException;

    public void uploadProject(AwsAsyncRequestCallback<NewS3Object> callback, String s3Bucket, String s3key, String vfsid, String projectid)
            throws RequestException;
}
