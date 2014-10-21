/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.actions;

import java.util.ArrayList;
import java.util.List;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.runner.gwt.client.utils.RunnerUtils;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Action to view the runner 'recipe' file being used for running app.
 * 
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ViewRecipeAction extends Action {
    private final RunController controller;
    private final AppContext appContext;
    private final DtoFactory dtoFactory;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final EditorAgent editorAgent;
    private final ProjectServiceClient projectServiceClient;
    private final EventBus eventBus;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public ViewRecipeAction(RunController runController,
                            RunnerResources resources,
                            RunnerLocalizationConstant localizationConstants,
                            AppContext appContext,
                            DtoFactory dtoFactory,
                            DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            EditorAgent editorAgent,
                            ProjectServiceClient projectServiceClient,
                            EventBus eventBus,
                            AnalyticsEventLogger eventLogger) {
        super(localizationConstants.viewRecipeText(), localizationConstants.viewRecipeDescription(), null, resources.viewRecipe());
        this.controller = runController;
        this.appContext = appContext;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.editorAgent = editorAgent;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        showRecipe();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
        e.getPresentation().setEnabled(controller.isRecipeLinkExists());
    }

    /** Opens runner recipe file in editor. */
    private void showRecipe() {
        if (appContext.getCurrentProject() != null && appContext.getCurrentProject().getProcessDescriptor() != null) {
            final Link recipeLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(), "runner recipe");
            if (recipeLink != null) {
                List<Link> links = new ArrayList<>(1);
                links.add(dtoFactory.createDto(Link.class).withHref(recipeLink.getHref())
                                    .withRel("get content"));
                ItemReference recipeFileItem = dtoFactory.createDto(ItemReference.class)
                                                         .withName("Runner Recipe")
                                                         .withPath("runner_recipe")
                                                         .withMediaType("text/x-dockerfile-config")
                                                         .withLinks(links);
                final FileNode recipeFile = new RecipeFile(null, recipeFileItem, eventBus, projectServiceClient, dtoUnmarshallerFactory);
                editorAgent.openEditor(recipeFile);
                EditorPartPresenter editor = editorAgent.getOpenedEditors().get(recipeFileItem.getPath());
                if (editor instanceof CodenvyTextEditor) {
                    ((CodenvyTextEditor)editor).getView().setReadOnly(true);
                }
            }
        }
    }

    private static class RecipeFile extends FileNode {
        public RecipeFile(TreeNode< ? > parent, ItemReference data, EventBus eventBus, ProjectServiceClient projectServiceClient,
                          DtoUnmarshallerFactory dtoUnmarshallerFactory) {
            super(parent, data, eventBus, projectServiceClient, dtoUnmarshallerFactory);
        }

        @Override
        public void getContent(final AsyncCallback<String> callback) {
            for (Link link : data.getLinks()) {
                if ("get content".equals(link.getRel())) {
                    try {
                        new RequestBuilder(RequestBuilder.GET, link.getHref()).sendRequest("", new RequestCallback() {
                            @Override
                            public void onResponseReceived(Request request, Response response) {
                                callback.onSuccess(response.getText());
                            }

                            @Override
                            public void onError(Request request, Throwable exception) {
                                Log.error(RecipeFile.class, exception);
                            }
                        });
                    } catch (RequestException e) {
                        Log.error(RecipeFile.class, e);
                    }
                    break;
                }
            }
        }
    }
}
