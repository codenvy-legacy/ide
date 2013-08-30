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
package com.codenvy.ide.ext.git.client.status;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Handler to process actions with displaying the status of the Git work tree.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 28, 2011 3:58:20 PM anya $
 */
@Singleton
public class StatusCommandPresenter {
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private GitLocalizationConstant constant;
    private ConsolePart             console;

    /**
     * Create presenter.
     *
     * @param service
     * @param resourceProvider
     * @param console
     * @param constant
     */
    @Inject
    public StatusCommandPresenter(GitClientService service, ResourceProvider resourceProvider, ConsolePart console,
                                  GitLocalizationConstant constant) {
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
    }

    /** Show status. */
    public void showStatus() {
        Project project = resourceProvider.getActiveProject();
        if (project == null) {
            return;
        }

        StringUnmarshaller unmarshaller = new StringUnmarshaller();

        try {
            service.statusText(resourceProvider.getVfsId(), project.getId(), false, new AsyncRequestCallback<String>(unmarshaller) {
                @Override
                protected void onSuccess(String result) {
                    console.print(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage = exception.getMessage() != null ? exception.getMessage() : constant.statusFailed();
                    console.print(errorMessage);
                }
            });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : constant.statusFailed();
            console.print(errorMessage);
        }
    }
}