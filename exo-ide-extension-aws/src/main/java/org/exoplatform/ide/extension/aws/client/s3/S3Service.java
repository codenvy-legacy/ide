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
package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.shared.s3.NewS3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectsList;

import java.util.List;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3ServiceImpl.java Sep 19, 2012 vetal $
 */
public class S3Service {
    private static S3Service instance;

    public static S3Service getInstance() {
        if (instance == null) {
            instance = new S3Service(new S3Loader());
        }
        return instance;
    }

    private static final String BASE_URL = Utils.getWorkspaceName() + "/aws/s3";

    private static final String BUCKETS = BASE_URL + "/buckets";

    private static final String OBJECTS = BASE_URL + "/objects/";

    private static final String OBJECT_DELETE = BASE_URL + "/objects/delete/";

    private static final String BUCKETS_DELETE = BASE_URL + "/buckets/delete/";

    private static final String BUCKETS_CREATE = BASE_URL + "/buckets/create";

    private static final String PROJECT_UPLOAD = BASE_URL + "/objects/upload_project/";

    /** REST service context. */
    private String restServiceContext;

    /** Loader to be displayed. */
    private Loader loader;

    private S3Service(Loader loader) {
        this.loader = loader;
        this.restServiceContext = Utils.getRestContext();
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getAvailableSolutionStacks(org.exoplatform
     * .gwtframework.commons.rest.AsyncRequestCallback) */
    public void getBuckets(AwsAsyncRequestCallback<List<S3Bucket>> callback) throws RequestException {
        String url = restServiceContext + BUCKETS;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    public void getS3ObjectsList(AsyncRequestCallback<S3ObjectsList> callback, String s3Bucket, String nextMarker, int itemNums)
            throws RequestException {
        String url = restServiceContext + OBJECTS + s3Bucket + "?maxkeys=" + String.valueOf(itemNums);
        if (nextMarker != null)
            url += "&nextmarker=" + nextMarker;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    public void deleteObject(AsyncRequestCallback<String> callback, String s3Bucket, String s3key)
            throws RequestException {
        String url = restServiceContext + OBJECT_DELETE + s3Bucket + "?s3key=" + s3key;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);

    }

    public void deleteBucket(AsyncRequestCallback<String> callback, String bucketId) throws RequestException {
        String url = restServiceContext + BUCKETS_DELETE + bucketId;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    public void createBucket(AsyncRequestCallback<String> callback, String name, String region) throws RequestException {
        String url = restServiceContext + BUCKETS_CREATE + "?name=" + name + "&region=" + region;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    public void uploadProject(AsyncRequestCallback<NewS3Object> callback, String s3Bucket, String s3key, String vfsid, String projectid)
            throws RequestException {
        String url = restServiceContext + PROJECT_UPLOAD + s3Bucket + "?s3key=" + s3key + "&vfsid=" + vfsid + "&projectid=" + projectid;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

}
