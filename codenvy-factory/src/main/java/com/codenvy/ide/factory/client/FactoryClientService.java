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

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

/**
 * Client service for sharing Factory URL by e-mail messages.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryClientService.java Jun 25, 2013 11:32:37 PM azatsarynnyy $
 *
 */
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

    protected FactoryClientService() {
        instance = this;
    }

    /**
     * Sends e-mail message to share Factory URL.
     * 
     * @param recipient address to share Factory URL
     * @param message text message that includes Factory URL
     * @param callback callback
     * @throws RequestException
     */
    public abstract void share(String recipient, String message,
                              AsyncRequestCallback<Object> callback) throws RequestException;
}
