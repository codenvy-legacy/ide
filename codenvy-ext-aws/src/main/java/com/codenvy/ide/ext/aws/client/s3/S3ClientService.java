/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.aws.client.s3;

import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.shared.s3.NewS3Object;
import com.codenvy.ide.ext.aws.shared.s3.S3Bucket;
import com.codenvy.ide.ext.aws.shared.s3.S3ObjectsList;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for operating with S3 Objects.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3ClientService {
    /**
     * Get list of S3 Buckets.
     *
     * @param callback
     *         callback with Array of S3 Buckets.
     * @throws RequestException
     */
    public void getBuckets(AwsAsyncRequestCallback<JsonArray<S3Bucket>> callback) throws RequestException;

    /**
     * Get list of S3 Objects in specified bucket.
     *
     * @param callback
     *         callback with S3 Objects.
     * @param s3Bucket
     *         Amazon S3 Bucket.
     * @param nextMarker
     *         marker from which items must be picked.
     * @param itemNums
     *         count of objects to be piked.
     * @throws RequestException
     */
    public void getS3ObjectsList(AwsAsyncRequestCallback<S3ObjectsList> callback, String s3Bucket, String nextMarker, int itemNums)
            throws RequestException;

    /**
     * Delete S3 Object from specified bucket.
     *
     * @param callback
     *         callback.
     * @param s3Bucket
     *         Amazon S3 Bucket.
     * @param s3key
     *         Amazon S3 Key.
     * @throws RequestException
     */
    public void deleteObject(AwsAsyncRequestCallback<String> callback, String s3Bucket, String s3key)
            throws RequestException;

    /**
     * Delete S3 Bucket.
     *
     * @param callback
     *         callback.
     * @param bucketId
     *         Amazon S3 Bucket.
     * @throws RequestException
     */
    public void deleteBucket(AwsAsyncRequestCallback<String> callback, String bucketId) throws RequestException;

    /**
     * Create S3 Bucket with specified name and region.
     *
     * @param callback
     *         callback.
     * @param name
     *         name for the new bucket.
     * @param region
     *         region name for the new bucket.
     * @throws RequestException
     */
    public void createBucket(AwsAsyncRequestCallback<String> callback, String name, String region) throws RequestException;

    /**
     * Upload project to selected S3 Bucket.
     *
     * @param callback
     *         callback.
     * @param s3Bucket
     *         name for the S3 Bucket into which project will be uploaded.
     * @param s3key
     *         name of the S3 Key.
     * @param vfsid
     *         VFS is.
     * @param projectid
     *         Id for opened project.
     * @throws RequestException
     */
    public void uploadProject(AwsAsyncRequestCallback<NewS3Object> callback, String s3Bucket, String s3key, String vfsid, String projectid)
            throws RequestException;
}
