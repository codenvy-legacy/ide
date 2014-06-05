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
package com.codenvy.ide.ext.java.client.action;

import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.java.client.JavaLocalizationConstant;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.client.newresource.NewJavaResourcePresenter;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new Java file (e.g. class, enum, ...).
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewJavaClassAction extends Action {
    private NewJavaResourcePresenter newJavaResourcePresenter;
    private SelectionAgent           selectionAgent;

    @Inject
    public NewJavaClassAction(NewJavaResourcePresenter newJavaResourcePresenter,
                              SelectionAgent selectionAgent,
                              JavaLocalizationConstant constant,
                              JavaResources resources) {
        super(constant.actionNewClassTitle(), constant.actionNewClassDescription(), resources.classItem());
        this.newJavaResourcePresenter = newJavaResourcePresenter;
        this.selectionAgent = selectionAgent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        newJavaResourcePresenter.showDialog();
    }

    @Override
    public void update(ActionEvent e) {
        boolean enabled = false;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            if (selection.getFirstElement() instanceof Resource) {
                Resource resource = (Resource)selection.getFirstElement();
                if (resource.isFile()) {
                    resource = resource.getParent();
                }
                enabled = resource instanceof com.codenvy.ide.ext.java.client.projectmodel.Package || resource instanceof SourceFolder;
            }
        }
        e.getPresentation().setEnabledAndVisible(enabled);
    }
}
