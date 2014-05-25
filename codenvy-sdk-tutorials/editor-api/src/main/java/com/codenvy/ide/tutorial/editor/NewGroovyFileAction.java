/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.tutorial.editor;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.newresource.DefaultNewResourceAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new Groovy file.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewGroovyFileAction extends DefaultNewResourceAction {

    private EditorTutorialResource resource;

    @Inject
    public NewGroovyFileAction(ResourceProvider resourceProvider,
                               EditorTutorialResource resource,
                               SelectionAgent selectionAgent,
                               EditorAgent editorAgent) {
        super("Groovy file",
              "Creates new Groovy file",
              resource.groovyFile(),
              null,
              resourceProvider,
              selectionAgent,
              editorAgent);
        this.resource = resource;
    }

    @Override
    protected String getExtension() {
        return "groovy";
    }

    @Override
    protected String getDefaultContent() {
        return resource.contentFile().getText();
    }

    @Override
    protected String getMimeType() {
        return "text/x-groovy";
    }
}
