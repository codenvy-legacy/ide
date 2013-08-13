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