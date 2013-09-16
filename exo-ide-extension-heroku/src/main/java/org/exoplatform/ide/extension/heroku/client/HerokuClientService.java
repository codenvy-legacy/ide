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
package org.exoplatform.ide.extension.heroku.client;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;

/**
 * Heroku client service.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 25, 2011 12:19:59 PM anya $
 */
public abstract class HerokuClientService {
    /** Heroku client service. */
    private static HerokuClientService instance;

    /** @return {@link HerokuClientService} Heroku client service */
    public static HerokuClientService getInstance() {
        return instance;
    }

    protected HerokuClientService() {
        instance = this;
    }

    /**
     * Logins user on Heroku.
     *
     * @param login
     *         user's email
     * @param password
     *         user's password
     * @param callback
     *         callback
     */
    public abstract void login(String login, String password, AsyncRequestCallback<String> callback)
            throws RequestException;

    /**
     * Logout from Heroku.
     *
     * @param callback
     *         callback, that client has to implement
     */
    public abstract void logout(AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Creates new application on Heroku.
     *
     * @param applicationName
     *         application's name
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         GIT workdir's projectid
     * @param remoteName
     *         name of Git remote repository
     * @param callback
     *         callback
     */
    public abstract void createApplication(String applicationName, String vfsId, String projectid, String remoteName,
                                           HerokuAsyncRequestCallback callback) throws RequestException;

    /**
     * Creates new application on Heroku. Sends request over WebSocket.
     *
     * @param applicationName
     *         application's name
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         GIT workdir's projectid
     * @param remoteName
     *         name of Git remote repository
     * @param callback
     *         callback
     */
    public abstract void createApplicationWS(String applicationName, String vfsId, String projectid, String remoteName,
                                             HerokuRESTfulRequestCallback callback) throws WebSocketException;

    /**
     * Deletes application pointed by it's name or Git location.
     *
     * @param applicationName
     *         application's name
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root folder of GIT repository)
     * @param callback
     *         callback
     */
    public abstract void deleteApplication(String applicationName, String vfsId, String projectid,
                                           HerokuAsyncRequestCallback callback) throws RequestException;

    /**
     * Adds(deploys) public ssh keys on Heroku.
     *
     * @param callback
     *         callback
     */
    public abstract void addKey(HerokuAsyncRequestCallback callback) throws RequestException;

    /**
     * Clears (removes) public ssh keys from Heroku.
     *
     * @param callback
     */
    public abstract void clearKeys(HerokuAsyncRequestCallback callback) throws RequestException;

    /**
     * Get information about Heroku application, pointed by application's name or Git work directory location.
     *
     * @param applicationName
     *         application's name
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root folder of GIT repository)
     * @param isRaw
     *         the format of the response if <code>true</code> then get result as raw Map. If <code>false</code> (default)
     *         result is Map that contains predefined set of key-value pair
     * @param callback
     *         callback
     */
    public abstract void getApplicationInfo(String applicationName, String vfsId, String projectid, boolean isRaw,
                                            HerokuAsyncRequestCallback callback) throws RequestException;

    /**
     * Get the list of Heroku applications.
     *
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void listApplications(ApplicationListAsyncRequestCallback callback) throws RequestException;

    /**
     * Rename Heroku application, pointed by application's name or Git work directory location.
     *
     * @param applicationName
     *         application's name
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root folder of GIT repository)
     * @param newName
     *         new name of the application
     * @param callback
     *         callback
     */
    public abstract void renameApplication(String applicationName, String vfsId, String projectid, String newName,
                                           HerokuAsyncRequestCallback callback) throws RequestException;

    /**
     * Run Heroku rake command.
     *
     * @param applicationName
     *         name of the Heroku application (may be null)
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root folder of GIT repository)
     * @param command
     *         command to run (example: rake db:migrate)
     * @param callback
     *         callback
     */
    public abstract void run(String applicationName, String vfsId, String projectid, String command,
                             RakeCommandAsyncRequestCallback callback) throws RequestException;

    /**
     * Get application's logs.
     *
     * @param applicationName
     *         name of the Heroku application (may be null)
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root folder of GIT repository)
     * @param linesNumber
     *         number of log lines
     * @param callback
     *         callback
     */
    public abstract void logs(String applicationName, String vfsId, String projectid, int linesNumber,
                              LogsAsyncRequestCallback callback) throws RequestException;

    /**
     * Get help for rake.
     *
     * @param applicationName
     *         name of the Heroku application (may be null)
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root folder of GIT repository)
     * @param callback
     *         callback
     */
    public abstract void help(String applicationName, String vfsId, String projectid,
                              RakeCommandAsyncRequestCallback callback) throws RequestException;

    /**
     * Get the list of Heroku application's available stacks (deployment environment). The list contains info about current stack.
     *
     * @param applicationName
     *         name of the Heroku application (may be null)
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root folder of GIT repository)
     * @param callback
     *         callback
     */
    public abstract void getStackList(String applicationName, String vfsId, String projectid,
                                      StackListAsyncRequestCallback callback) throws RequestException;

    /**
     * Migrate from one stack to another (change deployment environment)
     *
     * @param applicationName
     *         name of the Heroku application (may be null)
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root folder of GIT repository)
     * @param stack
     *         name of the stack
     * @param callback
     *         callback
     */
    public abstract void migrateStack(String applicationName, String vfsId, String projectid, String stack,
                                      StackMigrationAsyncRequestCallback callback) throws RequestException;
}
