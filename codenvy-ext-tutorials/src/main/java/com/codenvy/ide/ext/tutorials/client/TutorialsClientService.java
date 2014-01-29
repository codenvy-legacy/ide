/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.tutorials.client;

import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import javax.validation.constraints.NotNull;

/**
 * Client service to create 'Tutorial' projects.
 *
 * @author Artem Zatsarynnyy
 */
public interface TutorialsClientService {

    /**
     * Create 'Notification tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param callback
     *         callback
     * @throws RequestException
     */
    void unzipNotificationTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'Notification tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param callback
     *         callback
     * @throws RequestException
     */
    void unzipActionTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'Wizard tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param callback
     *         callback
     * @throws RequestException
     */
    void unzipWizardTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'New project wizard tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param callback
     *         callback
     * @throws RequestException
     */
    void unzipNewProjectWizardTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'New resource wizard tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param callback
     *         callback
     * @throws RequestException
     */
    void unzipNewResourceWizardTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'Parts tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param callback
     *         callback
     * @throws RequestException
     */
    void unzipPartsTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'Editor tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param properties
     *         properties to set to a newly created project
     * @param callback
     *         callback
     * @throws RequestException
     */
    void unzipEditorTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'GIN tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param callback
     *         callback
     * @throws RequestException
     */
    void unzipGinTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'WYSIWYG Editor' project.
     *
     * @param projectName
     *         name of the project to create
     * @param callback
     *         callback
     * @throws RequestException
     */
    void unzipWYSIWYGEditorTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException;
}