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

import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Action for executing custom Docker-images on runner.
 * <p/>
 * Instantiates with {@link com.codenvy.ide.extension.runner.client.run.customimage.ImageActionFactory}.
 *
 * @author Artem Zatsarynnyy
 * @see com.codenvy.ide.extension.runner.client.run.customimage.ImageActionFactory
 */
public class RunImageAction extends Action {

    private final RunController runController;
    private final DtoFactory    dtoFactory;

    @Inject
    public RunImageAction(RunnerResources resources, RunController runController, DtoFactory dtoFactory,
                          @Assisted("title") String title, @Assisted("description") String description) {
        super(title, description, null, resources.launchApp());
        this.runController = runController;
        this.dtoFactory = dtoFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        Window.alert("Executing " + getTemplatePresentation().getText() + "...");

        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
//        runOptions.setOptions();
        runController.runActiveProject(runOptions, null, false);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(true);
    }
}
