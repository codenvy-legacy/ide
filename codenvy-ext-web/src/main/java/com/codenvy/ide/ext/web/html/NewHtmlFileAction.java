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
package com.codenvy.ide.ext.web.html;

import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.ext.web.WebExtensionResource;
import com.codenvy.ide.ext.web.WebLocalizationConstant;
import com.codenvy.ide.newresource.DefaultNewResourceAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new HTML file.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewHtmlFileAction extends DefaultNewResourceAction {
    private static final String DEFAULT_CONTENT = "<!DOCTYPE html>\n" +
                                                  "<html>\n" +
                                                  "<head>\n" +
                                                  "    <title></title>\n" +
                                                  "</head>\n" +
                                                  "<body>\n" +
                                                  "\n" +
                                                  "</body>\n" +
                                                  "</html>";

    @Inject
    public NewHtmlFileAction(ResourceProvider resourceProvider,
                             WebExtensionResource webExtensionResource,
                             WebLocalizationConstant localizationConstant,
                             SelectionAgent selectionAgent,
                             EditorAgent editorAgent) {
        super(localizationConstant.newHtmlFileActionTitle(),
              localizationConstant.newHtmlFileActionDescription(),
              webExtensionResource.html(),
              null,
              resourceProvider,
              selectionAgent,
              editorAgent);
    }

    @Override
    protected String getExtension() {
        return  "html";
    }

    @Override
    protected String getDefaultContent() {
        return DEFAULT_CONTENT;
    }

    @Override
    protected String getMimeType() {
        return MimeType.TEXT_HTML;
    }
}
