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
