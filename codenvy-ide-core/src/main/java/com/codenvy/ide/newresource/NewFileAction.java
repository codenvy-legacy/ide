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
package com.codenvy.ide.newresource;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Action to create new folder.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewFileAction extends DefaultNewResourceAction {
    @Inject
    public NewFileAction(AppContext appContext,
                         CoreLocalizationConstant localizationConstant,
                         SelectionAgent selectionAgent,
                         EditorAgent editorAgent,
                         Resources resources,
                         ProjectServiceClient projectServiceClient,
                         EventBus eventBus) {
        super(localizationConstant.actionNewFileTitle(),
              localizationConstant.actionNewFileDescription(),
              null,
              resources.defaultFile(),
              appContext,
              selectionAgent,
              editorAgent,
              projectServiceClient,
              eventBus);
    }

    @Override
    protected String getMimeType() {
        return MimeType.TEXT_PLAIN;
    }
}
