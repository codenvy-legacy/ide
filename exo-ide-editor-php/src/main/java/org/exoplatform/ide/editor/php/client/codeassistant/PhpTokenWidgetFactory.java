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
package org.exoplatform.ide.editor.php.client.codeassistant;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.php.client.codeassistant.ui.*;

/**
 * Factory of {@link TokenWidget}, need to build token UI representation.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class PhpTokenWidgetFactory implements TokenWidgetFactory {

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory#buildTokenWidget(org.exoplatform.ide.editor.api
     * .codeassitant.Token) */
    @Override
    public TokenWidget buildTokenWidget(Token token) {
        switch (token.getType()) {
            case KEYWORD:
                return new PhpTokenKeyWordWidget(token);

            case FUNCTION:
            case METHOD:
                return new PhpFunctionWidget(token);

            case PROPERTY:
                return new PhpPropertyWidget(token);

            case CONSTANT:
            case CLASS_CONSTANT:
                return new PhpConstantWidget(token);

            case PARAMETER:
            case VARIABLE:
            case LOCAL_VARIABLE:
                return new PhpVariableWidget(token);

            case CLASS:
            case INTERFACE:
                return new PhpClassWidget(token);

            default:
                return new PhpTokenKeyWordWidget(token);
        }

    }

}
