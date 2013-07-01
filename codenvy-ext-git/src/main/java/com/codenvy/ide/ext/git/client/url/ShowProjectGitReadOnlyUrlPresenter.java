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
package com.codenvy.ide.ext.git.client.url;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter for showing git url.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 24, 2011 9:07:58 AM anya $
 */
@Singleton
public class ShowProjectGitReadOnlyUrlPresenter implements ShowProjectGitReadOnlyUrlView.ActionDelegate {
    private ShowProjectGitReadOnlyUrlView view;
    private GitClientService              service;
    private ResourceProvider              resourceProvider;
    private ConsolePart                   console;
    private GitLocalizationConstant       constant;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param console
     * @param constant
     */
    @Inject
    public ShowProjectGitReadOnlyUrlPresenter(ShowProjectGitReadOnlyUrlView view, GitClientService service,
                                              ResourceProvider resourceProvider, ConsolePart console, GitLocalizationConstant constant) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
    }

    /** Show dialog. */
    public void showDialog() {
        String projectId = resourceProvider.getActiveProject().getId();
        StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());

        try {
            service.getGitReadOnlyUrl(resourceProvider.getVfsId(), projectId, new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                @Override
                protected void onSuccess(StringBuilder result) {
                    view.setUrl(result.toString());
                    view.showDialog();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage = exception.getMessage() != null && !exception.getMessage().isEmpty() ? exception.getMessage()
                                                                                                              : constant.initFailed();
                    console.print(errorMessage);
                }
            });
        } catch (RequestException e) {
            String errorMessage = e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : constant.initFailed();
            console.print(errorMessage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }
}