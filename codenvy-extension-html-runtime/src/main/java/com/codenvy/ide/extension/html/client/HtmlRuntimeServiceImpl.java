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
package com.codenvy.ide.extension.html.client;

import com.codenvy.ide.extension.html.shared.ApplicationInstance;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Implementation of {@link HtmlRuntimeService}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlRuntimeServiceImpl.java Jun 26, 2013 11:10:54 AM azatsarynnyy $
 */
public class HtmlRuntimeServiceImpl extends HtmlRuntimeService {

    private static final String BASE_URL         = "/html/runner";

    private static final String RUN_APPLICATION  = BASE_URL + "/run";

    private static final String STOP_APPLICATION = BASE_URL + "/stop";

    private final String        wsName;

    /** REST service context. */
    private final String        restContext;

    public HtmlRuntimeServiceImpl(String restContext, String wsName) {
        this.restContext = restContext;
        this.wsName = wsName;
    }

    /**
     * @see com.codenvy.ide.extension.html.client.HtmlRuntimeService#start(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void start(String vfsId, String projectId, AsyncRequestCallback<ApplicationInstance> callback)
                                                                                                         throws RequestException {
        String requestUrl = restContext + wsName + RUN_APPLICATION;
        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);
        AsyncRequest.build(RequestBuilder.GET, requestUrl + params.toString()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /**
     * @see com.codenvy.ide.extension.html.client.HtmlRuntimeService#stop(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void stop(String name, AsyncRequestCallback<Object> callback) throws RequestException {
        String requestUrl = restContext + wsName + STOP_APPLICATION;
        StringBuilder params = new StringBuilder("?");
        params.append("name=").append(name);
        AsyncRequest.build(RequestBuilder.GET, requestUrl + params.toString()).send(callback);
    }
}
