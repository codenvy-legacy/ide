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
package org.exoplatform.ide.extension.php.client;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.extension.php.shared.ApplicationInstance;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Service for operations with PHP applications.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: PhpRuntimeService.java Apr 17, 2013 4:17:44 PM azatsarynnyy $
 *
 */
public abstract class PhpRuntimeService {
    /** PHP service. */
    private static PhpRuntimeService instance;

    public static PhpRuntimeService getInstance() {
        return instance;
    }

    protected PhpRuntimeService() {
        instance = this;
    }

    /**
     * Start PHP project.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectId
     *         project's id
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void start(String vfsId, ProjectModel project, RequestCallback<ApplicationInstance> callback)
            throws WebSocketException;

    /**
     * Stop running PHP application.
     *
     * @param name
     *         application's name to stop
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void stop(String name, AsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Get PHP application's logs.
     * 
     * @param name
     *         application's name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void getLogs(String name, AsyncRequestCallback<StringBuilder> callback) throws RequestException;
}
