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

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service to create 'Tutorial' projects.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TutorialsClientService.java Sep 13, 2013 12:48:08 PM azatsarynnyy $
 */
public interface TutorialsClientService {

    /**
     * Create 'DTO tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param properties
     *         properties to set to a newly created project
     * @param callback
     *         callback
     * @throws RequestException
     */
    void createDTOTutorialProject(@NotNull String projectName, @NotNull JsonArray<Property> properties,
                                  @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'Notification tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param properties
     *         properties to set to a newly created project
     * @param callback
     *         callback
     * @throws RequestException
     */
    void createNotificationTutorialProject(@NotNull String projectName, @NotNull JsonArray<Property> properties,
                                           @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'Notification tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param properties
     *         properties to set to a newly created project
     * @param callback
     *         callback
     * @throws RequestException
     */
    void createActionTutorialProject(@NotNull String projectName, @NotNull JsonArray<Property> properties,
                                     @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'Wizard tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param properties
     *         properties to set to a newly created project
     * @param callback
     *         callback
     * @throws RequestException
     */
    void createWizardTutorialProject(@NotNull String projectName, @NotNull JsonArray<Property> properties,
                                     @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'New project wizard tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param properties
     *         properties to set to a newly created project
     * @param callback
     *         callback
     * @throws RequestException
     */
    void createNewProjectWizardTutorialProject(@NotNull String projectName, @NotNull JsonArray<Property> properties,
                                               @NotNull AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create 'New resource wizard tutorial' project.
     *
     * @param projectName
     *         name of the project to create
     * @param properties
     *         properties to set to a newly created project
     * @param callback
     *         callback
     * @throws RequestException
     */
    void createNewResourceWizardTutorialProject(@NotNull String projectName, @NotNull JsonArray<Property> properties,
                                                @NotNull AsyncRequestCallback<Void> callback) throws RequestException;
}