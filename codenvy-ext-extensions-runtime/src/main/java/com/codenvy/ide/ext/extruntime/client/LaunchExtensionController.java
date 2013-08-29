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
package com.codenvy.ide.ext.extruntime.client;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.extruntime.client.marshaller.ApplicationInstanceUnmarshallerWS;
import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.Utils;
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
    private ApplicationInstance            launchedApp;

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
        return launchedApp != null;
    }

    /** Launch the Codenvy application with custom extesnion. */
    public void launch() {
        project = resourceProvider.getActiveProject();
        if (project == null) {
            Window.alert("Project is not opened.");
            return;
        }

        ApplicationInstanceUnmarshallerWS unmarshaller = new ApplicationInstanceUnmarshallerWS();

        try {
            beforeApplicationStart();
            service.launch(resourceProvider.getVfsId(), project.getId(),
                           new RequestCallback<ApplicationInstance>(unmarshaller) {
                               @Override
                               protected void onSuccess(ApplicationInstance result) {
                                   launchedApp = result;
                                   afterApplicationLaunched();
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   onFail(constant.startApplicationFailed(), exception);
                               }
                           });
        } catch (WebSocketException e) {
            console.print(e.getMessage());
        }
    }

    /** Get logs of launched application. */
    public void getLogs() {
        if (project == null) {
            Window.alert("Project is not opened.");
            return;
        }

        try {
            service.getLogs(launchedApp.getId(),
                            new AsyncRequestCallback<StringBuilder>(new com.codenvy.ide.resources.marshal.StringUnmarshaller()) {
                                @Override
                                protected void onSuccess(StringBuilder result) {
                                    console.print("<pre>" + result.toString() + "</pre>");
                                }

                                @Override
                                protected void onFailure(Throwable exception) {
                                    onFail(constant.getApplicationLogsFailed(), exception);
                                }
                            });
        } catch (RequestException e) {
            console.print(e.getMessage());
        }
    }

    /** Stop the Codenvy application. */
    public void stop() {
        if (project == null) {
            Window.alert("Project is not opened.");
            return;
        }

        try {
            service.stop(launchedApp.getId(),
                         new AsyncRequestCallback<Void>() {
                             @Override
                             protected void onSuccess(Void result) {
                                 launchedApp = null;
                                 console.print(constant.applicationStopped(project.getName()));
                             }

                             @Override
                             protected void onFailure(Throwable exception) {
                                 onFail(constant.stopApplicationFailed(), exception);
                             }
                         });
        } catch (RequestException e) {
            console.print(e.getMessage());
        }
    }

    /** Performs actions before starting application. */
    private void beforeApplicationStart() {
        final String message = constant.applicationStarting(project.getName());
        console.print(message);
    }

    /** Performs actions after application was successfully launched. */
    private void afterApplicationLaunched() {
        UrlBuilder builder = new UrlBuilder();
        final String uri = builder.setProtocol("http:").setHost(launchedApp.getHost())
                                  .setPort(launchedApp.getPort())
                                  .setPath("ide" + '/' + Utils.getWorkspaceName())
                                  .setParameter("h", launchedApp.getCodeServerHost())
                                  .setParameter("p", String.valueOf(launchedApp.getCodeServerPort())).buildString();
        console.print(constant.applicationStartedOnUrls(project.getName(), "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>"));
    }

    private void onFail(String message, Throwable exception) {
        if (exception != null && exception.getMessage() != null) {
            message += ": " + exception.getMessage();
        }
        console.print(message);
    }

}
