/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package org.exoplatform.ide.editor.java.client.codeassistant;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.java.client.codeassistant.ui.*;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 25, 2010 4:50:06 PM evgen $
 */
public class JavaTokenWidgetFactory implements TokenWidgetFactory {

    private String restContext;

    private String projectId;

    /** @param context */
    public JavaTokenWidgetFactory(String context) {
        restContext = context;
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.api.TokenWidgetFactory#getTokenWidget(java.lang.Object, int) */
    @Override
    public TokenWidget buildTokenWidget(Token token) {
        switch (token.getType()) {
            case CLASS:
            case INTERFACE:
            case ANNOTATION:
                return new JavaClassTokenWidget(token, restContext, projectId);

            case CONSTRUCTOR:
                return new JavaConstructorWidget(token, restContext, projectId);

            case METHOD:
                return new JavaMethodWidget(token, restContext, projectId);

            case FIELD:
            case PROPERTY:
                return new JavaFieldWidget(token, restContext, projectId);

            case VARIABLE:
            case PARAMETER:
                return new JavaVariableWidget(token);

            case KEYWORD:
                return new JavaKeyWordWidget(token);

            default:
                return new JavaClassTokenWidget(token, restContext, projectId);

        }
    }

    /** @return the projectId */
    public String getProjectId() {
        return projectId;
    }

    /**
     * @param projectId
     *         the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

}
