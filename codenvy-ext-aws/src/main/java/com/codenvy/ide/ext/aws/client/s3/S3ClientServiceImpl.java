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
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Implementation for the {@link S3ClientService}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class S3ClientServiceImpl implements S3ClientService {
    private static final String BASE_URL       = '/' + Utils.getWorkspaceName() + "/aws/s3";
    private static final String BUCKETS        = BASE_URL + "/buckets";
    private static final String OBJECTS        = BASE_URL + "/objects/";
    private static final String OBJECT_DELETE  = BASE_URL + "/objects/delete/";
    private static final String BUCKETS_DELETE = BASE_URL + "/buckets/delete/";
    private static final String BUCKETS_CREATE = BASE_URL + "/buckets/create";
    private static final String PROJECT_UPLOAD = BASE_URL + "/objects/upload_project/";

    private String restServiceContext;
    private Loader loader;

    /**
     * Create Aws S3 Client service.
     *
     * @param restContext
     * @param loader
     */
    @Inject
    protected S3ClientServiceImpl(@Named("restContext") String restContext, Loader loader) {
        this.loader = loader;
        this.restServiceContext = restContext;
    }

    /** {@inheritDoc} */
    @Override
    public void getBuckets(AwsAsyncRequestCallback<JsonArray<S3Bucket>> callback) throws RequestException {
        String url = restServiceContext + BUCKETS;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getS3ObjectsList(AwsAsyncRequestCallback<S3ObjectsList> callback, String s3Bucket, String nextMarker, int itemNums)
            throws RequestException {
        String url = restServiceContext + OBJECTS + s3Bucket + "?maxkeys=" + String.valueOf(itemNums);
        if (nextMarker != null)
            url += "&nextmarker=" + nextMarker;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteObject(AwsAsyncRequestCallback<String> callback, String s3Bucket, String s3key)
            throws RequestException {
        String url = restServiceContext + OBJECT_DELETE + s3Bucket + "?s3key=" + s3key;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);

    }

    /** {@inheritDoc} */
    @Override
    public void deleteBucket(AwsAsyncRequestCallback<String> callback, String bucketId) throws RequestException {
        String url = restServiceContext + BUCKETS_DELETE + bucketId;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createBucket(AwsAsyncRequestCallback<String> callback, String name, String region) throws RequestException {
        String url = restServiceContext + BUCKETS_CREATE + "?name=" + name + "&region=" + region;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void uploadProject(AwsAsyncRequestCallback<NewS3Object> callback, String s3Bucket, String s3key, String vfsid, String projectid)
            throws RequestException {
        String url = restServiceContext + PROJECT_UPLOAD + s3Bucket + "?s3key=" + s3key + "&vfsid=" + vfsid + "&projectid=" + projectid;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }
}
