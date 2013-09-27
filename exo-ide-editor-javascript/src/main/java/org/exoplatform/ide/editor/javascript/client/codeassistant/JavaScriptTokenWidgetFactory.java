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
package org.exoplatform.ide.editor.javascript.client.codeassistant;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.javascript.client.codeassistant.ui.*;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaScriptTokenWidgetFactory Feb 24, 2011 11:54:32 AM evgen $
 */
public class JavaScriptTokenWidgetFactory implements TokenWidgetFactory {

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory#buildTokenWidget(org.exoplatform.ide.editor.api
     * .codeassitant.Token) */
    @Override
    public TokenWidget buildTokenWidget(Token token) {
        switch (token.getType()) {
            case KEYWORD:
                return new JsKeyWordWidget(token);

            case TEMPLATE:
                return new JsTemplateWidget(token);

            case CLASS:
                return new JsObjectWidget(token);

            case METHOD:
            case VARIABLE:
            case PROPERTY:
                return new JsWidget(token);

            case FUNCTION:
                return new JsFunctionWidget(token);

            default:
                return new JsKeyWordWidget(token);
        }

    }

}
