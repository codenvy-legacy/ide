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
package org.exoplatform.ide.editor.javascript.client;

import org.exoplatform.ide.client.framework.editor.AbstractCommentsModifier;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 11, 2012 9:40:32 AM anya $
 */
public class JavaScriptCommentsModifier extends AbstractCommentsModifier {

    /** @see org.exoplatform.ide.client.framework.editor.AbstractCommentsModifier#getOpenBlockComment() */
    @Override
    public String getOpenBlockComment() {
        return "/*";
    }

    /** @see org.exoplatform.ide.client.framework.editor.AbstractCommentsModifier#getCloseBlockComment() */
    @Override
    public String getCloseBlockComment() {
        return "*/";
    }

    /** @see org.exoplatform.ide.client.framework.editor.AbstractCommentsModifier#getSingleLineComment() */
    @Override
    public String getSingleLineComment() {
        return "//";
    }
}
