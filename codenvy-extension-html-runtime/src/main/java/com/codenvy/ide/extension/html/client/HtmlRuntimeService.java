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
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

/**
 * Client service for running/stopping Factory HTML applications.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlRuntimeService.java Jun 26, 2013 11:10:07 AM azatsarynnyy $
 */
public abstract class HtmlRuntimeService {
    /** {@link HtmlRuntimeService} instance. */
    private static HtmlRuntimeService instance;

    /**
     * Returns instance of {@link HtmlRuntimeService}.
     * 
     * @return
     */
    public static HtmlRuntimeService getInstance() {
        return instance;
    }

    protected HtmlRuntimeService() {
        instance = this;
    }

    /**
     * Start HTML project.
     * 
     * @param vfsId virtual file system id
     * @param projectId project's id
     * @param callback callback
     * @throws RequestException
     */
    public abstract void start(String vfsId, String projectId, AsyncRequestCallback<ApplicationInstance> callback)
                                                                                                                  throws RequestException;

    /**
     * Stop running HTML application.
     * 
     * @param name application's name to stop
     * @param callback callback
     * @throws RequestException
     */
    public abstract void stop(String name, AsyncRequestCallback<Object> callback) throws RequestException;
}
