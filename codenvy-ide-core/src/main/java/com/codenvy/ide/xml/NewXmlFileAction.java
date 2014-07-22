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
package com.codenvy.ide.xml;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.resources.ProjectsManager;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.newresource.DefaultNewResourceAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new XML file.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewXmlFileAction extends DefaultNewResourceAction {
    private static final String DEFAULT_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                                  "</xml>";

    @Inject
    public NewXmlFileAction(ProjectsManager projectsManager,
                            CoreLocalizationConstant localizationConstant,
                            SelectionAgent selectionAgent,
                            EditorAgent editorAgent,
                            Resources resources,
                            ProjectServiceClient projectServiceClient) {
        super(localizationConstant.actionNewXmlFileTitle(),
              localizationConstant.actionNewXmlFileDescription(),
              null,
              resources.defaultFile(),
              projectsManager,
              selectionAgent,
              editorAgent,
              projectServiceClient);
    }

    @Override
    protected String getExtension() {
        return "xml";
    }

    @Override
    protected String getDefaultContent() {
        return DEFAULT_CONTENT;
    }

    @Override
    protected String getMimeType() {
        return MimeType.TEXT_XML;
    }

}
