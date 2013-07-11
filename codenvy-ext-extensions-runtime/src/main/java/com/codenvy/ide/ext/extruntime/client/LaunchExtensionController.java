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
package com.codenvy.ide.ext.extruntime.client;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Controller for launching Codenvy extension.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: LaunchExtensionController.java Jul 3, 2013 3:07:52 PM azatsarynnyy $
 */
@Singleton
public class LaunchExtensionController {
    /** Project to launch. */
    private Project                        project;
    private ResourceProvider               resourceProvider;
    private ConsolePart                    console;
    private ExtRuntimeClientService        service;
    private ExtRuntimeLocalizationConstant constant;
    private String                         launchedAppId;

    /**
     * Create controller.
     * 
     * @param resourceProvider
     * @param console
     * @param service
     * @param constant
     */
    @Inject
    protected LaunchExtensionController(ResourceProvider resourceProvider,
                                        ConsolePart console,
                                        ExtRuntimeClientService service,
                                        ExtRuntimeLocalizationConstant constant) {
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.service = service;
        this.constant = constant;
    }

    /**
     * Check whether any application is launched.
     * 
     * @return <code>true</code> if any application is launched, and <code>false</code> otherwise
     */
    public boolean isAnyAppLaunched() {
        return launchedAppId != null;
    }

    /** Launch the Codenvy extension project which is currently opened. */
    public void launch() {
        project = resourceProvider.getActiveProject();
        if (project == null) {
            Window.alert("Project is not opened.");
            return;
        }

        final String projectId = project.getId();
        try {
            beforeApplicationStart();
            service.launch(resourceProvider.getVfsId(), projectId,
                           new RequestCallback<StringBuffer>() {
                               @Override
                               protected void onSuccess(StringBuffer result) {
                                   launchedAppId = result.toString();
                                   afterApplicationLaunched();
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   onApplicationLaunchFailure(exception);
                               }
                           });
        } catch (WebSocketException e) {
            console.print(e.getMessage());
        }
    }

    /** Launch the Codenvy extension project which is currently opened. */
    public void stop() {
        project = resourceProvider.getActiveProject();
        if (project == null) {
            Window.alert("Project is not opened.");
            return;
        }

        try {
            service.stop(launchedAppId,
                         new AsyncRequestCallback<Void>() {
                             @Override
                             protected void onSuccess(Void result) {
                                 console.print(constant.applicationStopped(project.getName()));
                             }

                             @Override
                             protected void onFailure(Throwable exception) {
                                 onApplicationStopFailure(exception);
                             }
                         });
        } catch (RequestException e) {
            console.print(e.getMessage());
        }
    }

    /** Performs actions before starting application. */
    private void beforeApplicationStart() {
        final String message = constant.applicationStarting();
        console.print(message);
    }

    /** Performs actions after application was successfully launched. */
    private void afterApplicationLaunched() {
        UrlBuilder builder = new UrlBuilder();
        final String uri = builder.setProtocol(Window.Location.getProtocol()).setHost(Window.Location.getHostName()).setPort(8081)
                                  .setPath("IDE").buildString();
        console.print(constant.applicationStartedOnUrls(project.getName(), "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>"));
    }

    /**
     * Performs actions when launching an application has failed.
     * 
     * @param exception an exception
     */
    private void onApplicationLaunchFailure(Throwable exception) {
        String msg = constant.startApplicationFailed();
        if (exception != null && exception.getMessage() != null) {
            msg += " : " + exception.getMessage();
        }
        console.print(msg);
    }

    /**
     * Performs actions when stopping an application has failed.
     * 
     * @param exception an exception
     */
    private void onApplicationStopFailure(Throwable exception) {
        String msg = constant.stopApplicationFailed();
        if (exception != null && exception.getMessage() != null) {
            msg += " : " + exception.getMessage();
        }
        console.print(msg);
    }
}
