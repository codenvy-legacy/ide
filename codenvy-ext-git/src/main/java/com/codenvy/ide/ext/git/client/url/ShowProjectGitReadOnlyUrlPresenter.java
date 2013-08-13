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