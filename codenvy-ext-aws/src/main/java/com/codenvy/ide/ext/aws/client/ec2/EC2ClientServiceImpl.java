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
package com.codenvy.ide.ext.aws.client.ec2;

import com.codenvy.ide.ext.aws.shared.ec2.InstanceInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
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
 * The implementation of {@link EC2ClientService}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class EC2ClientServiceImpl implements EC2ClientService {
    private static final String BASE_URL = '/' + Utils.getWorkspaceName() + "/aws/ec2";

    private static final String INSTANCES = BASE_URL + "/instances";

    private static final String TERMINATE_INSTANCE = BASE_URL + "/instances/terminate/";

    private static final String REBOOT_INSTANCE = BASE_URL + "/instances/reboot/";

    private static final String START_INSTANCE = BASE_URL + "/instances/start/";

    private static final String STOP_INSTANCE = BASE_URL + "/instances/stop/";

    private String restServiceContext;
    private Loader loader;

    /**
     * Create client service.
     *
     * @param restContext
     * @param loader
     */
    @Inject
    protected EC2ClientServiceImpl(@Named("restContext") String restContext, Loader loader) {
        this.loader = loader;
        this.restServiceContext = restContext;
    }

    /** {@inheritDoc} */
    @Override
    public void getInstances(AsyncRequestCallback<JsonArray<InstanceInfo>> callback) throws RequestException {
        final String url = restServiceContext + INSTANCES;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void terminateInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + TERMINATE_INSTANCE + id;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void rebootInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + REBOOT_INSTANCE + id;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stopInstance(String id, boolean force, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + STOP_INSTANCE + id + "?force=" + force;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void startInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + START_INSTANCE + id;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }
}
