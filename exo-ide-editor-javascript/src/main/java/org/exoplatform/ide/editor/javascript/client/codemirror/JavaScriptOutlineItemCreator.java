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
package org.exoplatform.ide.editor.javascript.client.codemirror;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.client.framework.outline.OutlineItemCreatorImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.javascript.client.JavaScriptEditorExtension;

/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class JavaScriptOutlineItemCreator extends OutlineItemCreatorImpl {
    @Override
    public ImageResource getTokenIcon(TokenBeenImpl token) {
        switch (token.getType()) {
            case FUNCTION:
                return JavaScriptEditorExtension.RESOURCES.functionItem();

            case VARIABLE:
                return JavaScriptEditorExtension.RESOURCES.varItem();

            case TAG:
                return JavaScriptEditorExtension.RESOURCES.tag();

            case PROPERTY:
                return JavaScriptEditorExtension.RESOURCES.propertyItem();

            case METHOD:
                return JavaScriptEditorExtension.RESOURCES.methodItem();

            default:
                return null;
        }
    }

    @Override
    public String getTokenDisplayTitle(TokenBeenImpl token) {
        String label = token.getName();

        // Add parameter list
        if (TokenType.FUNCTION.equals(token.getType())) {
            label += getParametersList(token);
        }

        label += getElementType(token);

        return label;
    }

}
