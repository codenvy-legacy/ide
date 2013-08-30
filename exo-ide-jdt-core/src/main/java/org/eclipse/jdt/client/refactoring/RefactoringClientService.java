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
