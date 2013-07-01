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
package com.codenvy.ide.ext.git.client.add;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.js.JsoArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter for add changes to Git index.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 29, 2011 4:35:16 PM anya $
 */
@Singleton
public class AddToIndexPresenter implements AddToIndexView.ActionDelegate {
    private AddToIndexView          view;
    private GitClientService        service;
    private ConsolePart             console;
    private GitLocalizationConstant constant;
    private ResourceProvider        resourceProvider;
    private Project                 project;

    /**
     * Create presenter
     *
     * @param view
     * @param service
     * @param console
     * @param constant
     * @param resourceProvider
     */
    @Inject
    public AddToIndexPresenter(AddToIndexView view, GitClientService service, ConsolePart console, GitLocalizationConstant constant,
                               ResourceProvider resourceProvider) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.console = console;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        String workDir = project.getPath();
        view.setMessage(formMessage(workDir));
        view.setUpdated(false);
        view.showDialog();
    }

    /**
     * Form the message to display for adding to index, telling the user what is gonna to be added.
     *
     * @return {@link String} message to display
     */
    private String formMessage(String workdir) {
        // TODO we don't know selected item
        //        if (selectedItem == null) {
        //            return "";
        //        }
        //        String pattern = selectedItem.getPath().replaceFirst(workdir, "");
        //        pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;
        //
        //        // Root of the working tree:
        //        if (pattern.length() == 0 || "/".equals(pattern)) {
        //            return GitExtension.MESSAGES.addToIndexAllChanges();
        //        }
        //
        //        if (selectedItem instanceof Folder) {
        //            return GitExtension.MESSAGES.addToIndexFolder(pattern);
        //        } else {
        //            return GitExtension.MESSAGES.addToIndexFile(pattern);
        //        }

        return constant.addToIndexAllChanges();
    }

    /** {@inheritDoc} */
    @Override
    public void onAddClicked() {
        boolean update = view.isUpdated();

        try {
            service.addWS(resourceProvider.getVfsId(), project, update, getFilePatterns(), new RequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    console.print(constant.addSuccess());
                    // TODO
                    //IDE.fireEvent(new TreeRefreshedEvent(getSelectedProject()));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                }
            });
            view.close();
        } catch (WebSocketException e) {
            doAddREST(project, update);
        }
    }

    /** Perform adding to index (sends request over HTTP). */
    private void doAddREST(Project project, boolean update) {
        try {
            service.add(resourceProvider.getVfsId(), project, update, getFilePatterns(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    console.print(constant.addSuccess());
                    // TODO
                    //IDE.fireEvent(new TreeRefreshedEvent(getSelectedProject()));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                }
            });
        } catch (RequestException e) {
            handleError(e);
        }
        view.close();
    }

    /**
     * Returns pattern of the files to be added.
     *
     * @return pattern of the files to be added
     */
    private JsonArray<String> getFilePatterns() {
        String projectPath = project.getPath();
        // TODO we don't know selected item
        String pattern = projectPath;
        // String pattern = selectedItem.getPath().replaceFirst(projectPath, "");

        pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;

        JsoArray<String> patterns = JsoArray.create();
        if (pattern.isEmpty() || "/".equals(pattern)) {
            patterns.add(".");
        } else {
            patterns.add(pattern);
        }

        return patterns;
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     */
    private void handleError(Throwable e) {
        String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : constant.addFailed();
        console.print(errorMessage);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}