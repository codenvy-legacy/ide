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
package com.codenvy.ide.ext.git.client.delete;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Delete repository command handler, performs deleting Git repository.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 21, 2011 5:57:30 PM anya $
 */
@Singleton
public class DeleteRepositoryPresenter {
    private GitClientService        service;
    private EventBus                eventBus;
    private GitLocalizationConstant constant;
    private ConsolePart             console;
    private ResourceProvider        resourceProvider;
    private Project                 project;

    /**
     * Create presenter.
     *
     * @param service
     * @param eventBus
     * @param constant
     * @param console
     * @param resourceProvider
     */
    @Inject
    public DeleteRepositoryPresenter(GitClientService service, EventBus eventBus, GitLocalizationConstant constant, ConsolePart console,
                                     ResourceProvider resourceProvider) {
        this.service = service;
        this.eventBus = eventBus;
        this.constant = constant;
        this.console = console;
        this.resourceProvider = resourceProvider;
    }

    /** Delete Git repository. */
    public void deleteRepository() {
        project = resourceProvider.getActiveProject();
        String workDir = project.getPath();
        askBeforeDelete(workDir);
    }

    /**
     * Confirm, that user wants to delete Git repository.
     *
     * @param repository
     *         repository name
     */
    private void askBeforeDelete(@NotNull String repository) {
        boolean needToDelete = Window.confirm(constant.deleteGitRepositoryQuestion(repository));
        if (needToDelete) {
            doDeleteRepository();
        }
    }

    /** Perform deleting Git repository. */
    private void doDeleteRepository() {
        try {
            service.deleteRepository(resourceProvider.getVfsId(), project.getId(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    project.refreshProperties(new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            console.print(constant.deleteGitRepositorySuccess());
                            eventBus.fireEvent(new RefreshBrowserEvent(project));
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(DeleteRepositoryPresenter.class, caught);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}