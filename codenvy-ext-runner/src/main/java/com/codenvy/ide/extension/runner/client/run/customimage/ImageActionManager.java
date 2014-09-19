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
package com.codenvy.ide.extension.runner.client.run.customimage;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.keybinding.KeyBuilder;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.actions.RunImageAction;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.input.CharCodeWithModifiers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.extension.runner.client.RunnerExtension.GROUP_CUSTOM_IMAGES;

/**
 * Listens for opening/closing a project and adds/removes
 * a corresponding action for executing every custom Docker-script.
 *
 * @author Artem Zatsarynnyy
 */
public class ImageActionManager implements ProjectActionHandler {

    /** Project-relative path to the custom Docker-scripts folder. */
    static final String SCRIPTS_FOLDER_REL_LOCATION = "/.codenvy/scripts";
    private final Map<RunImageAction, CharCodeWithModifiers> actions2HotKeys;
    private final ImageActionFactory                         imageActionFactory;
    private final RunnerLocalizationConstant                 localizationConstants;
    private final ActionManager                              actionManager;
    private final KeyBindingAgent                            keyBindingAgent;
    private final ProjectServiceClient                       projectServiceClient;
    private final DtoUnmarshallerFactory                     dtoUnmarshallerFactory;

    @Inject
    public ImageActionManager(ImageActionFactory imageActionFactory,
                              RunnerLocalizationConstant localizationConstants,
                              ActionManager actionManager,
                              KeyBindingAgent keyBindingAgent,
                              RunnerResources resources,
                              EventBus eventBus,
                              ProjectServiceClient projectServiceClient,
                              DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.imageActionFactory = imageActionFactory;
        this.localizationConstants = localizationConstants;
        this.actionManager = actionManager;
        this.keyBindingAgent = keyBindingAgent;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;

        actions2HotKeys = new HashMap<>();
        eventBus.addHandler(ProjectActionEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(ProjectActionEvent event) {
        retrieveCustomImages(event.getProject(), new AsyncCallback<Array<ItemReference>>() {
            @Override
            public void onSuccess(Array<ItemReference> result) {
                for (ItemReference item : result.asIterable()) {
                    addActionForScript(item);
                }
            }

            @Override
            public void onFailure(Throwable ignore) {
                // no scripts are found
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectClosed(ProjectActionEvent event) {
        removeAllActions();
    }

    /**
     * Retrieve list of custom images.
     *
     * @param project
     *         project for which need to get list of images
     * @param callback
     *         callback to return custom images
     */
    void retrieveCustomImages(ProjectDescriptor project, final AsyncCallback<Array<ItemReference>> callback) {
        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(project.getPath() + SCRIPTS_FOLDER_REL_LOCATION,
                                         new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
                                             @Override
                                             protected void onSuccess(Array<ItemReference> result) {
                                                 callback.onSuccess(result);
                                             }

                                             @Override
                                             protected void onFailure(Throwable caught) {
                                                 callback.onFailure(caught);
                                             }
                                         });
    }

    /**
     * Add action to execute the specified script.
     *
     * @param scriptFile
     *         script for which need to create action
     */
    public void addActionForScript(ItemReference scriptFile) {
        final int actionNum = actions2HotKeys.size() + 1;
        final RunImageAction action = imageActionFactory.createAction(localizationConstants.imageActionText(scriptFile.getName()),
                                                                      localizationConstants.imageActionDescription(scriptFile.getName()),
                                                                      scriptFile);
        final String actionId = localizationConstants.imageActionId(actionNum);
        actionManager.registerAction(actionId, action);
        ((DefaultActionGroup)actionManager.getAction(GROUP_CUSTOM_IMAGES)).add(action);

        CharCodeWithModifiers hotKey = null;
        // Bind hot-key only for the first 10 actions (Ctrl+Alt+0...9)
        if (actionNum <= 10) {
            hotKey = new KeyBuilder().action().alt().charCode(actionNum + 47).build();
            keyBindingAgent.getGlobal().addKey(hotKey, actionId);
        }
        actions2HotKeys.put(action, hotKey);
    }

    /**
     * Remove action which corresponds to the specified script.
     *
     * @param scriptFile
     *         script for which need to remove action
     */
    public void removeActionForScript(ItemReference scriptFile) {
        for (RunImageAction action : actions2HotKeys.keySet()) {
            if (scriptFile.equals(action.getScriptFile())) {
                removeAction(action);
                break;
            }
        }
    }

    private void removeAction(RunImageAction action) {
        DefaultActionGroup customImagesGroup = (DefaultActionGroup)actionManager.getAction(GROUP_CUSTOM_IMAGES);
        customImagesGroup.remove(action);

        final String actionId = actionManager.getId(action);
        actionManager.unregisterAction(actionId);

        // unbind hot-key if action has it
        final CharCodeWithModifiers hotKey = actions2HotKeys.get(action);
        if (hotKey != null) {
            keyBindingAgent.getGlobal().removeKey(hotKey, actionId);
        }

        actions2HotKeys.remove(action);
    }

    private void removeAllActions() {
        for (Map.Entry<RunImageAction, CharCodeWithModifiers> entry : actions2HotKeys.entrySet()) {
            removeAction(entry.getKey());
        }
    }
}
