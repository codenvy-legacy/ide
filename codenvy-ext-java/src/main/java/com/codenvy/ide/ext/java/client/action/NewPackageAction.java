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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.java.client.JavaLocalizationConstant;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;
import com.codenvy.ide.newresource.DefaultNewResourceAction;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Action to create new Java package.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewPackageAction extends DefaultNewResourceAction {
    @Inject
    public NewPackageAction(JavaResources javaResources,
                            JavaLocalizationConstant localizationConstant,
                            AppContext appContext,
                            SelectionAgent selectionAgent,
                            EditorAgent editorAgent,
                            ProjectServiceClient projectServiceClient,
                            DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            EventBus eventBus) {
        super(localizationConstant.actionNewPackageTitle(),
              localizationConstant.actionNewPackageDescription(),
              null,
              javaResources.packageIcon(),
              appContext,
              selectionAgent,
              editorAgent,
              projectServiceClient,
              dtoUnmarshallerFactory,
              eventBus);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        new AskValueDialog("New " + title, "Name:", new AskValueCallback() {
//            @Override
//            public void onOk(String value) {
//                JavaProject activeProject = (JavaProject)resourceProvider.getActiveProject();
//                activeProject.createPackage(getParent(), value, new AsyncCallback<Package>() {
//                    @Override
//                    public void onSuccess(Package result) {
//                    }
//
//                    @Override
//                    public void onFailure(Throwable caught) {
//                        new Info(caught.getMessage()).show();
//                    }
//                });
//            }
//        }
//        ).show();
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
