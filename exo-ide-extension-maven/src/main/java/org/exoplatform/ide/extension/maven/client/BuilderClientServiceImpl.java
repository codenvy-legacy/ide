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
package org.exoplatform.ide.extension.maven.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;

/**
 * Implementation of {@link BuilderClientService} service.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderClientServiceImpl.java Feb 21, 2012 12:44:05 PM azatsarynnyy $
 */
public class BuilderClientServiceImpl extends BuilderClientService {

    /** Base url. */
    private static final String BASE_URL = Utils.getWorkspaceName() + "/maven";

    /** Build project method's path. */
    private static final String BUILD    = BASE_URL + "/build";

    /** Build project method's path. */
    private static final String DEPLOY   = BASE_URL + "/deploy";

    /** Cancel building project method's path. */
    private static final String CANCEL   = BASE_URL + "/cancel";

    /** Get status of build method's path. */
    private static final String STATUS   = BASE_URL + "/status";

    /** Get result of build method's path. */
    private static final String RESULT   = BASE_URL + "/result";

    /** Get build log method's path. */
    private static final String LOG      = BASE_URL + "/log";

    /** REST-service context. */
    private String              restServiceContext;

    /** Loader to be displayed. */
    private Loader              loader;

    /**
     * @param restContext REST-service context
     * @param loader loader to show on server request
     */
    public BuilderClientServiceImpl(Loader loader) {
        this.loader = loader;
        this.restServiceContext = Utils.getRestContext();
    }

    /**
     * Start new build.
     * 
     * @throws RequestException
     * @see org.exoplatform.ide.extension.maven.client.BuilderClientService#build(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    public void build(String projectId, String vfsId, String projectName, String projectType, AsyncRequestCallback<StringBuilder> callback)
                                                                                                                                           throws RequestException {
        final String requesrUrl = restServiceContext + BUILD;

        String params = "vfsid=" + vfsId + "&projectid=" + projectId + "&name=" + projectName + "&type=" + projectType;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, requesrUrl + "?" + params)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    @Override
    public void buildAndPublish(String projectId, String vfsId, String projectName, String projectType,
                                AsyncRequestCallback<StringBuilder> callback)
                                                                             throws RequestException {
        final String requesrUrl = restServiceContext + DEPLOY;

        String params = "vfsid=" + vfsId + "&projectid=" + projectId + "&name=" + projectName + "&type=" + projectType;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, requesrUrl + "?" + params)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * Cancel previously launched build.
     * 
     * @throws RequestException
     * @see org.exoplatform.ide.extension.maven.client.BuilderClientService#cancel(java.lang.String,java.lang.String,java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    public void cancel(String buildid, String projectName, String projectType, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String requestUrl = restServiceContext + CANCEL + "/" + buildid + "?projectName=" + projectName + "&projectType="
                                  + projectType;

        AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * Check current status of previously launched build.
     * 
     * @throws RequestException
     * @see org.exoplatform.ide.extension.maven.client.BuilderClientService#status(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    public void status(String buildid, AsyncRequestCallback<BuildStatus> callback) throws RequestException {
        final String requestUrl = restServiceContext + STATUS + "/" + buildid;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, requestUrl).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /**
     * Get build log.
     * 
     * @throws RequestException
     * @see org.exoplatform.ide.extension.maven.client.BuilderClientService#log(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    public void log(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String requestUrl = restServiceContext + LOG + "/" + buildid;

        AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * Get result of previously launched build.
     * 
     * @see org.exoplatform.ide.extension.maven.client.BuilderClientService#result(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void result(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String requestUrl = restServiceContext + RESULT + "/" + buildid;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, requestUrl).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /**
     * Check is URL for download artifact is valid.
     * 
     * @see org.exoplatform.ide.extension.maven.client.BuilderClientService#checkArtifactUrl(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */

    public void checkArtifactUrl(String url, AsyncRequestCallback<Object> callback) throws RequestException {
        final String requestUrl = restServiceContext + Utils.getWorkspaceName() + "/maven/check_download_url?url=" + url;
        AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(new EmptyLoader()).send(callback);
    }
}
