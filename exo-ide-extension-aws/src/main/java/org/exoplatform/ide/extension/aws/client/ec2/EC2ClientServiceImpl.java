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
package org.exoplatform.ide.extension.aws.client.ec2;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceInfo;

import java.util.List;

/**
 * Implementation of {@link EC2ClientService} service.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2ClientServiceImpl.java Sep 21, 2012 12:31:29 PM azatsarynnyy $
 */
public class EC2ClientServiceImpl extends EC2ClientService {

    private static final String BASE_URL = Utils.getWorkspaceName() + "/aws/ec2";

    private static final String INSTANCES = BASE_URL + "/instances";

    private static final String TERMINATE_INSTANCE = BASE_URL + "/instances/terminate/";

    private static final String REBOOT_INSTANCE = BASE_URL + "/instances/reboot/";

    private static final String START_INSTANCE = BASE_URL + "/instances/start/";

    private static final String STOP_INSTANCE = BASE_URL + "/instances/stop/";

    /** REST service context. */
    private String restServiceContext;

    /** Loader to be displayed. */
    private Loader loader;

    public EC2ClientServiceImpl(Loader loader) {
        this.loader = loader;
        this.restServiceContext = Utils.getRestContext();
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2ClientService#getInstances(org.exoplatform.gwtframework.commons.rest
     * .AsyncRequestCallback) */
    @Override
    public void getInstances(AsyncRequestCallback<List<InstanceInfo>> callback) throws RequestException {
        final String url = restServiceContext + INSTANCES;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.ec2.EC2ClientService#terminateInstance(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void terminateInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + TERMINATE_INSTANCE + id;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.ec2.EC2ClientService#rebootInstance(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void rebootInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + REBOOT_INSTANCE + id;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.ec2.EC2ClientService#stopInstance(java.lang.String, boolean,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void stopInstance(String id, boolean force, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + STOP_INSTANCE + id + "?force=" + force;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.ec2.EC2ClientService#startInstance(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void startInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + START_INSTANCE + id;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

}
