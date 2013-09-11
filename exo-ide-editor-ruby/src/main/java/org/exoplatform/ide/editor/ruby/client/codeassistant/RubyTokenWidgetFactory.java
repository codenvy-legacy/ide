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
package org.exoplatform.ide.editor.ruby.client.codeassistant;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.ruby.client.RubyClientBundle;
import org.exoplatform.ide.editor.ruby.client.codeassistant.ui.*;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RubyTokenWidgetFactory Apr 28, 2011 5:37:38 PM evgen $
 */
public class RubyTokenWidgetFactory implements TokenWidgetFactory {

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory#buildTokenWidget(org.exoplatform.ide.editor.api
     * .codeassitant.Token) */
    @Override
    public TokenWidget buildTokenWidget(Token token) {
        switch (token.getType()) {
            case KEYWORD:
                return new RubyKeyWordWidget(token);

            case METHOD:
                return new RubyMethodWidget(token);

            case CLASS:
                return new RubyClassWidget(token);

            case CONSTANT:
                return new RubyConstantWidget(token);

            case VARIABLE:
            case LOCAL_VARIABLE:
                return new RubyVariableWidget(token, RubyClientBundle.INSTANCE.variable());

            case INSTANCE_VARIABLE:
                return new RubyVariableWidget(token, RubyClientBundle.INSTANCE.rubyObjectVariable());

            case CLASS_VARIABLE:
                return new RubyVariableWidget(token, RubyClientBundle.INSTANCE.rubyClassVariable());

            case GLOBAL_VARIABLE:
                return new RubyVariableWidget(token, RubyClientBundle.INSTANCE.rubyGlobalVariable());

            default:
                return new RubyKeyWordWidget(token);
        }
    }

}
