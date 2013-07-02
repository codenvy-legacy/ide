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
package com.codenvy.ide.ext.aws.client.ec2;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.shared.ec2.InstanceInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.websocket.MessageBus;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class EC2ClientServiceImpl implements EC2ClientService {
    private static final String BASE_URL = "/ide/aws/ec2";

    private static final String INSTANCES = BASE_URL + "/instances";

    private static final String TERMINATE_INSTANCE = BASE_URL + "/instances/terminate/";

    private static final String REBOOT_INSTANCE = BASE_URL + "/instances/reboot/";

    private static final String START_INSTANCE = BASE_URL + "/instances/start/";

    private static final String STOP_INSTANCE = BASE_URL + "/instances/stop/";

    private String                  restServiceContext;
    private Loader                  loader;
    private MessageBus              wsMessageBus;
    private EventBus                eventBus;
    private AWSLocalizationConstant constant;

    @Inject
    protected EC2ClientServiceImpl(@Named("restContext") String restContext, Loader loader, MessageBus wsMessageBus,
                                   EventBus eventBus, AWSLocalizationConstant constant) {
        this.loader = loader;
        this.restServiceContext = restContext;
        this.wsMessageBus = wsMessageBus;
        this.eventBus = eventBus;
        this.constant = constant;
    }

    /**
     * Returns the list of EC2 instances.
     *
     * @param callback
     * @throws com.google.gwt.http.client.RequestException
     *
     */
    @Override
    public void getInstances(AsyncRequestCallback<JsonArray<InstanceInfo>> callback) throws RequestException {
        final String url = restServiceContext + INSTANCES;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /**
     * Terminate the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param callback
     * @throws com.google.gwt.http.client.RequestException
     *
     */
    @Override
    public void terminateInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + TERMINATE_INSTANCE + id;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /**
     * Reboot the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param callback
     * @throws com.google.gwt.http.client.RequestException
     *
     */
    @Override
    public void rebootInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + REBOOT_INSTANCE + id;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /**
     * Stop the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param force
     *         forces the instance to stop
     * @param callback
     * @throws com.google.gwt.http.client.RequestException
     *
     */
    @Override
    public void stopInstance(String id, boolean force, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + STOP_INSTANCE + id + "?force=" + force;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * Start the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param callback
     * @throws com.google.gwt.http.client.RequestException
     *
     */
    @Override
    public void startInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException {
        final String url = restServiceContext + START_INSTANCE + id;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }
}
