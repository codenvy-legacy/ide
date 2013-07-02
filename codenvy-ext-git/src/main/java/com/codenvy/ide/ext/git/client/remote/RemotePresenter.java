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
package com.codenvy.ide.ext.git.client.remote;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.RemoteListUnmarshaller;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter for working with remote repository list (view, add and delete).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 18, 2011 11:13:30 AM anya $
 */
@Singleton
public class RemotePresenter implements RemoteView.ActionDelegate {
    private RemoteView              view;
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private GitLocalizationConstant constant;
    private ConsolePart             console;
    private Remote                  selectedRemote;
    private Project                 project;

    @Inject
    public RemotePresenter(RemoteView view, GitClientService service, ResourceProvider resourceProvider, GitLocalizationConstant constant,
                           ConsolePart console) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.console = console;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        getRemotes();
    }

    /**
     * Get the list of remote repositories for local one. If remote repositories are found, then get the list of branches (remote and
     * local).
     */
    private void getRemotes() {
        RemoteListUnmarshaller unmarshaller = new RemoteListUnmarshaller(JsonCollections.<Remote>createArray());
        try {
            service.remoteList(resourceProvider.getVfsId(), project.getId(), null, true,
                               new AsyncRequestCallback<JsonArray<Remote>>(unmarshaller) {
                                   @Override
                                   protected void onSuccess(JsonArray<Remote> result) {
                                       view.setEnableDeleteButton(false);
                                       view.setRemotes(result);
                                       if (!view.isShown()) {
                                           view.showDialog();
                                       }
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       String errorMessage =
                                               exception.getMessage() != null ? exception.getMessage() : constant.remoteListFailed();
                                       Window.alert(errorMessage);
                                   }
                               });
        } catch (RequestException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : constant.remoteListFailed();
            Window.alert(errorMessage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onAddClicked() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        if (selectedRemote == null) {
            Window.alert(constant.selectRemoteRepositoryFail());
            return;
        }

        String name = selectedRemote.getName();
        boolean needToDelete = Window.confirm(constant.deleteRemoteRepositoryQuestion(name));
        if (needToDelete) {
            try {
                service.remoteDelete(resourceProvider.getVfsId(), project.getId(), name, new AsyncRequestCallback<String>() {
                    @Override
                    protected void onSuccess(String result) {
                        getRemotes();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        String errorMessage = exception.getMessage() != null ? exception.getMessage() : constant.remoteDeleteFailed();
                        console.print(errorMessage);
                    }
                });
            } catch (RequestException e) {
                String errorMessage = e.getMessage() != null ? e.getMessage() : constant.remoteDeleteFailed();
                console.print(errorMessage);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoteSelected(@NotNull Remote remote) {
        selectedRemote = remote;
        view.setEnableDeleteButton(true);
    }
}