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

        StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());

        try {
            service.statusText(resourceProvider.getVfsId(), project.getId(), false, new AsyncRequestCallback(unmarshaller) {
                @Override
                protected void onSuccess(Object result) {
                    String output = result.toString();
                    console.print(output);
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