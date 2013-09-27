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
package org.exoplatform.ide.editor.api.codeassitant.ui;

import org.exoplatform.ide.editor.api.codeassitant.Token;

/**
 * This interface describes factory that builds {@link TokenWidget} by specific token.<br>
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 25, 2010 3:59:50 PM evgen $
 */
public interface TokenWidgetFactory {

    /**
     * Create new {@link TokenWidget} for token.
     *
     * @param token
     * @return {@link TokenWidget} that represent token
     */
    TokenWidget buildTokenWidget(Token token);

}
