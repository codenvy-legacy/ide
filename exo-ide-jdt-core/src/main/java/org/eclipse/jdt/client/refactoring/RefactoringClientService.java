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
package org.eclipse.jdt.client.refactoring;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;

import java.util.List;

/**
 * Client service for refactoring features.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RefactoringClientService.java Jan 17, 2013 4:11:22 PM azatsarynnyy $
 */
public abstract class RefactoringClientService {

    private static RefactoringClientService instance;

    public static RefactoringClientService getInstance() {
        return instance;
    }

    protected RefactoringClientService() {
        instance = this;
    }

    /**
     * Rename a Java element using refactoring. Send request over WebSocket.
     *
     * @param vfsId
     * @param projectId
     * @param fqn
     * @param offset
     * @param newName
     * @param callback
     * @throws WebSocketException
     */
    public abstract void renameWS(String vfsId, String projectId, String fqn, int offset, String newName,
                                  RequestCallback<List<Action>> callback) throws WebSocketException;

    /**
     * Rename a Java element using refactoring. Send request over HTTP.
     *
     * @param vfsId
     * @param projectId
     * @param fqn
     * @param offset
     * @param newName
     * @param callback
     * @throws RequestException
     */
    public abstract void rename(String vfsId, String projectId, String fqn, int offset, String newName,
                                AsyncRequestCallback<List<Action>> callback) throws RequestException;

}
