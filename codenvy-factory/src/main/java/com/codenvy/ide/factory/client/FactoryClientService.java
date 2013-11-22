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
package com.codenvy.ide.factory.client;

import com.codenvy.api.factory.AdvancedFactoryUrl;
import com.codenvy.api.factory.SimpleFactoryUrl;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;

import java.util.List;

/** Client service for use factory. */
public abstract class FactoryClientService {

    /** Instance of {@link FactoryClientService}. */
    private static FactoryClientService instance;

    /**
     * Returns an instance of {@link FactoryClientService}.
     *
     * @return {@link FactoryClientService} instance
     */
    public static FactoryClientService getInstance() {
        return instance;
    }

    /** Construct client service. */
    protected FactoryClientService() {
        instance = this;
    }

    /**
     * Sends e-mail message to share Factory URL.
     *
     * @param recipient
     *         address to share Factory URL
     * @param message
     *         text message that includes Factory URL
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void share(String recipient, String message,
                               AsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Perform parsing user map with factory parameters into single {@link AdvancedFactoryUrl} bean.
     *
     * @param factoryId
     *         identificator of factory
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void getFactory(String factoryId, AsyncRequestCallback<AdvancedFactoryUrl> callback)
            throws RequestException;

    /**
     * Perform clone project via Websocket.
     *
     * @param vfsId
     *         ID of virtual file system
     * @param projectId
     *         ID of folder to which clone should be performed
     * @param factoryUrl
     *         instance of {@link AdvancedFactoryUrl}
     * @param callback
     *         callback
     * @throws WebSocketException
     */
    public abstract void cloneProjectWS(String vfsId, String projectId, SimpleFactoryUrl factoryUrl,
                                        RequestCallback<StringBuilder> callback) throws WebSocketException;

    /**
     * Perform clone project vie REST.
     *
     * @param vfsId
     *         ID of virtual file system
     * @param projectId
     *         ID of folder to which clone should be performed
     * @param factoryUrl
     *         instance of {@link AdvancedFactoryUrl}
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void cloneProject(String vfsId, String projectId, SimpleFactoryUrl factoryUrl,
                                      AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Perform copy project from temporary workspace into permanent.
     *
     * @param downloadUrl
     *         download url for the project
     * @param projectId
     *         ID of project to which should be copied
     * @param callback
     *         callback
     * @throws WebSocketException
     */
    public abstract void copyProjects(String downloadUrl, List<String> projects, RequestCallback<Void> callback) throws WebSocketException;
}
