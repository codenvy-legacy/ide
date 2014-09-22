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

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.extension.runner.client.RunnerExtension.RUN_SCRIPT_OPTION_NAME;

/**
 * Action for executing custom Docker-images on runner.
 * <p/>
 * Instantiates with {@link com.codenvy.ide.extension.runner.client.run.customimages.ImageActionFactory}.
 *
 * @author Artem Zatsarynnyy
 * @see com.codenvy.ide.extension.runner.client.run.customimages.ImageActionFactory
 */
public class RunImageAction extends Action {

    private final RunController runController;
    private final DtoFactory    dtoFactory;
    private final String        recipesFolderPath;
    private final ItemReference scriptFile;

    @Inject
    public RunImageAction(RunnerResources resources, RunController runController, DtoFactory dtoFactory,
                          @Named("recipesFolderPath") String recipesFolderPath,
                          @Assisted("title") String title,
                          @Assisted("description") String description,
                          @Assisted ItemReference scriptFile) {
        super(title, description, null, resources.customImage());
        this.runController = runController;
        this.dtoFactory = dtoFactory;
        this.recipesFolderPath = recipesFolderPath;
        this.scriptFile = scriptFile;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
        Map<String, String> options = new HashMap<>();
        options.put(RUN_SCRIPT_OPTION_NAME, recipesFolderPath + '/' + scriptFile.getName());
        runOptions.setOptions(options);
        runController.runActiveProject(runOptions, null, false);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(!runController.isAnyAppRunning());
    }

    /** Returns the script file which this action executes. */
    public ItemReference getScriptFile() {
        return scriptFile;
    }
}
